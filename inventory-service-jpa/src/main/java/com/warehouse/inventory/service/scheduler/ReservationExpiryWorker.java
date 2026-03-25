package com.warehouse.inventory.service.scheduler;

import com.warehouse.inventory.dao.InventoryDao;
import com.warehouse.inventory.model.InventoryReserved;
import com.warehouse.inventory.service.RedisStockService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
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
    private final RedisStockService redisStockService ;
    private final InventoryDao reservationRepo;

    private static final String EXPIRY_ZSET = "reservation:expiry";

    @Scheduled(fixedRate = 5000)
    public void processExpiredReservations() {

        long now = System.currentTimeMillis();
        logger.info(">> Checking for expired reservations at {}", now);
        // WHY: only fetch expired items → efficient
        Set<Object> expiredOrders =
                redisTemplate.opsForZSet()
                        .rangeByScore(EXPIRY_ZSET, 0, now);
        logger.info(" << Found {} expired reservations", expiredOrders.size());
        if (expiredOrders == null || expiredOrders.isEmpty()) return;

        for (Object orderIdStr : expiredOrders) {
            logger.info(" << orderId ={}", orderIdStr.toString());
            Long orderId = Long.valueOf(orderIdStr.toString());

            // WHY: business logic rollback
            redisStockService.rollback(orderId);

            // WHY: remove from queue → prevent reprocessing
            redisTemplate.opsForZSet()
                    .remove(EXPIRY_ZSET, orderIdStr);
        }
    }

}
