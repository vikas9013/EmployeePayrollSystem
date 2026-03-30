package com.vikas.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// NEW CLASS
// Location: src/main/java/com/vikas/security/JwtUtil.java
// Purpose : Generates and validates JWT tokens.
//           Used by AuthController (to issue tokens) and JwtAuthFilter (to verify them).

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /** Generate a signed JWT for the given username and role */
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    /** Extract the username (subject) from a token */
    public String extractUsername(String token) {
        return parseClaims(token).getPayload().getSubject();
    }

    /** Extract the role claim from a token */
    public String extractRole(String token) {
        return parseClaims(token).getPayload().get("role", String.class);
    }

    /** Returns true if the token signature is valid and it has not expired */
    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Jws<Claims> parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
    }
}