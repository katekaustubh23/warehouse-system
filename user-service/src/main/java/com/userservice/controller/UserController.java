package com.userservice.controller;

import com.userservice.constant.Role;
import com.userservice.dto.UserResponse;
import com.userservice.dto.UserResponseWithPassword;
import com.userservice.model.User;
import com.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController( UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponse> getAllUsers(@RequestParam("role") Role role) throws NoSuchFieldException {
        return userService.getAllUsers(role);
    }

    @GetMapping("/verify/empty")
    public Boolean anyUserExists(@RequestParam("role") Role role) throws NoSuchFieldException {
        return userService.userExits(role);
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable("id") Long id, @RequestParam("role") Role role) {
        return userService.getUserById(id, role);
    }

    @GetMapping("/by-username/{username}")
    public UserResponseWithPassword getUserByUsername(@RequestHeader(value = "X-Internal-Secret", required = false) String secret, @PathVariable("username") String username, @RequestParam("role") Role role) {

        return userService.getUserByUsername(username, role);
    }


    @PostMapping
    public UserResponse createUser(@RequestBody User user, @RequestParam("role") Role role) throws NoSuchFieldException {
        return userService.createUser(user, role);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") Long id, @RequestParam("role") Role role) throws NoSuchFieldException {
        userService.deleteUser(id, role);
    }
}
