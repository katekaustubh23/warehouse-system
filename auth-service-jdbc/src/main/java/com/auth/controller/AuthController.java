package com.auth.controller;


import com.auth.client.UserServiceClient;
import com.auth.service.AuthRegisterService;
import com.auth.service.RefreshTokenService;
import com.auth.session.UserRegistration;
import com.auth.token.JwtTokenService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;

import java.time.Duration;
import java.util.Arrays;
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
    public ResponseEntity<?> refreshToken(HttpServletRequest request,
                                          HttpServletResponse response) {
        log.info("Refresh token request received");
        try {

            String refreshToken = extractCookie(request, "refresh_token");

            if (refreshToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // validate refresh token
            String username = jwtTokenService.getUsername(refreshToken);

            // OPTIONAL but recommended: validate against Redis/DB
            // refreshTokenService.validateStoredToken(username, refreshToken);

//             4. reload users (IMPORTANT for roles)

            Map<String, Object> user = userServiceClient.getUserByUsername(username, "USER");

            if (user == null) {
                return ResponseEntity.status(401).body("User not found");
            }

            String newAccessToken = jwtTokenService.generateAccessToken(username,
                    Collections.singletonList(user.get("role")
                            .toString()
                            .equals("ADMIN") ? "ADMIN" : "USER"));

            ResponseCookie newAccessCookie = ResponseCookie.from("access_token", newAccessToken)
                    .httpOnly(true) // Prevents JavaScript access to the cookie, mitigating XSS risks
                    .secure(false)
                    .path("/")
                    .maxAge(Duration.ofMinutes(15))
                    .sameSite("Lax") // Allows the cookie to be sent with top-level navigations and same-site requests, but not with cross-site requests, providing a balance between security and usability
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, newAccessCookie.toString());

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {

        ResponseCookie clearAccess = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie clearRefresh = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/auth/refresh")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, clearAccess.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, clearRefresh.toString());

        return ResponseEntity.ok().build();
    }

    private String extractCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {

        String token = extractCookie(request, "access_token");

        if (token == null || !jwtTokenService.isValidAccessToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = jwtTokenService.getUsername(token);

        return ResponseEntity.ok(Map.of(
                "username", username
        ));
    }
}