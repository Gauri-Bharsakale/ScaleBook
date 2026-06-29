package com.scalebook.scalebook_backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // In real projects, load this from environment variables, never hardcode it
    private final String SECRET = "this-is-a-very-long-secret-key-keep-it-safe-12345";
    private final SecretKey key =
            Keys.hmacShaKeyFor(
                    SECRET.getBytes(java.nio.charset.StandardCharsets.UTF_8)
            );

    private final long EXPIRATION_MS = 1000 * 60 * 60; // 1 hour

    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(key)
                .compact();
    }

    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false; // invalid signature, expired, or malformed
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}