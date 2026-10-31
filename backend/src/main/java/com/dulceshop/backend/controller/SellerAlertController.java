package com.dulceshop.backend.controller;
import com.dulceshop.backend.dto.StockAlertResponse;
import com.dulceshop.backend.service.StockAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
@RestController 
@RequestMapping("/api/seller/alerts")
@RequiredArgsConstructor 
public class SellerAlertController {
    private final StockAlertService stockAlertService;
    @GetMapping
    public List<StockAlertResponse> findMine(Authentication authentication){
        return stockAlertService.findForSeller(authentication.getName());
    }
        @PatchMapping("/{id}/read")
    public StockAlertResponse markAsRead(Authentication authentication, @PathVariable Long id) {
        return stockAlertService.markAsRead(authentication.getName(), id);
    }
}
