package com.dulceshop.backend.dto;

import com.dulceshop.backend.entity.Product;
import com.dulceshop.backend.entity.ProductImage;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        int stockQuantity,
        boolean inStock,
        Long categoryId,
        String categoryName,
        String sellerName,
        String mainImageUrl,
        LocalDateTime createdAt
) {
    public static ProductResponse from(Product product) {
        var category = product.getCategory();

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getStockQuantity() > 0,
                category != null ? category.getId() : null,
                category != null ? category.getName() : null,
                product.getSeller().getFirstName(),
                mainImageUrl(product),
                product.getCreatedAt()
        );
    }

    private static String mainImageUrl(Product product) {
        return product.getImages().stream()
                .filter(ProductImage::isMain)
                .findFirst()
                .or(() -> product.getImages().stream().findFirst())
                .map(ProductImage::getUrl)
                .orElse(null);
    }
}