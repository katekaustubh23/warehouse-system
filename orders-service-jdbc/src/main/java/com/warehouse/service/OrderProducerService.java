package com.warehouse.service;

import com.warehouse.model.OrderCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderCreatedMessage(OrderCreatedEvent orderEvent) {
        String topic = "order-placed"; // Make sure inventory-service consumes this topic
        String message = "OrderCreated:" + orderEvent.getOrderId();

        kafkaTemplate.send(topic, orderEvent.getOrderId().toString(), orderEvent);
        System.out.println("Sent message to inventory-service: " + orderEvent.toString());
    }
}
