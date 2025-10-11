package com.auth.controller;


import com.auth.service.AuthRegisterService;
import com.auth.session.UserRegistration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/v1/register")
public class AuthController {

    private final AuthRegisterService authRegisterService;
    public AuthController(AuthRegisterService authRegisterService){
        this.authRegisterService = authRegisterService;
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
}