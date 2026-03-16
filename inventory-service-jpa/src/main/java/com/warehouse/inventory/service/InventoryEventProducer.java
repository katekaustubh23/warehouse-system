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
        kafkaTemplate.send("stock-reserved", String.valueOf(event.getProductId()), event);
    }

     public void sendStockRejectedEvent(StockReservedEventDto event) {
         kafkaTemplate.send("stock-rejected", String.valueOf(event.getProductId()),event);
    }
}
