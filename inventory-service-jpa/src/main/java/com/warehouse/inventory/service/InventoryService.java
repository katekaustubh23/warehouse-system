package com.warehouse.inventory.service;

import com.warehouse.inventory.dao.InventoryDao;
import com.warehouse.inventory.dto.StockReservedEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryDao inventoryDao;
    private final InventoryEventProducer inventoryEventProducer;
    private final RedisTemplate redisTemplate;

    @Transactional
    public void releaseExpiredStock(Long orderId) {
        log.info("Releasing stock for expired order: {}", orderId);
        String reservationKey = "reservation:" + orderId;
        Map<Object, Object> reservedItems =
                redisTemplate.opsForHash().entries(reservationKey);
        if (reservedItems.isEmpty()) {
            log.warn("No reserved items found for orderId={}, skipping release", orderId);
            return; // No reservation found, possibly already processed
        }
        for (Map.Entry<Object, Object> entry : reservedItems.entrySet()) {

            String productId = (String) entry.getKey();
            int qty = Integer.parseInt((String) entry.getValue());

            String stockKey = "stock:product:" + productId;

            redisTemplate.opsForValue().increment(stockKey, qty);
        }
        redisTemplate.delete(reservationKey);

//        int updatedRow = inventoryDao.releasedStock(orderId);
//        if(updatedRow > 0) return; //already processed by another worker

        inventoryDao.makeReleased(orderId);
        inventoryEventProducer.sendOrderExpired(new StockReservedEventDto(orderId, 0L, 0, "EXPIRED"));
        // Implement logic to release reserved stock for the expired order
    }
}

