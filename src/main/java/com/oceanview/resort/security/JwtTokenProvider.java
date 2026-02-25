package com.oceanview.resort.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Utility component for JWT token operations.
 *
 * <p>Generates, validates, and parses JSON Web Tokens using the JJWT 0.12 API.
 * Token payloads include the username as subject together with userId and role
 * claims. Access tokens expire after {@code app.jwt.expiration} ms (default 24 h);
 * refresh tokens after {@code app.jwt.refresh-expiration} ms (default 7 d).</p>
 */
@Component
@Slf4j
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration}") long accessTokenExpiration,
            @Value("${app.jwt.refresh-expiration}") long refreshTokenExpiration) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(
                java.util.Base64.getEncoder().encodeToString(secret.getBytes())));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    // ──────────────────────── Token Generation ────────────────────────

    /**
     * Generates an access JWT for the authenticated user.
     *
     * @param authentication the current authentication object
     * @param userId         the user's UUID
     * @param role           the user's role name
     * @return signed JWT string
     */
    public String generateAccessToken(Authentication authentication,
                                      String userId, String role) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return buildToken(userDetails.getUsername(), userId, role, accessTokenExpiration);
    }

    /**
     * Generates a refresh JWT with a longer expiry.
     *
     * @param authentication the current authentication object
     * @param userId         the user's UUID
     * @param role           the user's role name
     * @return signed refresh JWT string
     */
    public String generateRefreshToken(Authentication authentication,
                                       String userId, String role) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return buildToken(userDetails.getUsername(), userId, role, refreshTokenExpiration);
    }

    /**
     * Generates a new access token from a valid refresh token's claims.
     *
     * @param refreshToken the refresh JWT to derive claims from
     * @return a new access JWT
     */
    public String refreshAccessToken(String refreshToken) {
        Claims claims = extractAllClaims(refreshToken);
        return buildToken(
                claims.getSubject(),
                claims.get("userId", String.class),
                claims.get("role", String.class),
                accessTokenExpiration);
    }

    // ──────────────────────── Extraction ────────────────────────

    /**
     * Extracts the username (subject) from a JWT.
     */
    public String getUsernameFromToken(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extracts the userId claim from a JWT.
     */
    public String getUserIdFromToken(String token) {
        return extractAllClaims(token).get("userId", String.class);
    }

    /**
     * Extracts the role claim from a JWT.
     */
    public String getRoleFromToken(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    // ──────────────────────── Validation ────────────────────────

    /**
     * Validates a JWT token.
     *
     * @param token the JWT to validate
     * @return {@code true} if the token is valid and not expired
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Malformed JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    // ──────────────────────── Private helpers ────────────────────────

    private String buildToken(String username, String userId,
                              String role, long expirationMs) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
