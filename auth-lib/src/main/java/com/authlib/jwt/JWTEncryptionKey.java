package com.authlib.jwt;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

@Component
public class JWTEncryptionKey {
    @Value("${jwt.secret.key}")
    private String secret;

    @Value("${jwt.secret.algorithm:HmacSHA256}")
    private String algorithm;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = new SecretKeySpec(secret.getBytes(), algorithm);
    }

    public Key getKey() {
        return key;
    }
}
