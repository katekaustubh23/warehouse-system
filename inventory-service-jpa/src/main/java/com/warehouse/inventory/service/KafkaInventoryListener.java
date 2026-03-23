package com.warehouse.inventory.service;

import com.warehouse.inventory.dto.OrderCreatedEvent;
import com.warehouse.inventory.dto.StockReservedEventDto;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaInventoryListener {

    private static final Logger logger = LoggerFactory.getLogger(KafkaInventoryListener.class);

    private final RedisStockService redisStockService;
    private final StockCacheService stockCacheService;
    private final InventoryEventProducer inventoryEventProducer;

    @KafkaListener(topics="order-placed", groupId = "inventory-group")
    public void consumerOrderPlaced(OrderCreatedEvent event) {
        logger.info("Received Order: {}", event.getOrderId());
        try {
            for (var item : event.getItems()) {
                logger.info("Order Item - ProductId: {}, Quantity: {}", item.getProductId(), item.getQuantity());
                boolean reserved = redisStockService.reverseStock(
                        item.getProductId(), item.getQuantity());
                logger.info("redis data :: " + stockCacheService.getAvailableStock(event.getItems().get(0).getProductId().intValue()));

                if(reserved){
                    logger.info("Stock reserved for order {}",event.getOrderId());
                    inventoryEventProducer.sendStockReservedEvent(new StockReservedEventDto(
                            event.getOrderId(), item.getProductId(), item.getQuantity()));
                }else{
                    logger.info("Failed to reserve stock for order {}",event.getOrderId());
                    inventoryEventProducer.sendStockRejectedEvent(new StockReservedEventDto(
                            event.getOrderId(), item.getProductId(), item.getQuantity()));
                }
            }

        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }

    }
//    public void consumeOrderCreated(ConsumerRecord<String, String> record) {
//        logger.info("📦 Received message from Kafka topic='{}': key={}, value={}",
//                record.topic(), record.key(), record.value());
//        System.out.println("✅ Received from Kafka → " + record.value());
//    }

}
