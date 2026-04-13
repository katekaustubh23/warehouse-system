package com.warehouse.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockReservedEventDto {
    private Long orderId;
    private Long productId;
    private int quantity;
    private String status; // "RESERVED" or "REJECTED"
}
