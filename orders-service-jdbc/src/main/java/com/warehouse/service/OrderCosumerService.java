package com.warehouse.service;

import com.warehouse.model.StockReservedEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCosumerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderService orderService;

    @KafkaListener(topics = {"stock-reserved", "stock-rejected"}, groupId = "orders-group")
        public void consumeInventoryResponse(StockReservedEventDto event) {
            System.out.println("Received message from inventory-service: " + event.toString());
            // Process the message and update order status accordingly
//            if (message.startsWith("StockReserved:")) {
//                String orderId = message.split(":")[1];
//                System.out.println("Order " + orderId + " is reserved. Updating order status to 'Confirmed'.");
//                // Update order status in database (not implemented here)
//            } else if (message.startsWith("StockRejected:")) {
//                String orderId = message.split(":")[1];
//                System.out.println("Order " + orderId + " is rejected. Updating order status to 'Cancelled'.");
//                // Update order status in database (not implemented here)
//            }
        }

    @KafkaListener(topics = {"order-expired"}, groupId = "orders-group")
    public void consumeExpiredOrder(StockReservedEventDto event) {
        log.info("Received message from inventory-service order-expired or cancel: " + event.toString());
        // Process the message and update order status accordingly
        orderService.updateStatus(event.getOrderId(), "EXPIRED");

    }
}
