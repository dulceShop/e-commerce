package com.dulceshop.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProductImageRequest(

        @NotBlank @Size(max = 500)
        @Pattern(regexp = "^https?://.+", message = "must be an http or https URL")
        String url,

        Boolean main
) {
}