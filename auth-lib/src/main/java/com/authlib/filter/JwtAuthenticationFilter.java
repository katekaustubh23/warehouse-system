package com.authlib.filter;

import com.authlib.service.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;

//import org.springframework.stereotype.Component;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


//@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${internal.secret}")
    private String internalSecret;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String internalHeader = request.getHeader("X-Internal-Secret");

        if (internalHeader != null && internalHeader.equals(internalSecret)) {
            // Trusted internal call (from Gateway)
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            "internal-service",
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_INTERNAL"))
                    );
            SecurityContextHolder.getContext().setAuthentication(auth);
            try {
                filterChain.doFilter(request, response);
            } finally {
                SecurityContextHolder.clearContext();
            }
            return;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Invalid internal secret\"}");
    }
}
