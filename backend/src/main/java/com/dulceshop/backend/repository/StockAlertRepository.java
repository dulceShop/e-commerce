package com.dulceshop.backend.repository;
import com.dulceshop.backend.entity.StockAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface StockAlertRepository  extends JpaRepository<StockAlert,Long> {
    boolean existsByProductIdAndReadFalse(Long productId);
    List<StockAlert> findByProductSellerEmailOrderByCreatedAtDesc(String email);
    
}
