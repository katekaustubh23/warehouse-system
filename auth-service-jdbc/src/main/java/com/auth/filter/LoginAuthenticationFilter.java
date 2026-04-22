package com.auth.filter;

import com.auth.service.RefreshTokenService;
import com.auth.token.JwtConfigProperties;
import com.auth.token.JwtTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import jakarta.servlet.FilterChain;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;

import java.util.List;
import java.util.Map;

/*
 * Custom authentication filter for handling /login requests.
 * - Extends AbstractAuthenticationProcessingFilter to leverage Spring Security login flow.
 * - Intercepts POST requests to the login URL.
 * - attemptAuthentication(): reads username/password from JSON and authenticates via AuthenticationManager.
 * - successfulAuthentication(): generates JWT access and refresh tokens and returns them as JSON.
 * - unsuccessfulAuthentication(): returns 401 error if credentials are invalid.
 *
 * Purpose:
 * This filter replaces default form login and provides JWT-based authentication.
 */

@Service
public class LoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger log = LoggerFactory.getLogger(LoginAuthenticationFilter.class);

    private final JwtTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;
    private final JwtConfigProperties jwtConfigProperties;

    public LoginAuthenticationFilter(AuthenticationManager manager,
                                     JwtTokenService jwtTokenService,
                                     RefreshTokenService refreshTokenService,
                                     JwtConfigProperties jwtConfigProperties) {
        super(new AntPathRequestMatcher("/v1/auth/login", "POST"));
        setAuthenticationManager(manager);
        this.jwtTokenService = jwtTokenService;
        this.refreshTokenService = refreshTokenService;
        this.jwtConfigProperties = jwtConfigProperties;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
            throws AuthenticationException, IOException {

        Map<String, String> credentials = new ObjectMapper()
                .readValue(request.getInputStream(), Map.class);

        String username = credentials.get("username");
        String password = credentials.get("password");

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(credentials.get("username"), credentials.get("password"));

        return getAuthenticationManager().authenticate(token);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException {
        String username = authResult.getName();

        List<String> roles = authResult.getAuthorities()
                .stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .toList();

        String accessToken = jwtTokenService.generateAccessToken(username, roles);
        String refreshToken = jwtTokenService.generateRefreshToken(username);

        // access token cookie (short-lived)
        ResponseCookie tokenCookie = ResponseCookie.from("access_token", accessToken)
                .httpOnly(true) // Prevents JavaScript access to the cookie, mitigating XSS risks
                .secure(true)// Ensures the cookie is only sent over HTTPS, enhancing security in production
                .path("/")// Makes the cookie available to the entire application
                .maxAge(Long.parseLong(jwtConfigProperties.getAccessTokenExpiryMs()) / 1000) // Sets the cookie's lifespan to match the access token's expiration time
                .sameSite("Strict") // Prevents the cookie from being sent with cross-site requests, mitigating CSRF risks
                .build();

        // refresh token cookie (long-lived)
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/v1/authenticate/refresh") // Restricts the refresh token cookie to the refresh endpoint, reducing exposure
                .maxAge(Long.parseLong(jwtConfigProperties.getRefreshTokenExpiryMs()) / 1000)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, tokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        refreshTokenService.store(username, refreshToken, Long.parseLong(jwtConfigProperties.getRefreshTokenExpiryMs()));
        // No need to send tokens in body anymore
        response.setStatus(HttpStatus.OK.value());


        //        response as JSON with tokens (optional, since we're using cookies)
        //        String token = "GENERATE_JWT_HERE"; // Replace with JwtTokenFactory
        //        response.setContentType("application/json");
        //        response.getWriter().write("{\"token\": \"" + accessToken + "\", \"refreshToken\": \"" + refreshToken +"\"}");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"error\": \"Invalid username or password\"}");
    }

    public void setAuthManager(AuthenticationManager manager) {
        super.setAuthenticationManager(manager);
    }
}
