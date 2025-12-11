package com.loan.poc.accountservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

/**
 * Utility class for validating JWT tokens.
 * This service DOES NOT generate tokens. Token generation happens only in the User Service.
 *
 * The Account Service only:
 * 1. Validates JWT signature
 * 2. Extracts claims such as username, expiration etc.
 */
@Component
public class JWTUtil {

    @Value("${jwt.secret}")
    private String secret;

    /**
     * Convert Base64-encoded secret string into a signing key.
     *
     * @return SecretKey object used for HS256 signature verification.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extracts username (subject) from the token.
     *
     * @param token JWT token
     * @return username stored in 'sub' claim
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Parses and validates the token signature.
     * Throws SignatureException or ExpiredJwtException if invalid.
     *
     * @param token JWT token
     * @return Claims object containing payload values
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())   // verify token with secret key
                .build()
                .parseSignedClaims(token)      // parse and validate signature
                .getPayload();
    }

    /**
     * Checks whether the JWT is expired.
     */
    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new java.util.Date());
    }

    /**
     * Validates whether token belongs to the given user.
     */
    public boolean validateToken(String token, String expectedUsername) {
        String username = extractUsername(token);
        return username.equals(expectedUsername) && !isTokenExpired(token);
    }
}
