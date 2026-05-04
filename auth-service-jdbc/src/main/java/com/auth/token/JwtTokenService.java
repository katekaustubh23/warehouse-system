package com.auth.token;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Key;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class JwtTokenService {
    private final Key key;
    private final String accessTokenExpiryMs;
    private final String refreshTokenExpiryMs;

    public JwtTokenService(JwtConfigProperties jwtConfigProperties) {
        this.key = Keys.hmacShaKeyFor(jwtConfigProperties.getSecret().getBytes());
        this.accessTokenExpiryMs = jwtConfigProperties.getAccessTokenExpiryMs();
        this.refreshTokenExpiryMs = jwtConfigProperties.getRefreshTokenExpiryMs();
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
                .claim("type", "access")
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(accessTokenExpiryMs)))
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
                .claim("type", "refresh")
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(refreshTokenExpiryMs)))
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

    public Claims extractClaims(String token) {
        return parseToken(token).getBody();
    }

    public boolean isExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public String getUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isRefreshToken(String token) {
        Object type = extractClaims(token).get("type");
        log.info("type claim: {}", type.toString());
        return "refresh".equals(type);
    }

    public boolean isValidAccessToken(String token) {
        try {
            Claims claims = extractClaims(token);

            // 1. check type
            String type = claims.get("type", String.class);
            if (!"access".equals(type)) {
                return false;
            }

            // 2. check expiration
            return !isExpired(token);

        } catch (ExpiredJwtException e) {
            log.warn("Token expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported token: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("Malformed token: {}", e.getMessage());
        } catch (SignatureException e) {
            log.warn("Invalid signature: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("Token is empty or null: {}", e.getMessage());
        }

        return false;
    }
}
