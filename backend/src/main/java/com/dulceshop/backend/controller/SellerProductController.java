package com.dulceshop.backend.controller;

import com.dulceshop.backend.dto.PageResponse;
import com.dulceshop.backend.dto.ProductRequest;
import com.dulceshop.backend.dto.ProductResponse;
import com.dulceshop.backend.service.SellerProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import com.dulceshop.backend.dto.StockUpdateRequest;
import org.springframework.web.bind.annotation.PatchMapping;
@RestController
@RequestMapping("/api/seller/products")
@RequiredArgsConstructor
public class SellerProductController {

    private final SellerProductService sellerProductService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(Authentication authentication,
                                  @Valid @RequestBody ProductRequest request) {
        return sellerProductService.create(authentication.getName(), request);
    }

    @GetMapping
    public PageResponse<ProductResponse> findMine(Authentication authentication,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "12") int size) {
        return sellerProductService.findMine(authentication.getName(), page, size);
    }
        @PutMapping("/{id}")
    public ProductResponse update(Authentication authentication,
                                  @PathVariable Long id,
                                  @Valid @RequestBody ProductRequest request) {
        return sellerProductService.update(
                authentication.getName(), isAdmin(authentication), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(Authentication authentication, @PathVariable Long id) {
        sellerProductService.deactivate(authentication.getName(), isAdmin(authentication), id);
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
        @PatchMapping("/{id}/stock")
    public ProductResponse updateStock(Authentication authentication,
                                       @PathVariable Long id,
                                       @Valid @RequestBody StockUpdateRequest request) {
        return sellerProductService.updateStock(
                authentication.getName(), isAdmin(authentication), id, request.stockQuantity());
    }
}