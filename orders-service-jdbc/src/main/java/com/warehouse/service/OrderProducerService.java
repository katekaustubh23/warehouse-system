package com.warehouse.service;

import com.warehouse.model.OrderConfirmEventDto;
import com.warehouse.model.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderCreatedMessage(OrderConfirmEventDto orderEvent) {
        String topic = "order-placed"; // Make sure inventory-service consumes this topic
        String message = "OrderCreated:" + orderEvent.getOrderId();

        kafkaTemplate.send(topic, orderEvent.getOrderId().toString(), orderEvent)
                .whenComplete((res, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send message: " + ex.getMessage());
                    } else {
                        log.info("Message sent successfully to topic " + topic + " with key " + orderEvent.getOrderId());
                    }
                });
    }
}
