package com.dulceshop.backend.service;

import com.dulceshop.backend.entity.Product;
import com.dulceshop.backend.exception.BadRequestException;
import com.dulceshop.backend.repository.ProductRepository;
import com.dulceshop.backend.repository.SellerProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private SellerProfileRepository sellerProfileRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void search_throwsException_whenMinPriceIsGreaterThanMaxPrice() {
        assertThrows(
                BadRequestException.class,
                () -> productService.search(
                        null,
                        null,
                        new BigDecimal("50.00"),
                        new BigDecimal("10.00"),
                        0,
                        12,
                        null
                )
        );

        verifyNoInteractions(productRepository, sellerProfileRepository);
    }

    @Test
    void search_capsPageSizeAndUsesRequestedSort() {
        when(productRepository.search(
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                any(Pageable.class)
        )).thenReturn(new PageImpl<>(
        List.of(),
        PageRequest.of(2, 50),
        0
));

        var response = productService.search(
                "   ",
                null,
                null,
                null,
                2,
                100,
                "price,asc"
        );

        ArgumentCaptor<Pageable> pageableCaptor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(productRepository).search(
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                pageableCaptor.capture()
        );

        Pageable pageable = pageableCaptor.getValue();
        Sort.Order priceOrder = pageable.getSort().getOrderFor("price");

        assertEquals(2, pageable.getPageNumber());
        assertEquals(50, pageable.getPageSize());
        assertNotNull(priceOrder);
        assertEquals(Sort.Direction.ASC, priceOrder.getDirection());

        assertEquals(2, response.page());
        assertEquals(50, response.size());
        assertEquals(0, response.totalElements());
        assertTrue(response.content().isEmpty());
    }

    @Test
    void search_throwsException_whenSortFieldIsNotAllowed() {
        assertThrows(
                BadRequestException.class,
                () -> productService.search(
                        null,
                        null,
                        null,
                        null,
                        0,
                        12,
                        "sellerEmail,asc"
                )
        );

        verifyNoInteractions(productRepository, sellerProfileRepository);
    }
}