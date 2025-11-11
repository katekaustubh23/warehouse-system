package com.product_service.entity.projection;

import java.math.BigDecimal;

public interface ProductListPageDTO {
    Long getId();
    String getName();
    BigDecimal getPrice();
    String getCategoryName();
    String getBrandName();
    String getColor();
    String getSize();
}
