package com.dulceshop.backend.controller;

import com.dulceshop.backend.dto.SellerProfileRequest;
import com.dulceshop.backend.dto.SellerProfileResponse;
import com.dulceshop.backend.service.SellerProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/seller/profile")
@RequiredArgsConstructor
public class SellerOwnProfileController {

    private final SellerProfileService sellerProfileService;

    @PutMapping
    public SellerProfileResponse update(Authentication authentication,
                                        @Valid @RequestBody SellerProfileRequest request) {
        return sellerProfileService.updateMine(authentication.getName(), request);
    }
}