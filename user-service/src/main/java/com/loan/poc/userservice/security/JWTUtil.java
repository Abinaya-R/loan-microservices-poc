package com.loan.poc.userservice.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

@Component
public class JWTUtil {

    /**
     * Secret key stored in application.properties
     * This MUST remain constant. Never auto-generate inside code.
     */
    @Value("${jwt.secret}")
    private String secretkey;

    // Token validity period (in milliseconds), from application.properties
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Generates a new JWT token for the authenticated user.
     * Adds roles as part of the token claims.
     *
     * @param userDetails User details object provided by Spring Security
     * @return Signed JWT token as a string
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities()); // Store roles inside token
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Creates and signs a JWT token with:
     * - Claims (payload)
     * - Subject (username)
     * - Issued time
     * - Expiration time
     * - HS256 signing algorithm
     *
     * @param claims  Additional data to store in token
     * @param subject Username
     * @return Final JWT token string
     */
    private String createToken(Map<String, Object> claims, String username) {

        return Jwts.builder()
                .claims()
                .add(claims) // Add custom claims
                .subject(username)// Set username
                .issuedAt(new Date(System.currentTimeMillis())) // Token creation timestamp
                .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 30))// Token expiry timestamp
                .and()
                .signWith(getKey()) // Sign token using HS256
                .compact();// Generate final JWT string

    }

    private SecretKey getKey() {
        byte[] keyBytes = secretkey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extracts the username (subject) from the JWT token.
     *
     * @param token The JWT token
     * @return The username stored in the token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Generic method to extract any claim from the token using a resolver function.
     *
     * @param token          The JWT token
     * @param claimsResolver The function to apply on the retrieved Claims object
     * @return The resolved claim value
     * @param <T> Type of the claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parses and validates the JWT token and retrieves all claims.
     *
     * @param token The JWT token
     * @return Claims (payload) of the token
     *
     *         Note: This method will throw exceptions if token is invalid, expired,
     *         or tampered.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())// Set signing key for validation
                .build()
                .parseSignedClaims(token)// Parses and validates token signature
                .getPayload(); // Returns token claims
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUsername(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

}
