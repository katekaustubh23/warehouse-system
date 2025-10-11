package com.warehouse.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducerService {

    private final KafkaTemplate kafkaTemplate;

    public OrderProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderCreatedMessage(String orderId) {
        String topic = "inventory-topic"; // Make sure inventory-service consumes this topic
        String message = "OrderCreated:" + orderId;
        kafkaTemplate.send(topic, orderId, message);
        System.out.println("Sent message to inventory-service: " + message);
    }
}
