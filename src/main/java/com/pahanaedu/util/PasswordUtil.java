package com.pahanaedu.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for password hashing and verification
 * Uses SHA-256 with salt for security
 * Demonstrates utility pattern and security best practices
 *
 * @author Pahana Edu Development Team
 * @version 1.0
 */
public class PasswordUtil {

    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    private static final String DELIMITER = ":";

    /**
     * Private constructor to prevent instantiation
     * This is a utility class with only static methods
     */
    private PasswordUtil() {
        // Prevent instantiation
    }

    /**
     * Generate a random salt for password hashing
     *
     * @return Base64 encoded salt string
     */
    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hash a password with a random salt
     *
     * @param plainPassword The plain text password to hash
     * @return String containing salt and hash separated by colon
     * @throws RuntimeException if hashing fails
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        try {
            String salt = generateSalt();
            String hash = hashPasswordWithSalt(plainPassword, salt);
            return salt + DELIMITER + hash;

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Password hashing failed: " + e.getMessage(), e);
        }
    }

    /**
     * Hash password with provided salt
     *
     * @param password Plain text password
     * @param salt Salt string
     * @return Base64 encoded hash
     * @throws NoSuchAlgorithmException if algorithm not available
     */
    private static String hashPasswordWithSalt(String password, String salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);

        // Add salt to the hash
        md.update(salt.getBytes());

        // Hash the password
        byte[] hashedPassword = md.digest(password.getBytes());

        // Return Base64 encoded hash
        return Base64.getEncoder().encodeToString(hashedPassword);
    }

    /**
     * Verify if a plain password matches the stored hash
     *
     * @param plainPassword The plain text password to verify
     * @param storedHash The stored hash (salt:hash format)
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null) {
            return false;
        }

        try {
            // Split stored hash into salt and hash parts
            String[] parts = storedHash.split(DELIMITER);
            if (parts.length != 2) {
                System.err.println("Invalid stored hash format");
                return false;
            }

            String salt = parts[0];
            String expectedHash = parts[1];

            // Hash the provided password with the same salt
            String actualHash = hashPasswordWithSalt(plainPassword, salt);

            // Compare hashes
            return expectedHash.equals(actualHash);

        } catch (Exception e) {
            System.err.println("Error verifying password: " + e.getMessage());
            return false;
        }
    }

    /**
     * Generate a random password for testing purposes
     *
     * @param length Length of password to generate
     * @return Random password string
     */
    public static String generateRandomPassword(int length) {
        if (length < 4) {
            throw new IllegalArgumentException("Password length must be at least 4 characters");
        }

        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }

        return password.toString();
    }

    /**
     * Validate password strength
     *
     * @param password Password to validate
     * @return true if password meets minimum requirements
     */
    public static boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            if (Character.isLowerCase(c)) hasLower = true;
            if (Character.isDigit(c)) hasDigit = true;
        }

        return hasUpper && hasLower && hasDigit;
    }
}