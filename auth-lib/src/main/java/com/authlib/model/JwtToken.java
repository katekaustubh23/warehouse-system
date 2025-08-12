package com.authlib.model;

public class JwtToken {
    private String token;
    private long expiresAt;

    public JwtToken(String token, long expiresAt) {
        this.token = token;
        this.expiresAt = expiresAt;
    }

    public String getToken() {
        return token;
    }

    public long getExpiresAt() {
        return expiresAt;
    }
}
