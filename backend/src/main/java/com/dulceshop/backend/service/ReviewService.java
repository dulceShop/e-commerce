package com.dulceshop.backend.service;

import com.dulceshop.backend.dto.PageResponse;
import com.dulceshop.backend.dto.ReviewRequest;
import com.dulceshop.backend.dto.ReviewResponse;
import com.dulceshop.backend.entity.Product;
import com.dulceshop.backend.entity.Review;
import com.dulceshop.backend.entity.Role;
import com.dulceshop.backend.entity.User;
import com.dulceshop.backend.exception.BadRequestException;
import com.dulceshop.backend.exception.ProductNotFoundException;
import com.dulceshop.backend.exception.ReviewAlreadyExistsException;
import com.dulceshop.backend.exception.ReviewNotFoundException;
import com.dulceshop.backend.repository.ProductRepository;
import com.dulceshop.backend.repository.ReviewRepository;
import com.dulceshop.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private static final int MAX_PAGE_SIZE = 50;

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReviewResponse create(String email, Long productId, ReviewRequest request) {
        User author = findCustomer(email);
        Product product = findActiveProduct(productId);

        if (reviewRepository.existsByProductIdAndAuthorId(productId, author.getId())) {
            throw new ReviewAlreadyExistsException(productId);
        }

        Review review = new Review();
        review.setProduct(product);
        review.setAuthor(author);
        review.setRating(request.rating());
        review.setComment(request.comment().trim());

        return ReviewResponse.from(reviewRepository.save(review));
    }

    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> findApprovedForProduct(
            Long productId,
            int page,
            int size
    ) {
        findActiveProduct(productId);

        return PageResponse.from(reviewRepository
                .findByProductIdAndApprovedTrue(productId, pageRequest(page, size))
                .map(ReviewResponse::from));
    }

    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> findMine(String email, int page, int size) {
        User author = findCustomer(email);

        return PageResponse.from(reviewRepository
                .findByAuthorId(author.getId(), pageRequest(page, size))
                .map(ReviewResponse::from));
    }

    @Transactional
    public ReviewResponse update(String email, Long reviewId, ReviewRequest request) {
        Review review = findOwned(reviewId, email);
        review.setRating(request.rating());
        review.setComment(request.comment().trim());
        review.setApproved(false);
        review.setUpdatedAt(LocalDateTime.now());

        return ReviewResponse.from(review);
    }

    @Transactional
    public void delete(String email, Long reviewId) {
        reviewRepository.delete(findOwned(reviewId, email));
    }

    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> findAll(int page, int size) {
        return PageResponse.from(reviewRepository.findAllBy(pageRequest(page, size))
                .map(ReviewResponse::from));
    }

    @Transactional
    public ReviewResponse setApproval(Long reviewId, boolean approved) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));
        review.setApproved(approved);
        review.setUpdatedAt(LocalDateTime.now());

        return ReviewResponse.from(review);
    }

    private User findCustomer(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException(
                        "Authenticated user not found: " + email));

        if (user.getRole() != Role.CUSTOMER) {
            throw new BadRequestException("Only customers can manage reviews");
        }

        return user;
    }

    private Product findActiveProduct(Long productId) {
        return productRepository.findByIdAndActiveTrue(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    private Review findOwned(Long reviewId, String email) {
        return reviewRepository.findByIdAndAuthorEmail(reviewId, email)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));
    }

    private PageRequest pageRequest(int page, int size) {
        if (page < 0 || size < 1) {
            throw new BadRequestException("page must be >= 0 and size must be >= 1");
        }

        return PageRequest.of(
                page,
                Math.min(size, MAX_PAGE_SIZE),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
    }
}
