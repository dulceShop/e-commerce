package com.dulceshop.backend.entity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name="products")
@Getter
@Setter
@NoArgsConstructor

public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable =false,length=200)
    private String name;
    @Column(length=2000)
    private String description;
    @Column(nullable = false, precision=10,scale=2)
    private BigDecimal price;
    @Column(nullable=false)
    private int stockQuantity;
    @Column(nullable = false)
    private int lowStockThreshold=5;
    @Column(nullable=false)
    private boolean active=true;
    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;
    @ManyToOne (fetch=FetchType.LAZY)
    @JoinColumn (name="category_id")
    private Category category;
    @OneToMany(mappedBy = "product",cascade=CascadeType.ALL, orphanRemoval=true)
    private List<ProductImage> images =new ArrayList<>();
    @PrePersist 
    void onCreate(){
        this.createdAt=LocalDateTime.now();
    }
    public void addImage(ProductImage image){
        images.add(image);
        image.setProduct(this);
    }


    
}
