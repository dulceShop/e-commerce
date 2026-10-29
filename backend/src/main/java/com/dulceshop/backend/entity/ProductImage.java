package com.dulceshop.backend.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity 
@Table(name="product_images")
@Getter 
@Setter 
@NoArgsConstructor 
public class ProductImage {
    @Id 
    @GeneratedValue (strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length=500)
    private String url;
    @Column(name="is_main",nullable = false)
    private boolean main;
    @ManyToOne (fetch=FetchType.LAZY,optional=false)
    @JoinColumn(name="product_id",nullable=false)
    private Product product;

    
}
