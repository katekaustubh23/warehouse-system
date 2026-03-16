package com.warehouse.inventory.service;

import com.warehouse.inventory.dao.InventoryDao;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class StockCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final InventoryDao inventoryDao;

    public int getAvailableStock(int productId) {
        String cacheKey = "stock:product:" + productId;
        Integer cachedStock = (Integer) redisTemplate.opsForValue().get(cacheKey);

        if (cachedStock != null) {
            System.out.println("Cache hit for productId=" + productId + ", stock=" + cachedStock);
            return cachedStock;
        }

        System.out.println("Cache miss for productId=" + productId + ". Fetching from DB...");
        int stockFromDb = inventoryDao.findById(productId).getQuantity();
        // TTL 10 minutes: prevent redis memory overflow
        redisTemplate.opsForValue().set(cacheKey, stockFromDb, 10, TimeUnit.MINUTES);
        return stockFromDb;
    }
}
