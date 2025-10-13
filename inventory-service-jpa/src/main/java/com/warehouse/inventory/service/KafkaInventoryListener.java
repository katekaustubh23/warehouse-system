package com.warehouse.inventory.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.stereotype.Service;

@Service
public class KafkaInventoryListener {

    private static final Logger logger = LoggerFactory.getLogger(KafkaInventoryListener.class);

    @KafkaListener(topics="inventory-topic", groupId = "inventory-group")
    public void consumeOrderCreated(ConsumerRecord<String, String> record) {
        logger.info("📦 Received message from Kafka topic='{}': key={}, value={}",
                record.topic(), record.key(), record.value());
        System.out.println("✅ Received from Kafka → " + record.value());
    }

}
