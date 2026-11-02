package com.dulceshop.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SellerProfileRequest {

    @NotBlank(message = "Le nom de la boutique est obligatoire")
    @Size(max = 100, message = "Le nom de la boutique ne doit pas dépasser 100 caractères")
    private String shopName;

    @Size(max = 1000, message = "La description ne doit pas dépasser 1000 caractères")
    private String shopDescription;
}