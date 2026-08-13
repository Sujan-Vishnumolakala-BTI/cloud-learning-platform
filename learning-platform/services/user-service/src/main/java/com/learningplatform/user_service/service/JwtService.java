package com.learningplatform.user_service.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.learningplatform.user_service.entity.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expiration;
    private final long refreshExpiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration,
            @Value("${jwt.refresh-expiration}") long refreshExpiration) {

        this.secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8));

        this.expiration = expiration;
        this.refreshExpiration = refreshExpiration;
    }

    // public String generateToken(String email) {

    // Date now = new Date();
    // Date expiry = new Date(now.getTime() + expiration);

    // return Jwts.builder()
    // .subject(email)
    // .issuedAt(now)
    // .expiration(expiry)
    // .signWith(secretKey)
    // .compact();
    // }
    public String generateToken(User user) {

        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("role", user.getRole().name())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    public String extractEmail(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public String generateRefreshToken(User user) {

        Date now = new Date();
        Date expiry = new Date(
                now.getTime() + refreshExpiration);

        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    // public String extractRole(String token) {
    // return Jwts.parser()
    // .verifyWith(secretKey)
    // .build()
    // .parseSignedClaims(token)
    // .getPayload()
    // .get("role", String.class);
    // }
}