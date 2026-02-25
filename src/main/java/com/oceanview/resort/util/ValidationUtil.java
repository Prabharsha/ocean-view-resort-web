package com.oceanview.resort.util;

import java.time.LocalDate;

/**
 * Utility class for common validation operations.
 * Provides reusable validation methods for the application.
 */
public final class ValidationUtil {

    private ValidationUtil() {
        // Utility class – prevent instantiation.
    }

    /**
     * Validates that check-out date is after check-in date.
     *
     * @param checkIn  the check-in date
     * @param checkOut the check-out date
     * @return true if the date range is valid
     */
    public static boolean isValidDateRange(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            return false;
        }
        return checkOut.isAfter(checkIn);
    }

    /**
     * Validates that a string is not null or blank.
     *
     * @param value the string to validate
     * @return true if the string is not blank
     */
    public static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Validates an email address format.
     *
     * @param email the email to validate
     * @return true if the email format is valid
     */
    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * Validates a phone number format.
     *
     * @param phone the phone number to validate
     * @return true if the phone format is valid
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null) {
            return false;
        }
        return phone.matches("^[+]?[0-9]{10,15}$");
    }
}
