package com.dulceshop.backend.dto;

import jakarta.validation.constraints.NotNull;

public record ReviewApprovalRequest(
        @NotNull(message = "approved is required")
        Boolean approved
) {
}
