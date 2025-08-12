package com.auth.token;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Service
public class JwtTokenService {
    private final Key key;
    private final long accessTokenExpiryMs;
    private final long refreshTokenExpiryMs;

    public JwtTokenService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token.expiry-ms}") long accessTokenExpiryMs,
            @Value("${jwt.refresh-token.expiry-ms}") long refreshTokenExpiryMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenExpiryMs = accessTokenExpiryMs;
        this.refreshTokenExpiryMs = refreshTokenExpiryMs;
    }

    /**
     * Generates a JWT access token for the specified user with given roles.
     *
     * @param username the username for which the access token is generated
     * @param roles the roles assigned to the user
     * @return a JWT access token string
     */
    public String generateAccessToken(String username, List<String> roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiryMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generates a JWT refresh token for the specified user.
     *
     * @param username the username for which the refresh token is generated
     * @return a JWT refresh token string
     */
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiryMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Parses a JWT token string and returns a {@link Jws} object containing the payload.
     * <p>
     * You can use this method to verify the signature of a JWT token and extract the payload.
     * <p>
     * The caller must handle {@link ExpiredJwtException}, {@link UnsupportedJwtException}, {@link MalformedJwtException}
     * and {@link SignatureException} that may be thrown by the parser.
     *
     * @param token the JWT token string to parse
     * @return a {@link Jws} object containing the payload
     */
    public Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

}
