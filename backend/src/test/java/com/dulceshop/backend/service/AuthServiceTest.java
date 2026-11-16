package com.dulceshop.backend.service;

import com.dulceshop.backend.dto.AuthResponse;
import com.dulceshop.backend.dto.LoginRequest;
import com.dulceshop.backend.dto.RegisterRequest;
import com.dulceshop.backend.entity.Role;
import com.dulceshop.backend.entity.User;
import com.dulceshop.backend.exception.EmailAlreadyUsedException;
import com.dulceshop.backend.exception.InvalidCredentialsException;
import com.dulceshop.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_throwsException_whenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest(
                "Client",
                "Test",
                "client@example.com",
                "Password123",
                "0600000000"
        );

        when(userRepository.existsByEmail("client@example.com")).thenReturn(true);

        assertThrows(
                EmailAlreadyUsedException.class,
                () -> authService.register(request)
        );

        verify(userRepository).existsByEmail("client@example.com");
        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void login_returnsToken_whenCredentialsAreValid() {
        User user = new User();
        user.setEmail("client@example.com");
        user.setPasswordHash("encoded-password");
        user.setRole(Role.CUSTOMER);
        user.setActive(true);

        LoginRequest request = new LoginRequest(
                "client@example.com",
                "Password123"
        );

        when(userRepository.findByEmail("client@example.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password123", "encoded-password"))
                .thenReturn(true);
        when(jwtService.generateToken("client@example.com"))
                .thenReturn("fake-jwt-token");

        AuthResponse response = authService.login(request);

        assertEquals("fake-jwt-token", response.token());
        assertEquals("Bearer", response.tokenType());
        assertEquals("client@example.com", response.email());
        assertEquals(Role.CUSTOMER, response.role());

        verify(jwtService).generateToken("client@example.com");
    }

    @Test
    void login_throwsException_whenPasswordIsInvalid() {
        User user = new User();
        user.setEmail("client@example.com");
        user.setPasswordHash("encoded-password");
        user.setActive(true);

        LoginRequest request = new LoginRequest(
                "client@example.com",
                "wrong-password"
        );

        when(userRepository.findByEmail("client@example.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "encoded-password"))
                .thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        verify(jwtService, never()).generateToken(anyString());
    }
}