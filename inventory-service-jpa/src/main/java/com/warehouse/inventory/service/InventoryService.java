package com.warehouse.inventory.service;

import com.warehouse.inventory.dao.InventoryDao;
import com.warehouse.inventory.dto.StockReservedEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryDao inventoryDao;
    private final InventoryEventProducer inventoryEventProducer;

    @Transactional
    public void releaseExpiredStock(Long orderId) {
        log.info("Releasing stock for expired order: {}", orderId);
        int updatedRow = inventoryDao.releasedStock(orderId);
        if(updatedRow > 0) return; //already processed by another worker

        inventoryDao.makeReleased(orderId);
        inventoryEventProducer.sendOrderExpired(new StockReservedEventDto(orderId, 0L, 0, "EXPIRED"));
        // Implement logic to release reserved stock for the expired order
    }
}

