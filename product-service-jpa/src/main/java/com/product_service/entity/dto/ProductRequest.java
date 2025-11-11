package com.product_service.entity.dto;

import com.product_service.entity.ProductDetails;
import com.product_service.entity.ProductReview;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private String sku;
    private Integer categoryId;
    private Integer brandId;

    private ProductDetails details;
    private List<ProductReview> reviews;
}
