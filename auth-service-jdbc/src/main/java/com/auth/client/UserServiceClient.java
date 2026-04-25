package com.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "user-service" , url = "${user.service.url}/api/v1/user",configuration = UserServiceClientConfig.class)
public interface UserServiceClient {

    @GetMapping("/by-username/{username}")
    Map<String, Object> getUserByUsername(@PathVariable("username") String username, @RequestParam("role") String role);

    @PostMapping
    Map<String, Object> createUser(@RequestBody Map<String, Object> userRegistration, @RequestParam("role") String role);
}
