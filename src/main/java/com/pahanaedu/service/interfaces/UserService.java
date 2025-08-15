package com.pahanaedu.service.interfaces;

import com.pahanaedu.models.User;
import java.util.List;

/**
 * Service interface for User management operations
 * Defines business logic operations for user management
 *
 * Design Pattern: Service Layer Pattern
 * - Separates business logic from data access
 * - Provides transaction boundaries
 * - Encapsulates business rules
 *
 * @author Pahana Edu Development Team
 * @version 1.0
 */
public interface UserService {

    /**
     * Authenticate user with username and password
     *
     * @param username User's username
     * @param password User's plain text password
     * @return User object if authentication successful, null otherwise
     */
    User authenticateUser(String username, String password);

    /**
     * Create a new user with validation
     *
     * @param user User object to create
     * @return true if creation successful, false otherwise
     */
    boolean createUser(User user);

    /**
     * Update an existing user with validation
     *
     * @param user User object with updated information
     * @return true if update successful, false otherwise
     */
    boolean updateUser(User user);

    /**
     * Change user password with validation
     *
     * @param userId User ID
     * @param currentPassword Current password for verification
     * @param newPassword New password
     * @return true if password change successful, false otherwise
     */
    boolean changePassword(String userId, String currentPassword, String newPassword);

    /**
     * Deactivate user account
     *
     * @param userId User ID to deactivate
     * @return true if deactivation successful, false otherwise
     */
    boolean deactivateUser(String userId);

    /**
     * Find user by user ID
     *
     * @param userId User ID to search for
     * @return User object if found, null otherwise
     */
    User findUserById(String userId);

    /**
     * Find user by username
     *
     * @param username Username to search for
     * @return User object if found, null otherwise
     */
    User findUserByUsername(String username);

    /**
     * Get all active users
     *
     * @return List of all active users
     */
    List<User> getAllActiveUsers();

    /**
     * Get users by role
     *
     * @param role User role (ADMIN or STAFF)
     * @return List of users with specified role
     */
    List<User> getUsersByRole(User.Role role);

    /**
     * Validate user data
     *
     * @param user User object to validate
     * @return true if user data is valid, false otherwise
     */
    boolean validateUser(User user);

    /**
     * Check if username is available
     *
     * @param username Username to check
     * @param excludeUserId User ID to exclude from check (for updates)
     * @return true if username is available, false otherwise
     */
    boolean isUsernameAvailable(String username, String excludeUserId);

    /**
     * Generate next user ID
     *
     * @return Next available user ID
     */
    String generateNextUserId();
}