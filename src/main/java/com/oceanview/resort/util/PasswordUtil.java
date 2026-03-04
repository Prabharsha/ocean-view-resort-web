package com.oceanview.resort.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Password hashing utility using BCrypt.
 * Replaces Spring Security's BCryptPasswordEncoder.
 */
public final class PasswordUtil {

    private static final int BCRYPT_COST = 12;

    private PasswordUtil() {}

    /** Hashes a plain-text password with BCrypt. */
    public static String hashPassword(String plainPassword) {
        return BCrypt.withDefaults().hashToString(BCRYPT_COST, plainPassword.toCharArray());
    }

    /** Verifies a plain-text password against a BCrypt hash. */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        BCrypt.Result result = BCrypt.verifyer().verify(plainPassword.toCharArray(), hashedPassword);
        return result.verified;
    }
}

