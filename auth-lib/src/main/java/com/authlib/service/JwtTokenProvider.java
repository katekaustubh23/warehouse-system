package com.authlib.service;


import com.authlib.config.JwtConfigProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;


@Component
public class JwtTokenProvider {
    private final String jwtSecret;
    private final long accessTokenExpiryMs;

    public JwtTokenProvider(JwtConfigProperties jwtConfigProperties) {
        this.jwtSecret = jwtConfigProperties.getSecret();
        this.accessTokenExpiryMs = jwtConfigProperties.getAccessTokenExpiryMs();
    }

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {

                System.out.println("JWT token expired");
                return false;

            } catch (UnsupportedJwtException ex) {

                System.out.println("Unsupported JWT token");
                return false;

            } catch (MalformedJwtException ex) {

                System.out.println("Invalid JWT token");
                return false;

            } catch (SignatureException ex) {

                System.out.println("Invalid JWT signature");
                return false;

            } catch (IllegalArgumentException ex) {

                System.out.println("JWT claims string is empty");
                return false;
            }

        }

}
