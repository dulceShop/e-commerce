package com.dulceshop.backend.dto;

import com.dulceshop.backend.entity.Review;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        Long productId,
        String authorName,
        int rating,
        String comment,
        boolean approved,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getProduct().getId(),
                review.getAuthor().getFirstName(),
                review.getRating(),
                review.getComment(),
                review.isApproved(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
