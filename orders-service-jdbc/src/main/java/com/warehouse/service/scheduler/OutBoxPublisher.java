package com.warehouse.service.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.warehouse.constant.OrderStatus;
import com.warehouse.dao.OutBoxDAO;
import com.warehouse.model.OrderConfirmEventDto;
import com.warehouse.model.OrderCreatedEvent;
import com.warehouse.model.OutBox;
import com.warehouse.service.OrderProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class OutBoxPublisher {

    private final OutBoxDAO outBoxDAO;
    private final OrderProducerService orderProducerService;


    @Scheduled(fixedRate = 10000) // every 10 seconds
    @Transactional
    public void OutBoxPublisher() {
        List<OutBox> outBoxes = outBoxDAO.findTop50ByStatus(OrderStatus.NEW.name());
        log.info("Found {} new messages to publish", outBoxes.size());
        for (OutBox outBox : outBoxes) {
            try {
                // Simulate message publishing
                String payload = outBox.getPayload();
                OrderConfirmEventDto orderConfirmEvent = new ObjectMapper()
                        .readValue(payload, OrderConfirmEventDto.class);
                orderProducerService.sendOrderCreatedMessage(orderConfirmEvent);
                log.info("Publishing message: {}", outBox.getPayload());
                // After successful publish, update the status to SENT
                outBox.setStatus(OrderStatus.SENT.name());
                int updatedRow = outBoxDAO.updateOutBox(outBox.getId(), OrderStatus.SENT.name());
                log.info("Updated OutBox with id {}: {} row(s) affected", outBox.getId(), updatedRow);
            } catch (Exception e) {
                log.error("Failed to publish message with id {}: {}", outBox.getId(), e.getMessage());
                // Optionally, you can implement retry logic or mark the message as failed
            }
        }
    }
}
