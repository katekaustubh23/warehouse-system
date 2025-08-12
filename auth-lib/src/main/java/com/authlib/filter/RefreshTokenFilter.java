package com.authlib.filter;

import com.authlib.auth.RefreshTokenAuthentication;
import com.authlib.auth.RefreshTokenManager;
import com.authlib.jwt.JwtTokenFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

public class RefreshTokenFilter extends BaseAuthenticationProcessingFilter {
    public RefreshTokenFilter(String defaultFilterProcessesUrl, org.springframework.security.authentication.AuthenticationManager authManager) {
        super(defaultFilterProcessesUrl);
        setAuthenticationManager(authManager);
        setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(defaultFilterProcessesUrl, "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {
        String refreshToken = request.getParameter("refreshToken");

        if (refreshToken == null || !RefreshTokenManager.isValid(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String username = RefreshTokenManager.getUsernameFromRefreshToken(refreshToken);
        return new RefreshTokenAuthentication(refreshToken, username, null);
    }

    @Override
    protected void handleSuccess(HttpServletRequest request,
                                 HttpServletResponse response,
                                 Authentication authResult) throws IOException {
        String username = (String) authResult.getPrincipal();
        String newAccessToken = JwtTokenFactory.createToken(username);
        String newRefreshToken = RefreshTokenManager.createTokenForUser(username);

        response.setContentType("application/json");
        response.getWriter().write("{" +
                "\"accessToken\": \"" + newAccessToken + "\"," +
                "\"refreshToken\": \"" + newRefreshToken + "\"}");
    }
}
