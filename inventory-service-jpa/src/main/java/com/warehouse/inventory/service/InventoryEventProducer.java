package com.warehouse.inventory.service;

import com.warehouse.inventory.dto.StockReservedEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendStockReservedEvent(StockReservedEventDto event) {
        System.out.println(""+ "Sending stock-reserved event for productId: " + event.getProductId() + ", orderId: " + event.getOrderId());
        kafkaTemplate.send("stock-reserved", String.valueOf(event.getProductId()), event);
    }

     public void sendStockRejectedEvent(StockReservedEventDto event) {
        System.out.println(""+ "Sending stock-rejected event for productId: " + event.getProductId() + ", orderId: " + event.getOrderId());
         kafkaTemplate.send("stock-rejected", String.valueOf(event.getProductId()),event);
    }

    public void sendOrderExpired(StockReservedEventDto event) {
        System.out.println(""+ "Sending order-expired event for orderId: " + event.getOrderId());
        kafkaTemplate.send("order-expired", String.valueOf(event.getOrderId()),event);
    }
}
