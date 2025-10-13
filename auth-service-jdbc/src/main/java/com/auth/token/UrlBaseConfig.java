package com.auth.token;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class UrlBaseConfig {

    @Value("${user.service.url}")
    private String userServiceUrl;
}
