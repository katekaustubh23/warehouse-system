package com.warehouse.inventory.service;

import com.warehouse.inventory.dto.OrderConfirmEventDto;
import com.warehouse.inventory.dto.OrderCreatedEvent;
import com.warehouse.inventory.dto.StockReservedEventDto;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class KafkaInventoryListener {

    private static final Logger logger = LoggerFactory.getLogger(KafkaInventoryListener.class);

    private final RedisStockService redisStockService;
    private final StockCacheService stockCacheService;
    private final InventoryEventProducer inventoryEventProducer;

    @KafkaListener(topics="order-placed", groupId = "inventory-group")
    public void consumerOrderPlaced(OrderConfirmEventDto event) {
        logger.info("Received confirm-order : {}", event.getOrderId());
        try {
            // Here I have to remove reserve orderId and its expiration
            redisStockService.confirm(event.getOrderId());
             // Important: remove from cache to prevent stale data
            event.setStatus("CONFIRM_SUCCESS");
            inventoryEventProducer.confirmOrderSuccess(event);
        } catch (Exception e) {
            event.setStatus("CONFIRM_FAILED");
            inventoryEventProducer.confirmOrderFailed(event);
            logger.error(e.getLocalizedMessage());
        }

    }

    @KafkaListener(topics = "order-expired")
    @Transactional
    public void handleExpiry(StockReservedEventDto eventDto) {

    }

}
