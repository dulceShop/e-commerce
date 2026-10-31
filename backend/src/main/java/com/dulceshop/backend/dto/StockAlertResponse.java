package com.dulceshop.backend.dto;

import com.dulceshop.backend.entity.StockAlert;

import java.time.LocalDateTime;

public record StockAlertResponse(
        Long id,
        Long productId,
        String productName,
        String message,
        boolean read,
        LocalDateTime createdAt
) {
    public static StockAlertResponse from(StockAlert alert) {
        return new StockAlertResponse(
                alert.getId(),
                alert.getProduct().getId(),
                alert.getProduct().getName(),
                alert.getMessage(),
                alert.isRead(),
                alert.getCreatedAt());
    }
}