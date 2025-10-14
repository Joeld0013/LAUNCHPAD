package com.launchpad.shared.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret:mySecretKeyForLaunchPadApplicationWhichShouldBeLongEnoughForHS256Algorithm}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // Default 24 hours
    private long defaultExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String userId, String email, String userType, long expirationTime) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("userType", userType);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(String userId, String email, String userType) {
        return generateToken(userId, email, userType, defaultExpiration);
    }

    public Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token has expired");
        } catch (UnsupportedJwtException e) {
            throw new RuntimeException("Unsupported JWT token");
        } catch (MalformedJwtException e) {
            throw new RuntimeException("Invalid JWT token");
        } catch (SignatureException e) {
            throw new RuntimeException("Invalid JWT signature");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("JWT claims string is empty");
        }
    }

    public String extractUserId(String token) {
        return extractClaims(token).get("userId", String.class);
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractUserType(String token) {
        return extractClaims(token).get("userType", String.class);
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractClaims(token).getExpiration().before(new Date());
        } catch (RuntimeException e) {
            return true;
        }
    }

    public boolean validateToken(String token) {
        try {
            extractClaims(token);
            return !isTokenExpired(token);
        } catch (RuntimeException e) {
            return false;
        }
    }
}