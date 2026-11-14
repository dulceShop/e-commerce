package com.dulceshop.backend.controller;

import com.dulceshop.backend.dto.PageResponse;
import com.dulceshop.backend.dto.ReviewApprovalRequest;
import com.dulceshop.backend.dto.ReviewResponse;
import com.dulceshop.backend.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public PageResponse<ReviewResponse> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return reviewService.findAll(page, size);
    }

    @PatchMapping("/{id}/approval")
    public ReviewResponse setApproval(
            @PathVariable Long id,
            @Valid @RequestBody ReviewApprovalRequest request
    ) {
        return reviewService.setApproval(id, request.approved());
    }
}
