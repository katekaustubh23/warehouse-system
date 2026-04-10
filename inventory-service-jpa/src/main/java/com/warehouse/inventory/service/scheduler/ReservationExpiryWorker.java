package com.warehouse.inventory.service.scheduler;

import com.warehouse.inventory.dao.InventoryDao;
import com.warehouse.inventory.dto.StockReservedEventDto;
import com.warehouse.inventory.model.InventoryReserved;
import com.warehouse.inventory.service.InventoryEventProducer;
import com.warehouse.inventory.service.InventoryService;
import com.warehouse.inventory.service.RedisStockService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ReservationExpiryWorker {

    private final static Logger logger = LoggerFactory.getLogger(ReservationExpiryWorker.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private final InventoryService inventoryService;
    private final InventoryEventProducer inventoryEventProducer;

    private static final String EXPIRY_ZSET = "reservation:expiry";

    @Scheduled(fixedRateString = "PT2M") // every 2 minutes
    public void processExpiredReservations() {

        long now = System.currentTimeMillis();
        logger.info(">> Checking for expired reservations at {}", now);
        // WHY: only fetch expired items → efficient
        int batchSize = 50;

        for (int i = 0; i < batchSize; i++) {

            // ✅ Atomic fetch + remove (ZPOPMIN)
            ZSetOperations.TypedTuple<Object> tuple =
                    redisTemplate.opsForZSet().popMin(EXPIRY_ZSET);

            if (tuple == null) {
                return; // no more data
            }

            Long orderId = Long.valueOf(tuple.getValue().toString());
            long expiryTime = tuple.getScore().longValue();

            // Important: skip if not actually expired
            if (expiryTime > now) {
                // put back if not expired
                redisTemplate.opsForZSet()
                        .add(EXPIRY_ZSET, orderId, expiryTime);
                return;
            }

            StockReservedEventDto event = new StockReservedEventDto(orderId,0L,0,"EXPIRED");

            // publish event instead of processing directly
            inventoryService.releaseExpiredStock(orderId); // DB + stock rollback
//            try {
//                logger.info("Processing expired order {}", orderId);
//
//                orderService.expireOrder(orderId); // DB + stock rollback
//
//            } catch (Exception ex) {
//
//                logger.error("Failed to process order {}", orderId, ex);
//
//                // ❗ Reinsert for retry
//                redisTemplate.opsForZSet()
//                        .add(EXPIRY_ZSET, orderId, now + 30000); // retry after 30 sec
//            }
        }
    }
}
