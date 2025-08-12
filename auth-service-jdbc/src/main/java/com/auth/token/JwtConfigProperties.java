package com.auth.token;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfigProperties {

    private String secret;
    private long accessTokenExpiryMs;
    private long refreshTokenExpiryMs;
}
