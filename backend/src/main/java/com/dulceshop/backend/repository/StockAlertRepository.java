package com.dulceshop.backend.repository;
import com.dulceshop.backend.entity.StockAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface StockAlertRepository  extends JpaRepository<StockAlert,Long> {
    boolean existsByProductIdAndReadFalse(Long productId);
        Optional<StockAlert> findByIdAndProductSellerEmail(Long id, String email);
    List<StockAlert> findByProductSellerEmailOrderByCreatedAtDesc(String email);
    
}
