package com.dulceshop.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReviewRequest(
        @Min(value = 1, message = "rating must be between 1 and 5")
        @Max(value = 5, message = "rating must be between 1 and 5")
        int rating,

        @NotBlank(message = "comment is required")
        @Size(max = 1000, message = "comment must not exceed 1000 characters")
        String comment
) {
}
