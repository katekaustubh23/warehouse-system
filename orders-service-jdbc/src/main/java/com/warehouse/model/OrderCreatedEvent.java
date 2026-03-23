package com.warehouse.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderCreatedEvent {
    private Long orderId;
//    private Long userId;
    private List<OrderItem> items;
//    private String requestId; // UUID as String
//    private LocalDateTime createdAt;
}
