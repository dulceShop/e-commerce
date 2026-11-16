package com.dulceshop.backend.controller;

import com.dulceshop.backend.config.SecurityConfig;
import com.dulceshop.backend.dto.ReviewRequest;
import com.dulceshop.backend.dto.ReviewResponse;
import com.dulceshop.backend.repository.UserRepository;
import com.dulceshop.backend.service.JwtService;
import com.dulceshop.backend.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(ReviewController.class)
@Import(SecurityConfig.class)
class ReviewControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void create_returnsUnauthorized_whenNoUserIsAuthenticated() throws Exception {
        mockMvc.perform(post("/api/products/6/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "rating": 5,
                                  "comment": "Très bon produit"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "SELLER")
    void create_returnsForbidden_whenSellerCreatesReview() throws Exception {
        mockMvc.perform(post("/api/products/6/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "rating": 5,
                                  "comment": "Très bon produit"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "client@example.com", roles = "CUSTOMER")
    void create_returnsCreated_whenCustomerCreatesReview() throws Exception {
        ReviewResponse response = new ReviewResponse(
                1L,
                6L,
                "Client",
                5,
                "Très bon produit",
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(reviewService.create(
                eq("client@example.com"),
                eq(6L),
                any(ReviewRequest.class)
        )).thenReturn(response);

        mockMvc.perform(post("/api/products/6/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "rating": 5,
                                  "comment": "Très bon produit"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.approved").value(false));
    }
}