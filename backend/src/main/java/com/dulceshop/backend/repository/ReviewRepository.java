package com.dulceshop.backend.repository;

import com.dulceshop.backend.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByProductIdAndAuthorId(Long productId, Long authorId);

    @EntityGraph(attributePaths = {"product", "author"})
    Page<Review> findByProductIdAndApprovedTrue(Long productId, Pageable pageable);

    @EntityGraph(attributePaths = {"product", "author"})
    Page<Review> findByAuthorId(Long authorId, Pageable pageable);

    @EntityGraph(attributePaths = {"product", "author"})
    Optional<Review> findByIdAndAuthorEmail(Long id, String email);

    @EntityGraph(attributePaths = {"product", "author"})
    Page<Review> findAllBy(Pageable pageable);
}
