package com.userservice.dto;

import com.userservice.constant.Role;
import lombok.Builder;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class UserResponseWithPassword extends UserResponse {

    private String password;

    public UserResponseWithPassword(Long id, String username, String email, Role role, String password) {
        super(id, username, email, role);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
