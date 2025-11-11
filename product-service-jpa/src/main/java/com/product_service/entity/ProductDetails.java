package com.product_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "product_details", schema = "product_schema")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String color;
    private String size;
    private Double weight;

    @OneToOne()
    @JoinColumn(name="product_id", unique = true)
    private Product product;
}
