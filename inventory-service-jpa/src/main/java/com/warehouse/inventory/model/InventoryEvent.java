package com.warehouse.inventory.model;

import lombok.Data;

@Data
public class InventoryEvent {
    private String eventType;  // e.g. ORDER_CREATED, INVENTORY_ALLOCATED, INVENTORY_FAILED
    private Long orderId;
    private Long productId;
    private int quantity;
}
