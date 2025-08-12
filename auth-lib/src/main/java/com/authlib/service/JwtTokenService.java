package com.authlib.service;

import com.authlib.util.JwtUtil;

public class JwtTokenService {
    private final JwtUtil jwtUtil;

    public JwtTokenService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public JwtToken createToken(UserDetails userDetails) {
        String token = jwtUtil.generateToken(userDetails);
        long expiration = System.currentTimeMillis() + jwtUtil.getExpirationMillis();
        return new JwtToken(token, expiration);
    }
}
