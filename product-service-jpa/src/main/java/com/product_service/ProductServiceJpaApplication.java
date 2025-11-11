package com.product_service;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication(scanBasePackages = {"com.common", "com.product_service"})
public class ProductServiceJpaApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProductServiceJpaApplication.class, args);
	}

	@PostConstruct
	public void init() {
		// Force JVM default timezone
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		System.out.println("Default JVM TimeZone set to: " + TimeZone.getDefault().getID());
	}
}
