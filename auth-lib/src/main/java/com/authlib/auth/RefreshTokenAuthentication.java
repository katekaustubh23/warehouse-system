package com.authlib.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class RefreshTokenAuthentication extends AbstractAuthenticationToken {

    private final String refreshToken;
    private final String principal;

    public RefreshTokenAuthentication(String refreshToken, String principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.refreshToken = refreshToken;
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return refreshToken;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
