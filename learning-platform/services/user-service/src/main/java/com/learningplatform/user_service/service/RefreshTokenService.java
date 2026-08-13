package com.learningplatform.user_service.service;

import com.learningplatform.user_service.entity.RefreshToken;
import com.learningplatform.user_service.entity.User;
import com.learningplatform.user_service.exception.InvalidRefreshTokenException;
import com.learningplatform.user_service.repository.RefreshTokenRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final long refreshTokenExpiration;

    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            @Value("${jwt.refresh-expiration}") long refreshTokenExpiration) {

        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    // Create a new refresh token
    public String createRefreshToken(User user) {

        byte[] randomBytes = new byte[64];

        secureRandom.nextBytes(randomBytes);

        // This is the actual token given to the client
        String rawToken = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        // We store only the hash in the database
        String tokenHash = hashToken(rawToken);

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setToken(tokenHash);
        refreshToken.setUser(user);

        refreshToken.setExpiresAt(
                LocalDateTime.now()
                        .plusNanos(
                                refreshTokenExpiration * 1_000_000
                        )
        );

        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);

        // IMPORTANT:
        // Return raw token to the client,
        // NOT the hash.
        return rawToken;
    }

    // Validate refresh token
    public RefreshToken validateRefreshToken(String rawToken) {

        String tokenHash = hashToken(rawToken);

        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByToken(tokenHash)
                        .orElseThrow(() ->
                                new InvalidRefreshTokenException(
                                        "Invalid refresh token"
                                )
                        );

        // Check whether token was revoked
        if (refreshToken.isRevoked()) {

            throw new InvalidRefreshTokenException(
                    "Refresh token has been revoked"
            );
        }

        // Check whether token expired
        if (refreshToken.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            throw new InvalidRefreshTokenException(
                    "Refresh token has expired"
            );
        }

        return refreshToken;
    }

    // Revoke refresh token
    public void revokeToken(String rawToken) {

        String tokenHash = hashToken(rawToken);

        refreshTokenRepository
                .findByToken(tokenHash)
                .ifPresent(refreshToken -> {

                    refreshToken.setRevoked(true);

                    refreshTokenRepository.save(refreshToken);
                });
    }

    // Convert raw token into SHA-256 hash
    private String hashToken(String token) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            token.getBytes(StandardCharsets.UTF_8)
                    );

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(hash);

        } catch (NoSuchAlgorithmException e) {

            throw new IllegalStateException(
                    "SHA-256 algorithm not available",
                    e
            );
        }
    }
}