package com.warehouse.inventory.service;

import com.warehouse.inventory.dto.OrderPlacedEventDto;
import com.warehouse.inventory.dto.StockReservedEventDto;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaInventoryListener {

    private static final Logger logger = LoggerFactory.getLogger(KafkaInventoryListener.class);

    private final RedisStockService redisStockService;
    private final InventoryEventProducer inventoryEventProducer;

    @KafkaListener(topics="order-placed", groupId = "inventory-group")
    public void consumerOrderPlaced(OrderPlacedEventDto event) {
        logger.info("Received Order: {}", event.getOrderId());
        boolean reserved = redisStockService.reverseStock(
                event.getProductId(), event.getQuantity());

        if(reserved){
            logger.info("Stock reserved for order {}",event.getOrderId());
            inventoryEventProducer.sendStockReservedEvent(new StockReservedEventDto(
                    event.getOrderId(), event.getProductId(), event.getQuantity()));
        }else{
            logger.info("Failed to reserve stock for order {}",event.getOrderId());
            inventoryEventProducer.sendStockRejectedEvent(new StockReservedEventDto(
                    event.getOrderId(), event.getProductId(), event.getQuantity()));
        }
    }
//    public void consumeOrderCreated(ConsumerRecord<String, String> record) {
//        logger.info("📦 Received message from Kafka topic='{}': key={}, value={}",
//                record.topic(), record.key(), record.value());
//        System.out.println("✅ Received from Kafka → " + record.value());
//    }

}
