package com.warehouse.config;

import com.warehouse.model.InventoryHoldResultEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConsumerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.validation.ObjectError;

import java.util.HashMap;
import java.util.Map;

//@Configuration
public class KafkaConsumerConfig {

//    private final PropertyConfiguration propertyConfiguration;
//
//    public KafkaConsumerConfig(PropertyConfiguration propertyConfiguration){
//        this.propertyConfiguration = propertyConfiguration;
//    }
//
//    @Bean
//    public ConsumerFactory<String, InventoryHoldResultEvent> consumerFactory() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, propertyConfiguration.getKafkaBootstrapServer());
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, propertyConfiguration.getGroupId());
//
//        JsonDeserializer<InventoryHoldResultEvent> deserializer = new JsonDeserializer<>(InventoryHoldResultEvent.class);
//        deserializer.addTrustedPackages("*");
//
//        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
//    }
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, InventoryHoldResultEvent> kafkaListenerContainerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, InventoryHoldResultEvent> factory =
//                new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory());
//        // you can tune concurrency to match topic partitions
//        factory.setConcurrency(3);
//        return factory;
//    }
}

