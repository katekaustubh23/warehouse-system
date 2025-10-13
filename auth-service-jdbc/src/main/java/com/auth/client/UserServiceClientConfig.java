package com.auth.client;

import com.auth.token.JwtConfigProperties;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class UserServiceClientConfig {

    private String internalSecret;

    public UserServiceClientConfig(JwtConfigProperties jwtConfigProperties) {
        this.internalSecret = jwtConfigProperties.getInternalSecret();
    }

    @Bean
    public RequestInterceptor internalAuthHeaderInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("X-Internal-Secret", internalSecret);
        };
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
