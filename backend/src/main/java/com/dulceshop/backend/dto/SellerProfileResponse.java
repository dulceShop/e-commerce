package com.dulceshop.backend.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor 
public class SellerProfileResponse {
    private Long id;
    private Long userId;
    private String shopName;
    private String shopDescription;
    private boolean approved;
}
