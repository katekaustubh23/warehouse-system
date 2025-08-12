package com.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "user-service" , url = "http://localhost:8087/api/v1/user",configuration = UserServiceClientConfig.class)
public interface UserServiceClient {

    @GetMapping("/by-username/{username}")
    Map<String, Object> getUserByUsername(@PathVariable("username") String username, @RequestParam("role") String role);
}
