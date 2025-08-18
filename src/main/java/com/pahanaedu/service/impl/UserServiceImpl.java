package com.pahanaedu.service.impl;

import com.pahanaedu.dao.UserDAO;
import com.pahanaedu.models.User;
import com.pahanaedu.service.interfaces.UserService;
import com.pahanaedu.util.PasswordUtil;
import com.pahanaedu.util.ValidationUtils;

import java.util.List;

/**
 * Service implementation for User management operations
 * Implements business logic for user operations
 *
 * Design Patterns Used:
 * - Service Layer Pattern: Encapsulates business logic
 * - Dependency Injection: Uses DAO for data access
 * - Strategy Pattern: Different validation strategies
 *
 * @author Pahana Edu Development Team
 * @version 1.0
 */
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;

    /**
     * Constructor with dependency injection
     */
    public UserServiceImpl() {
        this.userDAO = new UserDAO();
    }

    /**
     * Constructor for testing with DAO injection
     */
    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public User authenticateUser(String username, String password) {
        // Validate input parameters
        if (!ValidationUtils.isNotEmpty(username) || !ValidationUtils.isNotEmpty(password)) {
            System.err.println("Authentication failed: Username and password are required");
            return null;
        }

        try {
            // Get user from database
            User user = userDAO.findByUsername(username);
            if (user == null) {
                System.err.println("User not found: " + username);
                return null;
            }

            // Use proper password verification
            if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
                System.err.println("Password verification failed for user: " + username);
                return null;
            }

            // Check if user is active
            if (!user.isActive()) {
                System.err.println("User account is deactivated: " + username);
                return null;
            }

            System.out.println("Authentication successful for user: " + username);
            return user;

        } catch (Exception e) {
            System.err.println("Authentication error: " + e.getMessage());
            return null;
        }
    }


    @Override
    public boolean createUser(User user) {
        // Validate user data
        if (!validateUser(user)) {
            return false;
        }

        // Check if username already exists
        if (!isUsernameAvailable(user.getUsername(), null)) {
            System.err.println("Username already exists: " + user.getUsername());
            return false;
        }

        // Generate user ID if not provided
        if (user.getUserId() == null || user.getUserId().trim().isEmpty()) {
            user.setUserId(generateNextUserId());
        }

        // Validate password strength
        if (!PasswordUtil.isPasswordStrong(user.getPassword())) {
            System.err.println("Password does not meet strength requirements");
            return false;
        }

        // Hash the password before saving
        String hashedPassword = PasswordUtil.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);

        try {
            return userDAO.createUser(user);
        } catch (Exception e) {
            System.err.println("Error creating user: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateUser(User user) {
        // Validate user data (excluding password for updates)
        if (!validateUserForUpdate(user)) {
            return false;
        }

        // Check if username is available (excluding current user)
        if (!isUsernameAvailable(user.getUsername(), user.getUserId())) {
            System.err.println("Username already exists: " + user.getUsername());
            return false;
        }

        try {
            return userDAO.updateUser(user);
        } catch (Exception e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean changePassword(String userId, String currentPassword, String newPassword) {
        // Validate input parameters
        if (!ValidationUtils.isNotEmpty(userId) ||
                !ValidationUtils.isNotEmpty(currentPassword) ||
                !ValidationUtils.isNotEmpty(newPassword)) {
            System.err.println("All password fields are required");
            return false;
        }

        // Find the user
        User user = findUserById(userId);
        if (user == null) {
            System.err.println("User not found: " + userId);
            return false;
        }

        // Verify current password (using correct variable names and return type)
        if (!PasswordUtil.verifyPassword(currentPassword, user.getPassword())) {
            System.err.println("Current password is incorrect for user: " + userId);
            return false;  // Changed from 'return null' to 'return false'
        }

        // Validate new password strength
        if (!PasswordUtil.isPasswordStrong(newPassword)) {
            System.err.println("New password does not meet strength requirements");
            return false;
        }

        // Check if new password is different from current
        if (PasswordUtil.verifyPassword(newPassword, user.getPassword())) {  // FIXED: Use simple comparison for now
            System.err.println("New password must be different from current password");
            return false;
        }

        try {
            return userDAO.updatePassword(userId, PasswordUtil.hashPassword(newPassword));
        } catch (Exception e) {
            System.err.println("Error changing password: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deactivateUser(String userId) {
        if (!ValidationUtils.isNotEmpty(userId)) {
            System.err.println("User ID is required");
            return false;
        }

        try {
            return userDAO.deactivateUser(userId);
        } catch (Exception e) {
            System.err.println("Error deactivating user: " + e.getMessage());
            return false;
        }
    }

    @Override
    public User findUserById(String userId) {
        if (!ValidationUtils.isNotEmpty(userId)) {
            return null;
        }

        try {
            return userDAO.findByUserId(userId);
        } catch (Exception e) {
            System.err.println("Error finding user by ID: " + e.getMessage());
            return null;
        }
    }

    @Override
    public User findUserByUsername(String username) {
        if (!ValidationUtils.isNotEmpty(username)) {
            return null;
        }

        try {
            return userDAO.findByUsername(username);
        } catch (Exception e) {
            System.err.println("Error finding user by username: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<User> getAllActiveUsers() {
        try {
            return userDAO.getAllActiveUsers();
        } catch (Exception e) {
            System.err.println("Error getting all active users: " + e.getMessage());
            return List.of(); // Return empty list on error
        }
    }

    @Override
    public List<User> getUsersByRole(User.Role role) {
        if (role == null) {
            System.err.println("Role is required");
            return List.of();
        }

        try {
            return userDAO.getUsersByRole(role);
        } catch (Exception e) {
            System.err.println("Error getting users by role: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public boolean validateUser(User user) {
        if (user == null) {
            System.err.println("User object is required");
            return false;
        }

        // Validate username
        if (!ValidationUtils.isNotEmpty(user.getUsername())) {
            System.err.println("Username is required");
            return false;
        }

        if (!ValidationUtils.isValidUsername(user.getUsername())) {
            System.err.println("Username format is invalid");
            return false;
        }

        // Validate password (for new users)
        if (!ValidationUtils.isNotEmpty(user.getPassword())) {
            System.err.println("Password is required");
            return false;
        }

        // Validate full name
        if (!ValidationUtils.isNotEmpty(user.getFullName())) {
            System.err.println("Full name is required");
            return false;
        }

        // Validate email if provided
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            if (!ValidationUtils.isValidEmail(user.getEmail())) {
                System.err.println("Email format is invalid");
                return false;
            }
        }

        // Validate phone number if provided
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().trim().isEmpty()) {
            if (!ValidationUtils.isValidPhoneNumber(user.getPhoneNumber())) {
                System.err.println("Phone number format is invalid");
                return false;
            }
        }

        // Validate role
        if (user.getRole() == null) {
            System.err.println("User role is required");
            return false;
        }

        return true;
    }

    /**
     * Validate user data for updates (password not required)
     */
    private boolean validateUserForUpdate(User user) {
        if (user == null) {
            System.err.println("User object is required");
            return false;
        }

        // Validate user ID
        if (!ValidationUtils.isNotEmpty(user.getUserId())) {
            System.err.println("User ID is required for updates");
            return false;
        }

        // Validate username
        if (!ValidationUtils.isNotEmpty(user.getUsername())) {
            System.err.println("Username is required");
            return false;
        }

        if (!ValidationUtils.isValidUsername(user.getUsername())) {
            System.err.println("Username format is invalid");
            return false;
        }

        // Validate full name
        if (!ValidationUtils.isNotEmpty(user.getFullName())) {
            System.err.println("Full name is required");
            return false;
        }

        // Validate email if provided
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            if (!ValidationUtils.isValidEmail(user.getEmail())) {
                System.err.println("Email format is invalid");
                return false;
            }
        }

        // Validate phone number if provided
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().trim().isEmpty()) {
            if (!ValidationUtils.isValidPhoneNumber(user.getPhoneNumber())) {
                System.err.println("Phone number format is invalid");
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isUsernameAvailable(String username, String excludeUserId) {
        if (!ValidationUtils.isNotEmpty(username)) {
            return false;
        }

        try {
            return !userDAO.isUsernameExists(username, excludeUserId);
        } catch (Exception e) {
            System.err.println("Error checking username availability: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String generateNextUserId() {
        try {
            return userDAO.generateNextUserId();
        } catch (Exception e) {
            System.err.println("Error generating user ID: " + e.getMessage());
            return "USR001"; // Fallback default
        }
    }
}