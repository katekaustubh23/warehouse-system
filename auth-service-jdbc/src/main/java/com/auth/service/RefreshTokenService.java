package com.auth.service;

public interface RefreshTokenService {
    String getUsername(String username);

    void store(String username, String token, long ttlMillis);
    void delete(String username);
}
