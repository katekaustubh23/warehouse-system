package com.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@SpringBootApplication(scanBasePackages = {"com.authlib", "com.gateway"})
public class WarehouseGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(WarehouseGatewayApplication.class, args);
	}

}
