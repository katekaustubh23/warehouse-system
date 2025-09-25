package com.gateway.filter;

import com.authlib.service.JwtTokenProvider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;


@Component
public class JwtAuthenticationGatewayFilter implements GlobalFilter, Ordered {
    private final JwtTokenProvider tokenProvider;

    // read from application.yml
    @Value("${internal.secret}")
    private String internalSecret;

    public JwtAuthenticationGatewayFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 1) Public endpoints (add more patterns as needed)
        if (path.equals("/login") || path.startsWith("/auth/") || path.startsWith("/public/")) {
            return chain.filter(exchange);
        }

        // 2) JWT expected for everything else
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);
        boolean valid;
        try {
            valid = tokenProvider.validateToken(token);
        } catch (Exception ex) {
            valid = false;
        }

        if (!valid) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 3) Forward with X-Internal-Secret
        ServerHttpRequest mutated = exchange.getRequest().mutate()
                .header("X-Internal-Secret", internalSecret)
                .build();
        return chain.filter(exchange.mutate().request(mutated).build());
    }

    @Override
    public int getOrder() {
        return -1; // run early
    }
}
