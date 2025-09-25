package com.authlib.config;

import com.authlib.filter.JwtAuthenticationFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@ConditionalOnClass(HttpSecurity.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class AuthLibAutoConfiguration {

    @Bean
    public JwtAuthenticationFilter internalAuthFilter() {
        return new JwtAuthenticationFilter();
    }

    // Defines the application's SecurityFilterChain bean.
    // - Disables CSRF & default form login (we use JWT instead).
    // - Requires authentication for all requests.
    // - Adds our JwtAuthenticationFilter before UsernamePasswordAuthenticationFilter
    //   so JWT tokens are validated early.
    // This replaces the old WebSecurityConfigurerAdapter and tells Spring Security
    // how to build the filter chain that protects every request.
    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter internalAuthFilter) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .addFilterBefore(internalAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
