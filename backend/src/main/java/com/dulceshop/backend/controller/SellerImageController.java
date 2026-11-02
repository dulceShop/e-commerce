package com.dulceshop.backend.controller;

import com.dulceshop.backend.dto.ProductImageRequest;
import com.dulceshop.backend.dto.ProductImageResponse;
import com.dulceshop.backend.service.SellerImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/seller/products/{productId}/images")
@RequiredArgsConstructor
public class SellerImageController {

    private final SellerImageService sellerImageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductImageResponse add(Authentication authentication,
                                    @PathVariable Long productId,
                                    @Valid @RequestBody ProductImageRequest request) {
        return sellerImageService.add(authentication.getName(), isAdmin(authentication), productId, request);
    }

    @PatchMapping("/{imageId}/main")
    public List<ProductImageResponse> setMain(Authentication authentication,
                                              @PathVariable Long productId,
                                              @PathVariable Long imageId) {
        return sellerImageService.setMain(authentication.getName(), isAdmin(authentication), productId, imageId);
    }

    @DeleteMapping("/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(Authentication authentication,
                       @PathVariable Long productId,
                       @PathVariable Long imageId) {
        sellerImageService.delete(authentication.getName(), isAdmin(authentication), productId, imageId);
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}