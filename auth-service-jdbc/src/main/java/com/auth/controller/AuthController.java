package com.auth.controller;


import com.auth.client.UserServiceClient;
import com.auth.service.AuthRegisterService;
import com.auth.service.RefreshTokenService;
import com.auth.session.UserRegistration;
import com.auth.token.JwtTokenService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/v1/authenticate")
public class AuthController {

    private final RefreshTokenService refreshTokenService;

    private final AuthRegisterService authRegisterService;
    private final JwtTokenService jwtTokenService;
    private final UserServiceClient userServiceClient;
    public AuthController(AuthRegisterService authRegisterService,
                          JwtTokenService jwtTokenService,
                          RefreshTokenService refreshTokenService,
                          UserServiceClient userServiceClient) {
        this.authRegisterService = authRegisterService;
        this.jwtTokenService = jwtTokenService;
        this.refreshTokenService = refreshTokenService;
        this.userServiceClient = userServiceClient;
    }

    @PostMapping("/first/register")
    public ResponseEntity<?> registerFirstUser(
            @RequestHeader("INTERNAL_SECRET") String secret,
            @RequestBody UserRegistration request) {

        try {
            Map<String, Object> createdUser = authRegisterService.registerFirstUser(secret, request);
            return ResponseEntity.ok(createdUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @RequestHeader("Authorization") String header) {
        log.info("Refresh token request received");
        try {
            String refreshToken = header.substring(7);
            Claims claims = jwtTokenService.extractClaims(refreshToken);
            jwtTokenService.isRefreshToken(refreshToken);
            // 1. validate type
            if (!jwtTokenService.isRefreshToken(refreshToken)) {
                return ResponseEntity.status(401).body("Invalid token type");
            }

            // 2. expiry
            if (jwtTokenService.isExpired(refreshToken)) {
                return ResponseEntity.status(401).body("Refresh token expired");
            }

            String username = jwtTokenService.getUsername(refreshToken);

            // 3. validate with Redis
            String stored = refreshTokenService.getUsername(username);
            if (stored == null || !stored.equals(refreshToken)) {
                return ResponseEntity.status(401).body("Invalid refresh token");
            }

            // 4. reload users (IMPORTANT for roles)

            Map<String, Object> user = userServiceClient.getUserByUsername(username, "USER");

            if (user == null) {
                return ResponseEntity.status(401).body("User not found");
            }
            String accessToken = jwtTokenService.generateAccessToken(username,
                    Collections.singletonList(user.get("role")
                            .toString()
                            .equals("ADMIN") ? "ADMIN" : "USER"));
            String newRefreshToken = jwtTokenService.generateRefreshToken(username);
            refreshTokenService.store(username, newRefreshToken, 7 * 24 * 60 * 60 * 1000);
            return ResponseEntity.ok(Map.of("accessToken",accessToken,
                "refreshToken",refreshToken));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}