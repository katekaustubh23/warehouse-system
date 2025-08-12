package com.authlib.config;

import com.authlib.filter.JwtAuthenticationFilter;
import com.authlib.filter.RefreshTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    // Update these values as needed
    private static final String SECRET_KEY = "your-256-bit-secret-your-256-bit-secret"; // 32+ chars
    private static final long EXPIRATION_TIME = 3600000; // 1 hour in ms

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(SECRET_KEY, EXPIRATION_TIME);
    }

    @Bean
    public JwtTokenService jwtTokenService(JwtUtil jwtUtil) {
        return new JwtTokenService(jwtUtil);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(); // replace with actual DB-backed service in production
    }

    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider(UserDetailsService userDetailsService,
                                                               PasswordEncoder passwordEncoder) {
        return new JwtAuthenticationProvider((CustomUserDetailsService) userDetailsService, passwordEncoder);
    }

    @Bean
    public AuthenticationManager authenticationManager(JwtAuthenticationProvider provider) {
        return new ProviderManager(List.of(provider));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtTokenService jwtTokenService,
                                                   AuthenticationManager authManager) throws Exception {

        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter("/login", authManager, jwtTokenService);

        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilter(jwtFilter); // Filter is only for login, not JWT validation post-login

        return http.build();
    }
}
