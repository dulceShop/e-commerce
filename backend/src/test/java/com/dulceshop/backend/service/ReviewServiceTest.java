package com.dulceshop.backend.service;

import com.dulceshop.backend.dto.ReviewRequest;
import com.dulceshop.backend.entity.Product;
import com.dulceshop.backend.entity.Review;
import com.dulceshop.backend.entity.Role;
import com.dulceshop.backend.entity.User;
import com.dulceshop.backend.exception.BadRequestException;
import com.dulceshop.backend.exception.ReviewAlreadyExistsException;
import com.dulceshop.backend.repository.ProductRepository;
import com.dulceshop.backend.repository.ReviewRepository;
import com.dulceshop.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void create_throwsException_whenCustomerAlreadyReviewedProduct() {
        User customer = customer();
        Product product = product();

        when(userRepository.findByEmail("client@example.com"))
                .thenReturn(Optional.of(customer));
        when(productRepository.findByIdAndActiveTrue(6L))
                .thenReturn(Optional.of(product));
        when(reviewRepository.existsByProductIdAndAuthorId(6L, 1L))
                .thenReturn(true);

        assertThrows(
                ReviewAlreadyExistsException.class,
                () -> reviewService.create(
                        "client@example.com",
                        6L,
                        new ReviewRequest(5, "Très bon produit")
                )
        );

        verify(reviewRepository, never()).save(any());
    }

    @Test
    void create_throwsException_whenUserIsNotCustomer() {
        User seller = customer();
        seller.setRole(Role.SELLER);

        when(userRepository.findByEmail("client@example.com"))
                .thenReturn(Optional.of(seller));

        assertThrows(
                BadRequestException.class,
                () -> reviewService.create(
                        "client@example.com",
                        6L,
                        new ReviewRequest(5, "Très bon produit")
                )
        );

        verifyNoInteractions(productRepository, reviewRepository);
    }

    @Test
    void update_setsApprovedToFalse_whenCustomerEditsReview() {
        User customer = customer();
        Product product = product();

        Review review = new Review();
        review.setId(10L);
        review.setAuthor(customer);
        review.setProduct(product);
        review.setRating(5);
        review.setComment("Ancien commentaire");
        review.setApproved(true);

        when(reviewRepository.findByIdAndAuthorEmail(10L, "client@example.com"))
                .thenReturn(Optional.of(review));

        var response = reviewService.update(
                "client@example.com",
                10L,
                new ReviewRequest(4, "  Commentaire modifié  ")
        );

        assertEquals(4, response.rating());
        assertEquals("Commentaire modifié", response.comment());
        assertFalse(response.approved());
        assertFalse(review.isApproved());
    }

    private User customer() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("Client");
        user.setEmail("client@example.com");
        user.setRole(Role.CUSTOMER);
        user.setActive(true);
        return user;
    }

    private Product product() {
        Product product = new Product();
        product.setId(6L);
        product.setActive(true);
        return product;
    }
}