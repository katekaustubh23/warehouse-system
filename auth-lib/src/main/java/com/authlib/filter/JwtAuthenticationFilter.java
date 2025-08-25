package com.authlib.filter;

import com.authlib.service.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;

//import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


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
            filterChain.doFilter(request, response);
            return;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
