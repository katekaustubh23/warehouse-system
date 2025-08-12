package com.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

//@Configuration
public class AuthSecurityConfig {
//    private final CustomAuthenticationProvider provider;
//
//    public AuthSecurityConfig(CustomAuthenticationProvider provider) {
//        this.provider = provider;
//    }
//
//    @Autowired
//    public void bindProvider(AuthenticationManagerBuilder builder) {
//        builder.authenticationProvider(provider);
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
//        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
//    }
}
