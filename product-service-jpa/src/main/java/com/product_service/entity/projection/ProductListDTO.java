package com.product_service.entity.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


public record ProductListDTO (
    Long id,
    String name,
    BigDecimal price,
    String color,
    String size,
    String category,
    String brand
){}

