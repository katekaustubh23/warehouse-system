package com.authlib.filter;


import com.authlib.auth.RefreshTokenManager;
import com.authlib.exception.InvalidTokenException;
import com.authlib.token.JwtAuthenticationToken;
import com.authlib.jwt.JwtTokenFactory;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;


import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final JwtTokenService jwtTokenService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthenticationFilter(String loginEndpoint,
                                   AuthenticationManager authenticationManager,
                                   JwtTokenService jwtTokenService) {
        super(new AntPathRequestMatcher(loginEndpoint, "POST"));
        setAuthenticationManager(authenticationManager);
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
            throws AuthenticationException, IOException {
        Map<String, String> creds = objectMapper.readValue(request.getInputStream(), Map.class);
        String username = creds.get("username");
        String password = creds.get("password");

        JwtAuthenticationToken authRequest = new JwtAuthenticationToken(username, password);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult)
            throws IOException {
        JwtToken jwtToken = jwtTokenService.createToken((org.springframework.security.core.userdetails.UserDetails) authResult.getPrincipal());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), jwtToken);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Authentication failed: " + failed.getMessage());
    }
}
