package com.dulceshop.backend.controller;

import com.dulceshop.backend.dto.AuthResponse;
import com.dulceshop.backend.dto.LoginRequest;
import com.dulceshop.backend.dto.RegisterRequest;
import com.dulceshop.backend.dto.UserResponse;
import com.dulceshop.backend.entity.User;
import com.dulceshop.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Authentication",
        description = "Customer registration and JWT authentication"
)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new customer")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request);
        return UserResponse.from(user);
    }

    @Operation(summary = "Authenticate and return a JWT token")
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}