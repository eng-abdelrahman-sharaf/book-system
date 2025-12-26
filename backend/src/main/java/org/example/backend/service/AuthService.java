package org.example.backend.service;

import org.example.backend.Repository.RefreshTokenRepository;
import org.example.backend.Repository.UserRepository;
import org.example.backend.model.dto.LoginResponse;
import org.example.backend.model.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Service for handling authentication operations including login, token refresh, and logout.
 * Implements JWT-based stateless authentication with refresh token rotation.
 */
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ShoppingCartService shoppingCartService;

    public AuthService(
            ShoppingCartService shoppingCartService,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.shoppingCartService = shoppingCartService;
    }


    // Refreshes an access token using a valid refresh token.
    // Implements token rotation: old refresh token is deleted and a new one is issued.
    @Transactional
    public LoginResponse refreshToken(String refreshToken) {
        if (!jwtService.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String tokenType = jwtService.extractTokenType(refreshToken);
        if (!"refresh".equals(tokenType)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        Integer userId = jwtService.extractUserId(refreshToken);
        String username = jwtService.extractUsername(refreshToken);

        // Verify the refresh token exists in the database
        if (!refreshTokenRepository.existsByToken(refreshToken)) {
            throw new IllegalArgumentException("Refresh token not found or already used");
        }

        // Get user to retrieve role
        User user = userRepository.findById(userId);

        // Extract expiration date from the old refresh token
        Date oldTokenExpiration = jwtService.extractExpiration(refreshToken);
        LocalDateTime expiresAt = LocalDateTime.ofInstant(
                oldTokenExpiration.toInstant(),
                ZoneId.systemDefault()
        );

        // Token rotation: delete old refresh token and issue new ones
        refreshTokenRepository.deleteByToken(refreshToken);

        String newAccessToken = jwtService.generateAccessToken(userId, username, user.getRole());
        String newRefreshToken = jwtService.generateRefreshToken(userId, username, oldTokenExpiration);

        refreshTokenRepository.save(userId, newRefreshToken, expiresAt);

        return new LoginResponse(newAccessToken, newRefreshToken);
    }


    @Transactional
    public void logout(String refreshToken) {
        int userId = jwtService.extractUserId(refreshToken);
        if (!jwtService.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String tokenType = jwtService.extractTokenType(refreshToken);
        if (!"refresh".equals(tokenType)) {
            throw new IllegalArgumentException("Invalid token type");
        }

        if (!refreshTokenRepository.existsByToken(refreshToken)) {
            throw new IllegalArgumentException("Refresh token already invalidated");
        }

        refreshTokenRepository.deleteByToken(refreshToken);
        System.out.println(userId);
        shoppingCartService.clearCart(userId);
    }



}
