package com.warehouse.config;

import com.warehouse.model.OrderConfirmEventDto;
import com.warehouse.model.StockReservedEventDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    private final PropertyConfiguration propertyConfiguration;

    public KafkaConsumerConfig(PropertyConfiguration propertyConfiguration){
        this.propertyConfiguration = propertyConfiguration;
    }

    @Bean
    public ConsumerFactory<String, OrderConfirmEventDto> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, propertyConfiguration.getKafkaBootstrapServer());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, propertyConfiguration.getGroupId());
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, propertyConfiguration.getGroupId());

        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), new JsonDeserializer<>(OrderConfirmEventDto.class));
    }
//
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderConfirmEventDto> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderConfirmEventDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        // can tune concurrency to match topic partitions
        factory.setConcurrency(3);
        return factory;
    }
}

