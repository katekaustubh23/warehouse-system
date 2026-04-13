package com.warehouse.inventory.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InventoryReserved {

    private Integer id;

    private Long orderId;
    private Long productId;
    private int quantity;
    private String status;
    private LocalDateTime expiredAt;
}
