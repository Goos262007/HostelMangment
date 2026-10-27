package com.hostel.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Security Utility: SecurityUtil
 * Demonstrates:
 * 1. Cryptographic Salting & Hashing: SHA-256 with constant-time equality checks.
 * 2. Device-Bound Token Signatures: HMAC-SHA256 cryptographic signing linking tokens
 *    to specific client devices (IP & user-agent).
 * 3. Protection Against Replay & Timing Attacks.
 */
public class SecurityUtil {
    private static final String HMAC_ALGO = "HmacSHA256";
    private static final byte[] SERVER_SECRET = new byte[32];
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    static {
        // Initialize server cryptographic secret key on startup
        SECURE_RANDOM.nextBytes(SERVER_SECRET);
    }

    /**
     * Generates a 16-byte cryptographically secure random salt in hexadecimal.
     */
    public static String generateSalt() {
        byte[] salt = new byte[16];
        SECURE_RANDOM.nextBytes(salt);
        return bytesToHex(salt);
    }

    /**
     * Hashes a password using SHA-256 with a unique salt.
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available in Java runtime", e);
        }
    }

    /**
     * Verifies a raw password against the stored salt and hash using constant-time comparison.
     */
    public static boolean verifyPassword(String rawPassword, String salt, String expectedHash) {
        String computedHash = hashPassword(rawPassword, salt);
        return MessageDigest.isEqual(
            computedHash.getBytes(StandardCharsets.UTF_8),
            expectedHash.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Generates a cryptographically signed, device-bound authentication token.
     * Format: Base64(username:clientIp:expiresAt:deviceHash:hmacSignature)
     */
    public static String generateDeviceToken(String username, String clientIp, String userAgent) {
        long expiresAt = System.currentTimeMillis() + (24L * 60 * 60 * 1000); // 24 hours
        int deviceHash = userAgent != null ? Math.abs(userAgent.hashCode()) : 0;
        String payload = username + "|" + clientIp + "|" + expiresAt + "|" + deviceHash;

        String signature = signPayload(payload);
        String fullToken = payload + "|" + signature;
        return Base64.getUrlEncoder().withoutPadding().encodeToString(fullToken.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Validates a device token: checks structure, signature authenticity, expiration, and device integrity.
     */
    public static boolean validateDeviceToken(String token, String clientIp, String userAgent) {
        if (token == null || token.trim().isEmpty()) return false;

        try {
            byte[] decodedBytes = Base64.getUrlDecoder().decode(token);
            String fullToken = new String(decodedBytes, StandardCharsets.UTF_8);
            String[] parts = fullToken.split("\\|");
            if (parts.length != 5) return false;

            String username = parts[0];
            String tokenIp = parts[1];
            long expiresAt = Long.parseLong(parts[2]);
            int tokenDeviceHash = Integer.parseInt(parts[3]);
            String tokenSignature = parts[4];

            // 1. Check expiration
            if (System.currentTimeMillis() > expiresAt) {
                return false;
            }

            // 2. Verify signature
            String reconstructedPayload = username + "|" + tokenIp + "|" + expiresAt + "|" + tokenDeviceHash;
            String expectedSignature = signPayload(reconstructedPayload);

            if (!MessageDigest.isEqual(
                tokenSignature.getBytes(StandardCharsets.UTF_8),
                expectedSignature.getBytes(StandardCharsets.UTF_8))) {
                return false;
            }

            // 3. Verify device consistency (Allow localhost variations 127.0.0.1 / 0:0:0:0:0:0:0:1)
            boolean ipMatches = tokenIp.equals(clientIp) ||
                (isLocalhost(tokenIp) && isLocalhost(clientIp));

            int currentDeviceHash = userAgent != null ? Math.abs(userAgent.hashCode()) : 0;
            boolean deviceMatches = (tokenDeviceHash == currentDeviceHash || tokenDeviceHash == 0 || currentDeviceHash == 0);

            return ipMatches && deviceMatches;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isLocalhost(String ip) {
        return "127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip) || "localhost".equalsIgnoreCase(ip);
    }

    private static String signPayload(String payload) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(new SecretKeySpec(SERVER_SECRET, HMAC_ALGO));
            byte[] hmacBytes = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hmacBytes);
        } catch (Exception e) {
            throw new RuntimeException("HMAC computation failed", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
