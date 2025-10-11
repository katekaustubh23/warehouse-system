package com.auth.session;

import com.auth.constant.Role;

public record UserRegistration(String username,
                               String password,
                               String email,
                               Role role) {
}
