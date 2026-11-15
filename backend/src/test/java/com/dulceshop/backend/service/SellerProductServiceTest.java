package com.dulceshop.backend.service;

import com.dulceshop.backend.entity.Product;
import com.dulceshop.backend.entity.User;
import com.dulceshop.backend.exception.ProductNotFoundException;
import com.dulceshop.backend.repository.CategoryRepository;
import com.dulceshop.backend.repository.ProductRepository;
import com.dulceshop.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StockAlertService stockAlertService;

    @InjectMocks
    private SellerProductService sellerProductService;

    @Test
    void findOwned_throwsNotFound_whenProductDoesNotBelongToSeller() {
        User seller = seller();

        when(userRepository.findByEmail("seller@example.com"))
                .thenReturn(Optional.of(seller));
        when(productRepository.findByIdAndSellerId(6L, 2L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> sellerProductService.findOwned(
                        "seller@example.com",
                        false,
                        6L
                )
        );
    }

    @Test
    void deactivate_setsProductAsInactive_whenSellerOwnsProduct() {
        User seller = seller();

        Product product = new Product();
        product.setId(6L);
        product.setActive(true);

        when(userRepository.findByEmail("seller@example.com"))
                .thenReturn(Optional.of(seller));
        when(productRepository.findByIdAndSellerId(6L, 2L))
                .thenReturn(Optional.of(product));

        sellerProductService.deactivate(
                "seller@example.com",
                false,
                6L
        );

        assertFalse(product.isActive());
        verify(productRepository).findByIdAndSellerId(6L, 2L);
        verifyNoInteractions(categoryRepository, stockAlertService);
    }

    private User seller() {
        User seller = new User();
        seller.setId(2L);
        seller.setEmail("seller@example.com");
        return seller;
    }
}