package com.authlib.jwt;

import com.authlib.exception.InvalidTokenException;
import com.authlib.token.JwtAuthenticationToken;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collections;
import java.util.Date;

@Component
public class JwtTokenFactory {

    private final Key key;

    @Value("${jwt.token.expiration-minutes:720}")
    private long expirationMinutes;

    public JwtTokenFactory(JWTEncryptionKey jwtEncryptionKey) {
        this.key = jwtEncryptionKey.getKey();
    }

    public String createToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMinutes * 60 * 1000); // Convert to milliseconds

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public JwtAuthenticationToken verify(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();

            return new JwtAuthenticationToken(
                    token,
                    new TokenBasedUserDetails(username),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );

        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException("Token has expired", e);
        } catch (JwtException e) {
            throw new InvalidTokenException("Invalid JWT token", e);
        }
    }
}
