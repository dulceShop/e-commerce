package com.dulceshop.backend.controller;

import com.dulceshop.backend.config.SecurityConfig;
import com.dulceshop.backend.dto.AuthResponse;
import com.dulceshop.backend.dto.LoginRequest;
import com.dulceshop.backend.dto.RegisterRequest;
import com.dulceshop.backend.entity.Role;
import com.dulceshop.backend.entity.User;
import com.dulceshop.backend.repository.UserRepository;
import com.dulceshop.backend.service.AuthService;
import com.dulceshop.backend.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void register_returnsCreated_whenRequestIsValid() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setFirstName("Client");
        user.setLastName("Test");
        user.setEmail("client@example.com");
        user.setRole(Role.CUSTOMER);

        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(user);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Client",
                                  "lastName": "Test",
                                  "email": "client@example.com",
                                  "password": "Password123",
                                  "phone": "0600000000"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("client@example.com"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    void register_returnsBadRequest_whenRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "",
                                  "lastName": "Test",
                                  "email": "not-an-email",
                                  "password": "short"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));

        verifyNoInteractions(authService);
    }

    @Test
    void login_returnsOk_whenCredentialsAreValid() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(new AuthResponse(
                        "fake-jwt-token",
                        "Bearer",
                        "client@example.com",
                        Role.CUSTOMER
                ));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "client@example.com",
                                  "password": "Password123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }
}