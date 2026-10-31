package com.dulceshop.backend.repository;

import com.dulceshop.backend.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(attributePaths = {"category", "seller"})
    @Query(value = """
            select p from Product p
            where p.active = true
              and (:categoryId is null or p.category.id = :categoryId)
              and (:minPrice is null or p.price >= :minPrice)
              and (:maxPrice is null or p.price <= :maxPrice)
              and (:q is null or lower(p.name) like lower(concat('%', :q, '%')))
            """,
            countQuery = """
            select count(p) from Product p
            where p.active = true
              and (:categoryId is null or p.category.id = :categoryId)
              and (:minPrice is null or p.price >= :minPrice)
              and (:maxPrice is null or p.price <= :maxPrice)
              and (:q is null or lower(p.name) like lower(concat('%', :q, '%')))
            """)
    Page<Product> search(@Param("categoryId") Long categoryId,
                         @Param("minPrice") BigDecimal minPrice,
                         @Param("maxPrice") BigDecimal maxPrice,
                         @Param("q") String q,
                         Pageable pageable);

    @EntityGraph(attributePaths = {"category", "seller"})
    Optional<Product> findByIdAndActiveTrue(Long id);
        @EntityGraph(attributePaths = {"category", "seller"})
    Page<Product> findBySellerId(Long sellerId, Pageable pageable);
        @EntityGraph(attributePaths = {"category", "seller"})
    Optional<Product> findByIdAndSellerId(Long id, Long sellerId);
}