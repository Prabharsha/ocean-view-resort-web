package com.oceanview.resort.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Simple JWT utility using HMAC-SHA256. No external JWT library needed.
 * Replaces Spring Security's JwtTokenProvider.
 */
public final class JwtUtil {

    private JwtUtil() {}

    /** Generates a JWT token. */
    public static String generateToken(String subject, String userId, String role,
                                       long expirationMs, String secret) {
        long now = System.currentTimeMillis();
        long exp = now + expirationMs;

        String header = base64UrlEncode("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        String payload = base64UrlEncode(String.format(
                "{\"sub\":\"%s\",\"userId\":\"%s\",\"role\":\"%s\",\"iat\":%d,\"exp\":%d}",
                subject, userId, role, now / 1000, exp / 1000));

        String content = header + "." + payload;
        String signature = base64UrlEncode(hmacSha256(content, secret));

        return content + "." + signature;
    }

    /** Validates and parses a JWT token. Throws if invalid/expired. */
    public static Map<String, Object> validateToken(String token, String secret) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) throw new IllegalArgumentException("Invalid JWT format");

        String content = parts[0] + "." + parts[1];
        String expectedSig = base64UrlEncode(hmacSha256(content, secret));

        if (!expectedSig.equals(parts[2])) {
            throw new SecurityException("Invalid JWT signature");
        }

        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);

        // Simple JSON parsing (avoid needing Gson dependency here)
        Map<String, Object> claims = new HashMap<>();
        payloadJson = payloadJson.replaceAll("[{}\"]", "");
        for (String pair : payloadJson.split(",")) {
            String[] kv = pair.split(":", 2);
            if (kv.length == 2) {
                String key = kv[0].trim();
                String val = kv[1].trim();
                try {
                    claims.put(key, Long.parseLong(val));
                } catch (NumberFormatException e) {
                    claims.put(key, val);
                }
            }
        }

        // Check expiration
        Object expObj = claims.get("exp");
        if (expObj instanceof Long) {
            long exp = (Long) expObj;
            if (exp < System.currentTimeMillis() / 1000) {
                throw new SecurityException("JWT token expired");
            }
        }

        return claims;
    }

    private static String base64UrlEncode(String input) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    private static String base64UrlEncode(byte[] input) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(input);
    }

    private static byte[] hmacSha256(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(
                    secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("HMAC-SHA256 failed", e);
        }
    }
}

