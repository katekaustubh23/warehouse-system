package com.warehouse_location;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class WarehouseLocationApplication {

    public static void main(String[] args) {
        SpringApplication.run(WarehouseLocationApplication.class, args);
    }

    @PostConstruct
    public void init() {
        // Force JVM default timezone
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        System.out.println("Default JVM TimeZone set to: " + TimeZone.getDefault().getID());
    }
}
