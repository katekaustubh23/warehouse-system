package com.authlib.auth;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RefreshTokenManager {
    private static final Map<String, String> userToRefreshToken = new ConcurrentHashMap<>();

    public static String createTokenForUser(String username) {
        String refreshToken = UUID.randomUUID().toString();
        userToRefreshToken.put(refreshToken, username);
        return refreshToken;
    }

    public static String getUsernameFromRefreshToken(String refreshToken) {
        return userToRefreshToken.get(refreshToken);
    }

    public static boolean isValid(String refreshToken) {
        return userToRefreshToken.containsKey(refreshToken);
    }
}
