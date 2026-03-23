package com.warehouse.inventory.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class RedisStockService {

    private final RedisTemplate<String, Object> redisTemplate;
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
}
