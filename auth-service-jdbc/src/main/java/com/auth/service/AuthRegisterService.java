package com.auth.service;

import com.auth.client.UserServiceClient;

import com.auth.constant.Role;
import com.auth.session.UserRegistration;
import com.auth.token.JwtConfigProperties;
import com.auth.token.UrlBaseConfig;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthRegisterService {

    private final JwtConfigProperties jwtConfigProperties;

    private final UrlBaseConfig usUrlBaseConfig;
    private final UserServiceClient userServiceClient;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;

    public AuthRegisterService(JwtConfigProperties jwtConfigProperties,
                               UserServiceClient userServiceClient,
                               PasswordEncoder passwordEncoder,
                               RestTemplate restTemplate,
                               UrlBaseConfig usUrlBaseConfig) {
        this.jwtConfigProperties = jwtConfigProperties;
        this.userServiceClient =userServiceClient;
        this.passwordEncoder= passwordEncoder;
        this.restTemplate = restTemplate;
        this.usUrlBaseConfig = usUrlBaseConfig;
    }

    public Map<String, Object> registerFirstUser(String secret, UserRegistration request) {

        if (!jwtConfigProperties.getInternalSecret().equals(secret)) {
            throw new IllegalArgumentException("Invalid internal secret");
        }

        // URL of user-service endpoint
//        String url = usUrlBaseConfig.getUserServiceUrl() +
//                "/api/v1/user/verify/empty?role=" + request.role();

        URI uri = UriComponentsBuilder
                .fromHttpUrl(usUrlBaseConfig.getUserServiceUrl())  // base URL from config
                .path("/api/v1/user/verify/empty")                // endpoint path
                .queryParam("role", request.role())               // query param
                .build()
                .toUri();

        // Create headers and add your internal secret
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-Secret", jwtConfigProperties.getInternalSecret()); // your secret key

        // Wrap headers in HttpEntity (nobody for GET)
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        // Check if any users exist
        // Make GET request with headers

        ResponseEntity<Boolean> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                entity,
                Boolean.class
        );

        Boolean anyUserExists = response.getBody();

        if (Boolean.FALSE.equals(anyUserExists)) {
            throw new IllegalStateException("Users already exist. Cannot register first user.");
        }else{
            checkPermission(request.role(), Role.ADMIN);
            Map<String, Object> userDto = new HashMap<>();
            userDto.put("username", request.username());
            userDto.put("email", request.email());
            userDto.put("password", request.password());
            userDto.put("role", request.role()); // First user always ADMIN

            // Call user-service to create the user
            return userServiceClient.createUser(userDto, request.role().toString());
        }

        // Prepare user DTO to send to user-service
    }

    private void checkPermission(Role requestorRole, Role... allowedRoles) throws AccessDeniedException {
        for (Role allowed : allowedRoles) {
            if (requestorRole == allowed) return;
        }
        throw new AccessDeniedException("Insufficient permissions");
    }
}
