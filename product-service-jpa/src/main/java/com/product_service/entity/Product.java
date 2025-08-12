package com.product_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@Entity
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

}
