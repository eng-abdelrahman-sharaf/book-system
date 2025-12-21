package org.example.backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.backend.config.JwtProperties;
import org.example.backend.model.enums.Role;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Service for JWT token generation, validation, and extraction.
 * Handles both access tokens (short-lived) and refresh tokens (long-lived).
 */
@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long accessTokenExpirationMinutes;
    private final long refreshTokenExpirationDays;

    public JwtService(JwtProperties jwtProperties) {
        String secret = jwtProperties.getSecret();
        // Ensure secret key is at least 32 bytes for HS256
        if (secret.getBytes().length < 32) {
            throw new IllegalArgumentException("JWT secret key must be at least 32 bytes long");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenExpirationMinutes = jwtProperties.getAccessTokenExpirationMinutes();
        this.refreshTokenExpirationDays = jwtProperties.getRefreshTokenExpirationDays();
    }

    // Generates an access token for a user.
    public String generateAccessToken(Integer userId, String username, Role role) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenExpirationMinutes * 60 * 1000);

        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("role", role.name())
                .claim("type", "access")
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    // Generates a refresh token for a user.
    public String generateRefreshToken(Integer userId, String username) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + refreshTokenExpirationDays * 24 * 60 * 60 * 1000L);

        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("type", "refresh")
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    // Validates a JWT token.
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    // Extracts the username from a JWT token.
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Extracts the user ID from a JWT token.
    public Integer extractUserId(String token) {
        return extractClaims(token).get("userId", Integer.class);
    }

    // Extracts the role from a JWT token.
    public Role extractRole(String token) {
        String roleName = extractClaims(token).get("role", String.class);
        return Role.valueOf(roleName);
    }

    // Extracts the token type (access or refresh) from a JWT token.
    public String extractTokenType(String token) {
        return extractClaims(token).get("type", String.class);
    }

    // Extracts all claims from a JWT token.
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
