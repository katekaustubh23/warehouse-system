package com.userservice.service;

import com.userservice.constant.Role;
import com.userservice.dto.UserResponse;
import com.userservice.dto.UserResponseWithPassword;
import com.userservice.exception.SQLTypeException;
import com.userservice.exception.UserNotFoundException;
import com.userservice.model.User;
import com.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserResponse> getAllUsers(Role requestorRole) throws AccessDeniedException {
        checkPermission(requestorRole, Role.ADMIN, Role.MANAGER);
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(Long id, Role requestorRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        // Only ADMIN or the user themself can view
        if (requestorRole != Role.ADMIN && requestorRole != user.getRole()) {
            throw new AccessDeniedException("Access denied");
        }
        return toResponse(user);
    }

    @Override
    public UserResponse createUser(User user, Role requestorRole) throws AccessDeniedException{
        checkPermission(requestorRole, Role.ADMIN);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // ... validate, encode password, etc.

        return toResponse(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id, Role requestorRole) throws AccessDeniedException {
        checkPermission(requestorRole, Role.ADMIN);
        userRepository.deleteById(id);
    }

    @Override
    public UserResponseWithPassword getUserByUsername(String username, Role role) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));
        return UserResponseWithPassword.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .password(user.getPassword()).build();
    }

    private void checkPermission(Role requestorRole, Role... allowedRoles) throws AccessDeniedException {
        for (Role allowed : allowedRoles) {
            if (requestorRole == allowed) return;
        }
        throw new AccessDeniedException("Insufficient permissions");
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
