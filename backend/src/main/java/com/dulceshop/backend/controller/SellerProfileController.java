package com.dulceshop.backend.controller;

import com.dulceshop.backend.dto.SellerProfileRequest;
import com.dulceshop.backend.dto.SellerProfileResponse;
import com.dulceshop.backend.service.SellerProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seller-profiles")
@RequiredArgsConstructor
public class SellerProfileController {

    private final SellerProfileService sellerProfileService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SellerProfileResponse create(
            Authentication authentication,
            @Valid @RequestBody SellerProfileRequest request
    ) {
        return sellerProfileService.create(authentication.getName(), request);
    }

    @GetMapping("/me")
    public SellerProfileResponse getMine(Authentication authentication) {
        return sellerProfileService.getMine(authentication.getName());
    }

    @GetMapping("/{id}")
    public SellerProfileResponse getById(@PathVariable Long id) {
        return sellerProfileService.getById(id);
    }

    @GetMapping
    public List<SellerProfileResponse> getAll(
            @RequestParam(required = false) Boolean approved
    ) {
        return sellerProfileService.getAll(approved);
    }

    @PatchMapping("/{id}/approve")
    public SellerProfileResponse approve(@PathVariable Long id) {
        return sellerProfileService.approve(id);
    }
}