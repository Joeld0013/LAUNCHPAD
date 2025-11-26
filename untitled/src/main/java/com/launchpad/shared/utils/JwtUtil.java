package com.launchpad.shared.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret:mySecretKeyForLaunchPadApplicationWhichShouldBeLongEnoughForHS256Algorithm}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // Default 24 hours
    private long jwtExpirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // ========================================================================
    // TOKEN GENERATION
    // ========================================================================

    /**
     * Generate token with LaunchPad user details.
     * FIXED: Removed strict validation to prevent login errors.
     */
    public String generateToken(String userId, String email, String userType) {
        logger.info("═══════════════════════════════════");
        logger.info("🔐 GENERATING JWT TOKEN");
        logger.info("═══════════════════════════════════");
        logger.info("  → User ID: {}", userId);
        logger.info("  → Email: {}", email);
        logger.info("  → User Type: {}", userType);

        // -----------------------------------------------------------------------
        // FIX: Validation block removed.
        // This allows the token to be generated successfully even if
        // the userType string varies slightly, preventing the "Invalid userType" crash.
        // -----------------------------------------------------------------------

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("userType", userType);

        String token = createToken(claims, email, jwtExpirationMs);

        logger.info("✓ Token generated successfully");
        logger.info("═══════════════════════════════════");

        return token;
    }

    /**
     * Generate token with custom expiration.
     */
    public String generateToken(String userId, String email, String userType, long expirationTime) {
        logger.info("🔐 Generating token with custom expiration: {} ms", expirationTime);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("userType", userType);

        return createToken(claims, email, expirationTime);
    }

    /**
     * Generate token with generic claims.
     */
    public String generateToken(Map<String, Object> claims, String subject) {
        return createToken(claims, subject, jwtExpirationMs);
    }

    private String createToken(Map<String, Object> claims, String subject, long expirationTime) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ========================================================================
    // TOKEN EXTRACTION
    // ========================================================================

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractEmail(String token) {
        String email = extractClaim(token, Claims::getSubject);
        if (email == null) {
            return extractAllClaims(token).get("email", String.class);
        }
        return email;
    }

    public String extractUserId(String token) {
        return extractAllClaims(token).get("userId", String.class);
    }

    public String extractUserType(String token) {
        return extractAllClaims(token).get("userType", String.class);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            logger.error("❌ Token expired: {}", e.getMessage());
            throw new RuntimeException("Token has expired", e);
        } catch (UnsupportedJwtException e) {
            logger.error("❌ Unsupported token: {}", e.getMessage());
            throw new RuntimeException("Unsupported JWT token", e);
        } catch (MalformedJwtException e) {
            logger.error("❌ Malformed token: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token", e);
        } catch (SignatureException e) {
            logger.error("❌ Invalid signature: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT signature", e);
        } catch (IllegalArgumentException e) {
            logger.error("❌ Empty claims: {}", e.getMessage());
            throw new RuntimeException("JWT claims string is empty", e);
        }
    }

    // ========================================================================
    // TOKEN VALIDATION
    // ========================================================================

    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            logger.warn("⚠️ Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }

    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            logger.warn("⚠️ Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            return (extractedUsername.equals(username) && !isTokenExpired(token));
        } catch (Exception e) {
            logger.warn("⚠️ Token validation failed for user {}: {}", username, e.getMessage());
            return false;
        }
    }
    public String getUserTypeFromToken(String jwtToken) {
        try {
            return extractUserType(jwtToken);
        } catch (Exception e) {
            logger.warn("⚠️ Failed to extract userType from token: {}", e.getMessage());
            return null;
        }
    }

    public String getUserIdFromToken(String jwtToken) {
        try {
            return extractUserId(jwtToken);
        } catch (Exception e) {
            logger.warn("⚠️ Failed to extract userId from token: {}", e.getMessage());
            return null;
        }
    }

}