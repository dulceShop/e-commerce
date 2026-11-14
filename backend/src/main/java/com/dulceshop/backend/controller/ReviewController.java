package com.dulceshop.backend.controller;

import com.dulceshop.backend.dto.PageResponse;
import com.dulceshop.backend.dto.ReviewRequest;
import com.dulceshop.backend.dto.ReviewResponse;
import com.dulceshop.backend.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/api/products/{productId}/reviews")
    public PageResponse<ReviewResponse> findApprovedForProduct(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return reviewService.findApprovedForProduct(productId, page, size);
    }

    @PostMapping("/api/products/{productId}/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponse create(
            Authentication authentication,
            @PathVariable Long productId,
            @Valid @RequestBody ReviewRequest request
    ) {
        return reviewService.create(authentication.getName(), productId, request);
    }

    @GetMapping("/api/reviews/me")
    public PageResponse<ReviewResponse> findMine(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return reviewService.findMine(authentication.getName(), page, size);
    }

    @PutMapping("/api/reviews/{id}")
    public ReviewResponse update(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequest request
    ) {
        return reviewService.update(authentication.getName(), id, request);
    }

    @DeleteMapping("/api/reviews/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(Authentication authentication, @PathVariable Long id) {
        reviewService.delete(authentication.getName(), id);
    }
}
