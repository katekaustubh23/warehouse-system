package com.warehouse.inventory.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.warehouse.inventory.dao.InventoryDao;
import com.warehouse.inventory.model.InventoryEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaInventoryListener {
    private final InventoryDao inventoryService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public KafkaInventoryListener(InventoryDao inventoryService, KafkaTemplate<String, String> kafkaTemplate) {
        this.inventoryService = inventoryService;
        this.kafkaTemplate = kafkaTemplate;
    }
    @KafkaListener(topics = "order-events", groupId = "inventory-service-group")
    public void consume(String message) {
        try {
            InventoryEvent event = mapper.readValue(message, InventoryEvent.class);

            if ("ORDER_CREATED".equals(event.getEventType())) {
                String status = inventoryService.allocateInventory(event);

                event.setEventType(status.contains("ALLOCATED") ? "INVENTORY_ALLOCATED" : "INVENTORY_FAILED");
                String resultJson = mapper.writeValueAsString(event);

                kafkaTemplate.send("inventory-events", resultJson);
                System.out.println("✅ Inventory processed: " + resultJson);
            }

        } catch (Exception e) {
            System.err.println("❌ Error processing event: " + e.getMessage());
        }
    }
}
