package com.auth.security;

import com.auth.filter.LoginAuthenticationFilter;
import com.auth.client.UserServiceClient;
import com.auth.token.JwtTokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
//@EnableWebSecurity
public class SecurityConfig {
//
//    private final UserServiceClient userServiceClient;
//    private final JwtTokenService jwtTokenService;
//
//    @Autowired
//    public SecurityConfig(UserServiceClient userServiceClient, JwtTokenService jwtTokenService) {
//        this.userServiceClient = userServiceClient;
//        this.jwtTokenService = jwtTokenService;
//    }
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(
//            HttpSecurity httpSecurity)
//            throws Exception {
//
//
////        return httpSecurity.csrf(csrf -> csrf.disable())
////                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
////                .and()
////                .authorizeHttpRequests(auth -> auth
////                        .requestMatchers(new AntPathRequestMatcher("/api/v1/auth/login")).permitAll()
////                        .anyRequest().authenticated())
////                .formLogin(form -> form.disable()) // disable default login form
////                .httpBasic(httpBasic -> httpBasic.disable()) // disable basic auth
////                .addFilterBefore(loginJwtFilter, UsernamePasswordAuthenticationFilter.class)
////                .build();
//        return httpSecurity
//                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(new AntPathRequestMatcher("/api/v1/auth/login")).permitAll()
//                        .anyRequest().authenticated())
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .addFilterBefore(customLoginFilter(httpSecurity.getSharedObject(AuthenticationManager.class)),
//                        UsernamePasswordAuthenticationFilter.class)
//                .build();
//    }
//
////    @Bean
////    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
////        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
////    }
//
//    @Bean
//    public LoginAuthenticationFilter customLoginFilter(AuthenticationManager authManager) {
//        LoginAuthenticationFilter loginJwtFilter = new LoginAuthenticationFilter("/api/v1/auth/login", jwtTokenService, userServiceClient, passwordEncoder());
////        loginJwtFilter.setAuthenticationManager(httpSecurity.getSharedObject(AuthenticationManager.class));
//
//        loginJwtFilter.setAuthenticationManager(authManager);
//        return loginJwtFilter;
//    }

    @Autowired
    private JwtTokenService jwtTokenService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthServiceAuthenticationProvider provider,
                                                   AuthenticationManager authManager,
                                                   JwtTokenService jwtTokenService) throws Exception {

        LoginAuthenticationFilter jwtFilter = new LoginAuthenticationFilter("/v1/auth/login", authManager, jwtTokenService);

        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(new AntPathRequestMatcher("/v1/auth/login")).permitAll()
                        .requestMatchers("/v1/register/**").permitAll()
                        .anyRequest().authenticated())
                .authenticationProvider(provider)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       AuthServiceAuthenticationProvider provider) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(provider)
                .build();
    }
}
