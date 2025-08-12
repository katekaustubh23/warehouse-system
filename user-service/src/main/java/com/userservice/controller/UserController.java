package com.userservice.controller;

import com.userservice.constant.Role;
import com.userservice.dto.UserResponse;
import com.userservice.dto.UserResponseWithPassword;
import com.userservice.model.User;
import com.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final String internalSecret;

    private final UserService userService;

    public UserController(@Value("${internal.secret}") String internalSecret, UserService userService) {
        this.internalSecret = internalSecret;
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponse> getAllUsers(@RequestParam Role role) throws NoSuchFieldException {
        return userService.getAllUsers(role);
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id, @RequestParam Role role) {
        return userService.getUserById(id, role);
    }

    @GetMapping("/by-username/{username}")
    public UserResponseWithPassword getUserByUsername(@RequestHeader(value = "X-Internal-Secret", required = false) String secret, @PathVariable String username, @RequestParam Role role) {

        if (secret == null || !secret.equals(internalSecret)) {
            throw new AccessDeniedException("Access denied");
        }
        return userService.getUserByUsername(username, role);
    }


    @PostMapping
    public UserResponse createUser(@RequestBody User user, @RequestParam Role role) throws NoSuchFieldException {
        return userService.createUser(user, role);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id, @RequestParam Role role) throws NoSuchFieldException {
        userService.deleteUser(id, role);
    }
}
