package com.pahanaedu.util;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Validation Utils Tests")
public class ValidationUtilsTest {

    @BeforeAll
    static void setUp() {
        System.out.println("=".repeat(60));
        System.out.println("Starting ValidationUtils Tests");
        System.out.println("=".repeat(60));
    }

    @AfterAll
    static void tearDown() {
        System.out.println("=".repeat(60));
        System.out.println("ValidationUtils Tests Completed");
        System.out.println("=".repeat(60));
    }

    // Email Validation Tests
    @Test
    @Order(1)
    @DisplayName("Test Valid Email Formats")
    void testValidEmails() {
        System.out.println("\n1. Testing valid email formats...");
        
        String[] validEmails = {
            "test@email.com",
            "user.name@domain.co.uk",
            "firstname.lastname@company.org",
            "admin@pahanaedu.com",
            "student123@university.edu.lk"
        };
        
        for (String email : validEmails) {
            assertTrue(ValidationUtils.isValidEmail(email), 
                "Email should be valid: " + email);
        }
        
        System.out.println("   ✓ All valid emails passed validation");
    }

    @Test
    @Order(2)
    @DisplayName("Test Invalid Email Formats")
    void testInvalidEmails() {
        System.out.println("\n2. Testing invalid email formats...");
        
        String[] invalidEmails = {
            null,
            "",
            "invalid-email",
            "@domain.com",
            "email@",
            "email@.com",
            "email.domain.com",
            "email@domain",
            "email@@domain.com",
            "email@domain..com"
        };
        
        for (String email : invalidEmails) {
            assertFalse(ValidationUtils.isValidEmail(email), 
                "Email should be invalid: " + email);
        }
        
        System.out.println("   ✓ All invalid emails correctly rejected");
    }

    // Phone Number Validation Tests
    @Test
    @Order(3)
    @DisplayName("Test Valid Phone Numbers")
    void testValidPhoneNumbers() {
        System.out.println("\n3. Testing valid phone numbers...");

        String[] validPhones = {
                "+94771234567",
                "+94701234567",
                "+94112345678",
                "0771234567",
                "0112345678",
                "+94 77 123 4567",
                "+94-77-123-4567",
                "94771234567"  // Added: International format without + prefix
        };

        for (String phone : validPhones) {
            assertTrue(ValidationUtils.isValidPhoneNumber(phone),
                    "Phone should be valid: " + phone);
        }

        System.out.println("   ✓ All valid phone numbers passed validation");
    }

    @Test
    @Order(4)
    @DisplayName("Test Invalid Phone Numbers")
    void testInvalidPhoneNumbers() {
        System.out.println("\n4. Testing invalid phone numbers...");

        String[] invalidPhones = {
                null,
                "",
                "123",
                "abcdefghij",
                "+1234567890123456", // Too long
                "+94", // Too short
                "077123", // Incomplete
                "+94881234567", // Invalid operator code
                "++94771234567", // Double plus
                "947712345678"  // Changed: Too many digits for international without +
        };

        for (String phone : invalidPhones) {
            assertFalse(ValidationUtils.isValidPhoneNumber(phone),
                    "Phone should be invalid: " + phone);
        }

        System.out.println("   ✓ All invalid phone numbers correctly rejected");
    }

    // Account Number Validation Tests
    @Test
    @Order(5)
    @DisplayName("Test Valid Account Numbers")
    void testValidAccountNumbers() {
        System.out.println("\n5. Testing valid account numbers...");
        
        String[] validAccounts = {
            "ACC001",
            "ACC999999",
            "CUS123",
            "ADM001",
            "STD12345"
        };
        
        for (String account : validAccounts) {
            assertTrue(ValidationUtils.isValidAccountNumber(account), 
                "Account number should be valid: " + account);
        }
        
        System.out.println("   ✓ All valid account numbers passed validation");
    }

    @Test
    @Order(6)
    @DisplayName("Test Invalid Account Numbers")
    void testInvalidAccountNumbers() {
        System.out.println("\n6. Testing invalid account numbers...");
        
        String[] invalidAccounts = {
            null,
            "",
            "AC1", // Too short
            "ACCOUNT1234567890", // Too long
            "123456", // No letters
            "ABCDEF", // No numbers
            "acc001", // Lowercase
            "ACC-001", // Special characters
            "ACC 001" // Spaces
        };
        
        for (String account : invalidAccounts) {
            assertFalse(ValidationUtils.isValidAccountNumber(account), 
                "Account number should be invalid: " + account);
        }
        
        System.out.println("   ✓ All invalid account numbers correctly rejected");
    }

    // String Validation Tests
    @Test
    @Order(7)
    @DisplayName("Test String Validation")
    void testStringValidation() {
        System.out.println("\n7. Testing string validation...");
        
        // Test non-empty strings
        assertTrue(ValidationUtils.isNotEmpty("Valid String"), 
            "Non-empty string should be valid");
        
        // Test empty/null strings
        assertFalse(ValidationUtils.isNotEmpty(null), "Null should be invalid");
        assertFalse(ValidationUtils.isNotEmpty(""), "Empty string should be invalid");
        assertFalse(ValidationUtils.isNotEmpty("   "), "Whitespace-only string should be invalid");
        
        // Test string length validation
        assertTrue(ValidationUtils.isValidLength("Test", 1, 10), 
            "String within length bounds should be valid");
        assertFalse(ValidationUtils.isValidLength("Too long string for this test", 1, 10), 
            "String exceeding max length should be invalid");
        assertFalse(ValidationUtils.isValidLength("", 1, 10), 
            "String below min length should be invalid");
        
        System.out.println("   ✓ String validation working correctly");
    }

    // Numeric Validation Tests
    @Test
    @Order(8)
    @DisplayName("Test Numeric Validation")
    void testNumericValidation() {
        System.out.println("\n8. Testing numeric validation...");
        
        // Valid numbers
        assertTrue(ValidationUtils.isValidDecimal("123.45"), "Valid decimal should pass");
        assertTrue(ValidationUtils.isValidDecimal("0"), "Zero should be valid");
        assertTrue(ValidationUtils.isValidDecimal("999999.99"), "Large decimal should be valid");
        
        // Invalid numbers
        assertFalse(ValidationUtils.isValidDecimal("abc"), "Non-numeric string should fail");
        assertFalse(ValidationUtils.isValidDecimal(""), "Empty string should fail");
        assertFalse(ValidationUtils.isValidDecimal(null), "Null should fail");
        assertFalse(ValidationUtils.isValidDecimal("12.34.56"), "Multiple decimals should fail");
        
        // Range validation
        assertTrue(ValidationUtils.isInRange(50, 1, 100), "Number in range should be valid");
        assertFalse(ValidationUtils.isInRange(150, 1, 100), "Number above range should be invalid");
        assertFalse(ValidationUtils.isInRange(-50, 1, 100), "Number below range should be invalid");
        
        System.out.println("   ✓ Numeric validation working correctly");
    }

    // Security Validation Tests
    @Test
    @Order(9)
    @DisplayName("Test Security Validation")
    void testSecurityValidation() {
        System.out.println("\n9. Testing security validation...");
        // Test SQL injection prevention
        String[] sqlInjectionAttempts = {
            "'; DROP TABLE customers; --",
            "' OR '1'='1",
            "'; DELETE FROM users; --",
            "admin'--",
            "' UNION SELECT * FROM passwords --"
        };
        for (String injection : sqlInjectionAttempts) {
            assertFalse(ValidationUtils.isSafeInput(injection), 
                "SQL injection attempt should be blocked: " + injection);
        }
        // Test XSS prevention
        String[] xssAttempts = {
            "<script>alert('XSS')</script>",
            "javascript:alert('XSS')",
            "<img src=x onerror=alert('XSS')>",
            "onload=alert('XSS')"
        };
        for (String xss : xssAttempts) {
            assertFalse(ValidationUtils.isSafeInput(xss), 
                "XSS attempt should be blocked: " + xss);
        }
        // Test safe inputs
        String[] safeInputs = {
            "Normal text input",
            "User Name 123",
            "Valid input with numbers 456",
            "Hyphen-separated-text"
        };
        for (String safe : safeInputs) {
            assertTrue(ValidationUtils.isSafeInput(safe), 
                "Safe input should be allowed: " + safe);
        }
        System.out.println("   ✓ Security validation working correctly");
    }

    // Password Validation Tests
    @Test
    @Order(10)
    @DisplayName("Test Password Validation")
    void testPasswordValidation() {
        System.out.println("\n10. Testing password validation...");
        
        // Strong passwords
        String[] strongPasswords = {
            "Password123!",
            "MySecure@Pass1",
            "Complex#Pass9",
            "Strong$Password2024"
        };
        
        for (String password : strongPasswords) {
            assertTrue(ValidationUtils.isStrongPassword(password), 
                "Strong password should pass validation: " + password);
        }
        
        // Weak passwords
        String[] weakPasswords = {
            "password", // No uppercase, numbers, or special chars
            "PASSWORD", // No lowercase, numbers, or special chars
            "Password", // No numbers or special chars
            "Pass1", // Too short
            "12345678", // Only numbers
            "", // Empty
            null // Null
        };
        
        for (String password : weakPasswords) {
            assertFalse(ValidationUtils.isStrongPassword(password), 
                "Weak password should fail validation: " + password);
        }
        
        System.out.println("   ✓ Password validation working correctly");
    }

    // Date Validation Tests
    @Test
    @Order(11)
    @DisplayName("Test Date Validation")
    void testDateValidation() {
        System.out.println("\n11. Testing date validation...");
        
        // Valid dates
        String[] validDates = {
            "2024-01-15",
            "2023-12-31",
            "2024-02-29", // Leap year
            "2024-06-15"
        };
        
        for (String date : validDates) {
            assertTrue(ValidationUtils.isValidDate(date), 
                "Valid date should pass: " + date);
        }
        
        // Invalid dates
        String[] invalidDates = {
            "2023-02-29", // Not a leap year
            "2024-13-01", // Invalid month
            "2024-01-32", // Invalid day
            "invalid-date",
            "2024/01/15", // Wrong format
            "",
            null
        };
        
        for (String date : invalidDates) {
            assertFalse(ValidationUtils.isValidDate(date), 
                "Invalid date should fail: " + date);
        }
        
        System.out.println("   ✓ Date validation working correctly");
    }

    // Edge Case Testing
    @Test
    @Order(12)
    @DisplayName("Test Edge Cases and Boundary Values")
    void testEdgeCases() {
        System.out.println("\n12. Testing edge cases and boundary values...");
        
        // Boundary length testing
        String exactLength = "a".repeat(50);
        assertTrue(ValidationUtils.isValidLength(exactLength, 50, 50), 
            "Exact boundary length should be valid");
        
        String oneTooLong = "a".repeat(51);
        assertFalse(ValidationUtils.isValidLength(oneTooLong, 50, 50), 
            "One character over boundary should be invalid");
        
        // Unicode and special character testing
        assertTrue(ValidationUtils.isValidName("José"), "Unicode names should be valid");
        assertTrue(ValidationUtils.isValidName("O'Connor"), "Apostrophes in names should be valid");
        assertTrue(ValidationUtils.isValidName("Mary-Jane"), "Hyphens in names should be valid");
        
        // Extreme values
        assertFalse(ValidationUtils.isInRange(Integer.MAX_VALUE, 1, 1000), 
            "Maximum integer should be outside normal range");
        assertFalse(ValidationUtils.isInRange(Integer.MIN_VALUE, 1, 1000), 
            "Minimum integer should be outside normal range");
        
        System.out.println("   ✓ Edge case testing completed");
    }
}