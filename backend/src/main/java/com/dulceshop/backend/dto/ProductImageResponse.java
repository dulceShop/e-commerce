package com.dulceshop.backend.dto;

import com.dulceshop.backend.entity.ProductImage;

public record ProductImageResponse(
        Long id,
        String url,
        boolean main
) {
    public static ProductImageResponse from(ProductImage image) {
        return new ProductImageResponse(image.getId(), image.getUrl(), image.isMain());
    }
}