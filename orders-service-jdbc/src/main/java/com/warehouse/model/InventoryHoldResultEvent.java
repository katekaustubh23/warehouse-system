package com.warehouse.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class InventoryHoldResultEvent {
    public static final String HOLD_SUCCESS = "HOLD_SUCCESS";
    public static final String HOLD_FAILED = "HOLD_FAILED";

    private Long orderId;
    private String status; // HOLD_SUCCESS or HOLD_FAILED
    private String reason; // optional
    private String requestId;
    private LocalDateTime eventAt;

    // When success, inventory may send per-item allocation with warehouse location
    private List<ItemAllocation> allocations;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemAllocation {
        private Long itemId;   // this can map to order_item.item_id if shared
        private Long productId;
        private Integer quantity;
        private Long warehouseId;   // optional assigned warehouse id
        private Double latitude;    // optional
        private Double longitude;

        // constructors, getters/setters...
    }
}
