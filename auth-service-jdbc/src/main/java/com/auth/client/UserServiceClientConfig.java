package com.auth.client;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserServiceClientConfig {

    @Value("${internal.secret}")
    private String internalSecret;

    @Bean
    public RequestInterceptor internalAuthHeaderInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("X-Internal-Secret", internalSecret);
        };
    }
}
