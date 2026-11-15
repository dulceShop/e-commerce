package com.dulceshop.backend.service;

import com.dulceshop.backend.entity.Product;
import com.dulceshop.backend.entity.StockAlert;
import com.dulceshop.backend.repository.StockAlertRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockAlertServiceTest {

    @Mock
    private StockAlertRepository stockAlertRepository;

    @InjectMocks
    private StockAlertService stockAlertService;

    @Test
    void checkStock_createsAlert_whenStockIsAtThreshold() {
        Product product = product(6L, "Tasse", 2, 2);

        when(stockAlertRepository.existsByProductIdAndReadFalse(6L))
                .thenReturn(false);

        stockAlertService.checkStock(product);

        ArgumentCaptor<StockAlert> alertCaptor =
                ArgumentCaptor.forClass(StockAlert.class);

        verify(stockAlertRepository).save(alertCaptor.capture());

        StockAlert alert = alertCaptor.getValue();
        assertEquals(product, alert.getProduct());
        assertEquals(
                "Low stock for 'Tasse': 2 left (threshold 2)",
                alert.getMessage()
        );
    }

    @Test
    void checkStock_doesNotCreateDuplicate_whenUnreadAlertAlreadyExists() {
        Product product = product(6L, "Tasse", 1, 2);

        when(stockAlertRepository.existsByProductIdAndReadFalse(6L))
                .thenReturn(true);

        stockAlertService.checkStock(product);

        verify(stockAlertRepository, never()).save(any(StockAlert.class));
    }

    private Product product(
            Long id,
            String name,
            int stockQuantity,
            int threshold
    ) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setStockQuantity(stockQuantity);
        product.setLowStockThreshold(threshold);
        return product;
    }
}