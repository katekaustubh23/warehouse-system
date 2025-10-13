package com.warehouse.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
@Component
public class PropertyConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")   // not needed here
    private String kafkaBootstrapServer;

    @Value("${spring.kafka.producer.acks}")  // not needed
    private String acks;

    @Value("${spring.kafka.producer.properties.enable.idempotence}")  // not needed
    private String idempotent;

//    @Value("${spring.kafka.consumer.group-id}")  // not needed
//    private String groupId;
}
