package com.warehouse.inventory.service;

import com.inventory.grpc.OrderItem;
import com.warehouse.inventory.dao.InventoryDao;
import com.warehouse.inventory.model.InventoryReserved;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedisStockService {
    private final static Logger log = LoggerFactory.getLogger(RedisStockService.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private final InventoryDao inventoryDao;
    private static final String EXPIRY_ZSET = "reservation:expiry";

    // Lua script for reversing stock, it checks if the current stock is greater than or equal to the decrement value,
    private static final String REVERSE_SCRIPT = """
        local key = KEYS[1]
        local decrement = tonumber(ARGV[1])
        local current = tonumber(redis.call('GET', key) or '0')
        if current >= decrement then
            return redis.call('DECRBY', key, decrement)
        else
            return nil
        end
    """;

    /**
     * Function with LUA script to reverse stock,
     * if the stock is not enough, return null, otherwise return the new stock after decrement.
     * @param productId product id
     * @param quantity quantity to reverse
     * @return true if stock reversed successfully, false if not enough stock
     */
    public boolean reverseStock(Long productId, int quantity) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>(REVERSE_SCRIPT, Long.class);
        script.setScriptText(REVERSE_SCRIPT);
        script.setResultType(Long.class);
        Long result = redisTemplate.execute(script, Collections
                .singletonList("stock:product:" + productId), quantity);
        System.out.println("Attempting to reverse stock for product = " + result);
        if (result != null) {

            System.out.println("Stock reversed for productId=" + productId + ", quantity=" + quantity + ", newStock=" + result);
            return true;
        } else {
            System.out.println("Failed to reverse stock for productId=" + productId + ". Not enough stock available.");
            return false;
        }
    }

    public void releaseStock(Long productId,int qty){

        redisTemplate.opsForValue()
                .increment("stock:product:"+productId,qty);
    }

    /**
     * Implement redis to store reserve quantity and expire time,
     * so that we can release the stock after the expiry time if the order is not completed.
     */
    @Transactional
    public boolean reserveStock(Long orderId, List<com.inventory.grpc.OrderItem> orderItems){

        Map<Long, Integer> reservedItems = new HashMap<>();
        log.info("Reserving stock for orderId={}, items={}", orderId, orderItems);
        //step 1
        for (com.inventory.grpc.OrderItem item : orderItems) {
            Long productId = item.getProductId();
            int qty = item.getQuantity();
            String stockKey = "stock:product:" + productId;

            // WHY: atomic decrement → ensures concurrency safety
            Long remaining = redisTemplate.opsForValue().increment(stockKey, -qty);
            log.info("remaining quantity ={} for product ={}", remaining, productId);
            if(remaining == null || remaining < 0) {
                // WHY: avoid partial reservation (critical)

                rollbackPartial(reservedItems);

                return false;
            }
            reservedItems.put(productId, qty);
        }

        //step 2: Store reservation in Redis (for rollback)

        String reservationKey = "reservation:" + orderId;

        // WHY: store reserved items for potential rollback (critical for consistency)
        redisTemplate.opsForHash().putAll(reservationKey, reservedItems.entrySet().
                stream().collect(Collectors.toMap(
                        e -> e.getKey().toString(),
                        e -> e.getValue().toString())));
        // WHY: set expiry for reservation (critical for auto-release)
        //step 4: Add expiry to ZSET
        long expiryTimeMillis = System.currentTimeMillis() + 50000; // 5 min

        // WHY: ZSET = delay queue → avoids DB scanning
        redisTemplate.opsForZSet().add(
                EXPIRY_ZSET,
                orderId.toString(),
                expiryTimeMillis
        );

        // Step 4: Save in DB (source of truth)
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);
        List<InventoryReserved> reserveds = new ArrayList<>();
        for (OrderItem item : orderItems ) {
            InventoryReserved reserved = new InventoryReserved();
            reserved.setOrderId(orderId);
            reserved.setProductId(item.getProductId());
            reserved.setQuantity(item.getQuantity());
            reserved.setStatus("RESERVED");
            reserved.setExpiredAt(expiryTime);
            reserveds.add(reserved);
        }
        inventoryDao.batchSaveReserveQuantity(reserveds);
        return true;
    }

    /**
     * Rollback the reserved stock in case of failure to reserve all items.
     * This is critical to maintain data consistency and prevent stock from being incorrectly reserved.
     * @param reservedItems Map of productId to quantity that were successfully reserved before the failure occurred.
     */
    private void rollbackPartial(Map<Long, Integer> reservedItems) {
        for (Map.Entry<Long, Integer> entry : reservedItems.entrySet()) {
            Long productId = entry.getKey();
            int qty = entry.getValue();
            String stockKey = "stock:product:" + productId;
            redisTemplate.opsForValue().increment(stockKey, qty);
        }
    }

    public void confirm(Long orderId) {

        List<InventoryReserved> reservations =
                inventoryDao.findByOrderId(orderId);

        for (InventoryReserved r : reservations) {

            if (!"RESERVED".equals(r.getStatus())) continue;

            // WHY: final commit to DB (real inventory)
            // inventoryRepo.decrement(...)

            r.setStatus("CONFIRMED");
        }

        inventoryDao.batchSaveReserveQuantity(reservations);

        // WHY: remove from Redis (no need to rollback anymore)
        redisTemplate.delete("reservation:" + orderId);

        // WHY: remove from delay queue (prevent duplicate rollback)
        redisTemplate.opsForZSet()
                .remove("reservation:expiry", orderId.toString());
    }

    public void rollback(Long orderId) {

        List<InventoryReserved> reservations = inventoryDao.findByOrderId(orderId);

        for (InventoryReserved r : reservations) {

            // WHY: idempotency → avoid double rollback
            if (!"RESERVED".equals(r.getStatus())) continue;

            redisTemplate.opsForValue().increment(
                    "stock:product:" + r.getProductId(),
                    r.getQuantity()
            );

            r.setStatus("EXPIRED");
        }

        inventoryDao.batchSaveReserveQuantity(reservations);

        redisTemplate.delete("reservation:" + orderId);
    }
}


