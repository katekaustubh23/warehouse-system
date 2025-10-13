package com.warehouse.inventory;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.kafka.annotation.EnableKafka;

import java.util.TimeZone;

@EnableKafka
@SpringBootApplication(exclude = { UserDetailsServiceAutoConfiguration.class },scanBasePackages = { "com.warehouse.inventory","com.authlib"})
public class InventoryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

    @PostConstruct
    public void init() {
        // Force JVM default timezone
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        System.out.println("Default JVM TimeZone set to: " + TimeZone.getDefault().getID());
    }
}