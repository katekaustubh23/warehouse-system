package com.authlib.util;

public class JwtUtil  {
        private final SecretKey secretKey;
        private final long expirationMillis;

        public JwtUtil(String secret, long expirationMillis) {
            this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
            this.expirationMillis = expirationMillis;
        }

        public String generateToken(UserDetails userDetails) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("roles", userDetails.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));

            Date now = new Date();
            Date expiry = new Date(now.getTime() + expirationMillis);

            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(userDetails.getUsername())
                    .setIssuedAt(now)
                    .setExpiration(expiry)
                    .signWith(secretKey, SignatureAlgorithm.HS256)
                    .compact();
        }

        public String getUsernameFromToken(String token) {
            return parseClaims(token).getSubject();
        }

        public List<String> getRolesFromToken(String token) {
            Claims claims = parseClaims(token);
            return claims.get("roles", List.class);
        }

        public boolean isTokenExpired(String token) {
            return parseClaims(token).getExpiration().before(new Date());
        }

        private Claims parseClaims(String token) {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }
}
