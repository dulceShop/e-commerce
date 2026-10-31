package com.dulceshop.backend.dto;

import com.dulceshop.backend.entity.Role;
import com.dulceshop.backend.entity.User;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        Role role
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole()
        );
    }
}