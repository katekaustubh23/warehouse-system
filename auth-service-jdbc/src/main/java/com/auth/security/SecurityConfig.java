package com.auth.security;

import com.auth.filter.LoginAuthenticationFilter;
import com.auth.client.UserServiceClient;
import com.auth.token.JwtTokenService;

import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Slf4j
@Configuration
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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthServiceAuthenticationProvider provider,
                                                   AuthenticationManager authManager,
                                                   LoginAuthenticationFilter loginFilter) throws Exception {

        loginFilter.setAuthManager(authManager);
        log.info("Configuring security filter chain with custom login filter");
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {}) // use default CORS configuration from corsConfigurationSource bean
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v1/auth/login").permitAll()
                        .requestMatchers("/v1/authenticate/**").permitAll()
                        .anyRequest().authenticated())
                .authenticationProvider(provider)
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class)
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200")); // frontend URL
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // 🔥 REQUIRED for cookies

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
