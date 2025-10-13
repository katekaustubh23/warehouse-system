package com.userservice.service;

import com.userservice.constant.Role;
import com.userservice.dto.UserResponse;
import com.userservice.dto.UserResponseWithPassword;
import com.userservice.model.User;

import java.sql.SQLException;
import java.util.List;

public interface UserService {
    UserResponse createUser(User user, Role role) throws NoSuchFieldException;

    List<UserResponse> getAllUsers(Role role) throws NoSuchFieldException;

    UserResponse getUserById(Long id, Role role);

    void deleteUser(Long id, Role role) throws NoSuchFieldException;

    UserResponseWithPassword getUserByUsername(String username, Role role);

    Boolean userExits(Role role);
}
