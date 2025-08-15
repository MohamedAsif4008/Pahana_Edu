package com.pahanaedu.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Utility class for data validation
 * Provides common validation methods for the application
 *
 * Design Pattern: Utility Pattern
 * - Static methods for common validations
 * - Centralized validation logic
 * - Reusable across the application
 *
 * @author Pahana Edu Development Team
 * @version 1.0
 */
public class ValidationUtils {

    // Regular expression patterns for validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[+]?[0-9]{10,15}$"
    );

    private static final Pattern USERNAME_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_]{3,20}$"
    );

    private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile(
            "^[A-Z]{3}[0-9]{3,6}$"
    );

    private static final Pattern ITEM_ID_PATTERN = Pattern.compile(
            "^ITEM[0-9]{3,6}$"
    );

    private static final Pattern BILL_NUMBER_PATTERN = Pattern.compile(
            "^BILL[0-9]{6}$"
    );

    /**
     * Private constructor to prevent instantiation
     */
    private ValidationUtils() {
        // Utility class
    }

    /**
     * Check if string is not null and not empty (after trimming)
     *
     * @param value String to check
     * @return true if string is not null and not empty
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Check if string has minimum length
     *
     * @param value String to check
     * @param minLength Minimum required length
     * @return true if string meets minimum length requirement
     */
    public static boolean hasMinLength(String value, int minLength) {
        return isNotEmpty(value) && value.trim().length() >= minLength;
    }

    /**
     * Check if string has maximum length
     *
     * @param value String to check
     * @param maxLength Maximum allowed length
     * @return true if string is within maximum length
     */
    public static boolean hasMaxLength(String value, int maxLength) {
        return value == null || value.trim().length() <= maxLength;
    }

    /**
     * Check if string length is within range
     *
     * @param value String to check
     * @param minLength Minimum length
     * @param maxLength Maximum length
     * @return true if string length is within range
     */
    public static boolean isLengthInRange(String value, int minLength, int maxLength) {
        return hasMinLength(value, minLength) && hasMaxLength(value, maxLength);
    }

    /**
     * Validate email format
     *
     * @param email Email to validate
     * @return true if email format is valid
     */
    public static boolean isValidEmail(String email) {
        return isNotEmpty(email) && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validate phone number format
     *
     * @param phoneNumber Phone number to validate
     * @return true if phone number format is valid
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (!isNotEmpty(phoneNumber)) {
            return false;
        }

        // Remove spaces and dashes for validation
        String cleanPhone = phoneNumber.replaceAll("[\\s-]", "");
        return PHONE_PATTERN.matcher(cleanPhone).matches();
    }

    /**
     * Validate username format
     *
     * @param username Username to validate
     * @return true if username format is valid
     */
    public static boolean isValidUsername(String username) {
        return isNotEmpty(username) && USERNAME_PATTERN.matcher(username.trim()).matches();
    }

    /**
     * Validate account number format
     *
     * @param accountNumber Account number to validate
     * @return true if account number format is valid
     */
    public static boolean isValidAccountNumber(String accountNumber) {
        return isNotEmpty(accountNumber) && ACCOUNT_NUMBER_PATTERN.matcher(accountNumber.trim()).matches();
    }

    /**
     * Validate item ID format
     *
     * @param itemId Item ID to validate
     * @return true if item ID format is valid
     */
    public static boolean isValidItemId(String itemId) {
        return isNotEmpty(itemId) && ITEM_ID_PATTERN.matcher(itemId.trim()).matches();
    }

    /**
     * Validate bill number format
     *
     * @param billNumber Bill number to validate
     * @return true if bill number format is valid
     */
    public static boolean isValidBillNumber(String billNumber) {
        return isNotEmpty(billNumber) && BILL_NUMBER_PATTERN.matcher(billNumber.trim()).matches();
    }

    /**
     * Validate BigDecimal is positive
     *
     * @param value BigDecimal to validate
     * @return true if value is positive
     */
    public static boolean isPositive(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Validate BigDecimal is non-negative
     *
     * @param value BigDecimal to validate
     * @return true if value is non-negative
     */
    public static boolean isNonNegative(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) >= 0;
    }

    /**
     * Validate integer is positive
     *
     * @param value Integer to validate
     * @return true if value is positive
     */
    public static boolean isPositive(int value) {
        return value > 0;
    }

    /**
     * Validate integer is non-negative
     *
     * @param value Integer to validate
     * @return true if value is non-negative
     */
    public static boolean isNonNegative(int value) {
        return value >= 0;
    }

    /**
     * Validate integer is within range
     *
     * @param value Integer to validate
     * @param min Minimum value (inclusive)
     * @param max Maximum value (inclusive)
     * @return true if value is within range
     */
    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    /**
     * Validate BigDecimal is within range
     *
     * @param value BigDecimal to validate
     * @param min Minimum value (inclusive)
     * @param max Maximum value (inclusive)
     * @return true if value is within range
     */
    public static boolean isInRange(BigDecimal value, BigDecimal min, BigDecimal max) {
        return value != null && min != null && max != null &&
                value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
    }

    /**
     * Sanitize string input (remove dangerous characters)
     *
     * @param input Input string to sanitize
     * @return Sanitized string
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }

        // Remove potentially dangerous characters for SQL injection prevention
        return input.trim()
                .replaceAll("[<>\"'%;()&+]", "")
                .replaceAll("--", "")
                .replaceAll("/\\*", "")
                .replaceAll("\\*/", "");
    }

    /**
     * Validate name format (letters, spaces, apostrophes, hyphens)
     *
     * @param name Name to validate
     * @return true if name format is valid
     */
    public static boolean isValidName(String name) {
        if (!isNotEmpty(name)) {
            return false;
        }

        String trimmedName = name.trim();
        return trimmedName.matches("^[a-zA-Z\\s'.-]+$") && trimmedName.length() <= 100;
    }

    /**
     * Validate address format
     *
     * @param address Address to validate
     * @return true if address format is valid
     */
    public static boolean isValidAddress(String address) {
        return isNotEmpty(address) && hasMaxLength(address, 500);
    }

    /**
     * Validate description format
     *
     * @param description Description to validate
     * @return true if description format is valid
     */
    public static boolean isValidDescription(String description) {
        // Description is optional, but if provided, should not exceed limit
        return description == null || hasMaxLength(description, 1000);
    }

    /**
     * Validate category format
     *
     * @param category Category to validate
     * @return true if category format is valid
     */
    public static boolean isValidCategory(String category) {
        return isNotEmpty(category) && isLengthInRange(category, 2, 50) &&
                category.trim().matches("^[a-zA-Z0-9\\s&-]+$");
    }
}