package com.auth.security;

import com.auth.config.PropertiesConfig;
import com.auth.filter.LoginAuthenticationFilter;
import com.auth.client.UserServiceClient;
import com.auth.token.JwtTokenService;

import lombok.RequiredArgsConstructor;
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
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final PropertiesConfig prop;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthServiceAuthenticationProvider provider,
                                                   AuthenticationManager authManager,
                                                   LoginAuthenticationFilter loginFilter) throws Exception {

        loginFilter.setAuthManager(authManager);
        log.info("Configuring security filter chain with custom login filter");
        return http
                .csrf(csrf -> csrf.disable())
//                .csrf(csrf -> csrf
//                        .ignoringRequestMatchers("/v1/auth/login", "/v1/authenticate/**") // disable CSRF for login endpoint
//                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())) // use cookies for CSRF tokens, allowing JavaScript to read them if needed
//                .cors(cors -> {}) // use default CORS configuration from corsConfigurationSource bean
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

}
