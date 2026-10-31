package com.dulceshop.backend.service;

import com.dulceshop.backend.dto.StockAlertResponse;
import com.dulceshop.backend.entity.Product;
import com.dulceshop.backend.entity.StockAlert;
import com.dulceshop.backend.repository.StockAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dulceshop.backend.exception.AlertNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockAlertService {

    private final StockAlertRepository stockAlertRepository;

    @Transactional
    public void checkStock(Product product) {
        if (product.getStockQuantity() > product.getLowStockThreshold()) {
            return;
        }
        if (stockAlertRepository.existsByProductIdAndReadFalse(product.getId())) {
            return;
        }

        StockAlert alert = new StockAlert();
        alert.setProduct(product);
        alert.setMessage("Low stock for '" + product.getName() + "': "
                + product.getStockQuantity() + " left (threshold "
                + product.getLowStockThreshold() + ")");
        stockAlertRepository.save(alert);
    }

    @Transactional(readOnly = true)
    public List<StockAlertResponse> findForSeller(String sellerEmail) {
        return stockAlertRepository.findByProductSellerEmailOrderByCreatedAtDesc(sellerEmail).stream()
                .map(StockAlertResponse::from)
                .toList();
    }
        @Transactional
    public StockAlertResponse markAsRead(String sellerEmail, Long alertId) {
        StockAlert alert = stockAlertRepository.findByIdAndProductSellerEmail(alertId, sellerEmail)
                .orElseThrow(() -> new AlertNotFoundException(alertId));
        alert.setRead(true);
        return StockAlertResponse.from(alert);
    }
}