package com.dulceshop.backend.dto;
import com.dulceshop.backend.entity.Role;

public record AuthResponse (
    String token,
    String tokenType,
    String email,
    Role role
){
    
}
