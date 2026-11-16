package com.dulceshop.backend.controller;

import com.dulceshop.backend.config.SecurityConfig;
import com.dulceshop.backend.dto.PageResponse;
import com.dulceshop.backend.dto.ProductResponse;
import com.dulceshop.backend.exception.ProductNotFoundException;
import com.dulceshop.backend.repository.UserRepository;
import com.dulceshop.backend.service.JwtService;
import com.dulceshop.backend.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(ProductController.class)
@Import(SecurityConfig.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void search_returnsOk_whenVisitorAccessesPublicCatalog() throws Exception {
        when(productService.search(
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(0),
                eq(12),
                isNull()
        )).thenReturn(new PageResponse<>(
                List.<ProductResponse>of(),
                0,
                12,
                0,
                0
        ));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(12))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void findById_returnsNotFound_whenProductDoesNotExist() throws Exception {
        when(productService.findById(99L))
                .thenThrow(new ProductNotFoundException(99L));

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Product not found: 99"));
    }
}