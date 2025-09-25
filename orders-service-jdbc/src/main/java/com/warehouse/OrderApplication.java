package com.warehouse;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.TimeZone;

@EnableAspectJAutoProxy
@SpringBootApplication(exclude = { UserDetailsServiceAutoConfiguration.class },scanBasePackages = {"com.warehouse", "com.logging", "com.authlib"})
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

    @PostConstruct
    public void init() {
        // Force JVM default timezone
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        System.out.println("Default JVM TimeZone set to: " + TimeZone.getDefault().getID());
    }
}