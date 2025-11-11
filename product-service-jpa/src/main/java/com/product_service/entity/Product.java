package com.product_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="products", schema = "product_schema")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private BigDecimal price;

    @Column(name = "available_quantity")
    private int availableQuantity;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(name="category_id")
    private Integer categoryId;

    @Column(name="brand_id")
    private Integer brandId;
// removed relationship from entity class for  the improvement performance not to load every time this objects
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name="category_id") owned side always use joinColumn
//    private ProductCategory category;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name="brand_id")
//    private Brand brand;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private ProductDetails productDetails;

    @OneToMany(mappedBy = "productId")
    private List<ProductReview> productReview;

}
