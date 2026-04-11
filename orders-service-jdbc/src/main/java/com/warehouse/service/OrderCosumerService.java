package com.warehouse.service;

import com.warehouse.constant.OrderStatus;
import com.warehouse.dao.OutBoxDAO;
import com.warehouse.model.OrderConfirmEventDto;
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

    private final OrderService orderService;
    private final OutBoxDAO outBoxDAO;

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
        orderService.updateStatus(event.getOrderId(), OrderStatus.EXPIRED.name());

    }

    @KafkaListener(topics = {"confirm-success"}, groupId = "orders-group")
    public void consumeConfirmSuccess(OrderConfirmEventDto event) {
        log.info("Received message from inventory-service order-expired or cancel: " + event.toString());
        // Process the message and update order status accordingly
        orderService.updateStatus(event.getOrderId(), OrderStatus.CONFIRM_SUCCESS.name());
        outBoxDAO.updateOutBox(event.getOrderId(), OrderStatus.CONFIRM_SUCCESS.name());
    }

    @KafkaListener(topics = {"confirm-failed"}, groupId = "orders-group")
    public void consumeConfirmFailed(OrderConfirmEventDto event) {
        log.info("Received message from inventory-service order-expired or cancel: " + event.toString());
        // Process the message and update order status accordingly
        orderService.updateStatus(event.getOrderId(), OrderStatus.CONFIRM_SUCCESS.name());
        outBoxDAO.updateOutBox(event.getOrderId(), OrderStatus.CONFIRM_SUCCESS.name());

    }
}
