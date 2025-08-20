package com.pahanaedu.dao;

import com.pahanaedu.models.User;
import com.pahanaedu.models.Admin;
import com.pahanaedu.models.Staff;
import com.pahanaedu.util.DatabaseConnection;
import com.pahanaedu.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User management
 * Handles all database operations related to users (Admin and Staff)
 *
 * Design Patterns Used:
 * - DAO Pattern: Encapsulates database access logic
 * - Factory Pattern: Creates appropriate User subclass instances
 *
 * @author Pahana Edu Development Team
 * @version 1.0
 */
public class UserDAO {

    private final DatabaseConnection dbConnection;

    /**
     * Constructor - initialize with database connection
     */
    public UserDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Authenticate user with username and password
     *
     * @param username User's username
     * @param password User's plain text password
     * @return User object if authentication successful, null otherwise
     */
    public User authenticateUser(String username, String password) {
        String sql = """
            SELECT user_id, username, password, role, full_name, email, phone_number, 
                   is_active, created_date, last_login 
            FROM users 
            WHERE username = ? AND is_active = TRUE
            """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHashedPassword = rs.getString("password");

                    // Verify password using PasswordUtil
                    if (PasswordUtil.verifyPassword(password, storedHashedPassword)) {
                        // Update last login time
                        updateLastLogin(rs.getString("user_id"));

                        // Return appropriate User object based on role
                        return createUserFromResultSet(rs);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Find user by user ID
     *
     * @param userId User ID to search for
     * @return User object if found, null otherwise
     */
    public User findByUserId(String userId) {
        String sql = """
            SELECT user_id, username, password, role, full_name, email, phone_number, 
                   is_active, created_date, last_login 
            FROM users 
            WHERE user_id = ?
            """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createUserFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding user by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Find user by username
     *
     * @param username Username to search for
     * @return User object if found, null otherwise
     */
    public User findByUsername(String username) {
        String sql = """
            SELECT user_id, username, password, role, full_name, email, phone_number, 
                   is_active, created_date, last_login 
            FROM users 
            WHERE username = ?
            """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createUserFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding user by username: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Create a new user
     *
     * @param user User object to create
     * @return true if creation successful, false otherwise
     */
    public boolean createUser(User user) {
        String sql = """
            INSERT INTO users (user_id, username, password, role, full_name, email, phone_number, is_active) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUserId());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, PasswordUtil.hashPassword(user.getPassword())); // Hash password
            stmt.setString(4, user.getRole().toString());
            stmt.setString(5, user.getFullName());
            stmt.setString(6, user.getEmail());
            stmt.setString(7, user.getPhoneNumber());
            stmt.setBoolean(8, user.isActive());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update an existing user
     *
     * @param user User object with updated information
     * @return true if update successful, false otherwise
     */
    public boolean updateUser(User user) {
        String sql = """
            UPDATE users 
            SET username = ?, full_name = ?, email = ?, phone_number = ?, is_active = ?
            WHERE user_id = ?
            """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getFullName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPhoneNumber());
            stmt.setBoolean(5, user.isActive());
            stmt.setString(6, user.getUserId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update user password
     *
     * @param userId User ID
     * @param newPassword New plain text password
     * @return true if update successful, false otherwise
     */
    public boolean updatePassword(String userId, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, PasswordUtil.hashPassword(newPassword)); // Hash new password
            stmt.setString(2, userId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deactivate user (soft delete)
     *
     * @param userId User ID to deactivate
     * @return true if deactivation successful, false otherwise
     */
    public boolean deactivateUser(String userId) {
        String sql = "UPDATE users SET is_active = FALSE WHERE user_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deactivating user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all active users
     *
     * @return List of all active users
     */
    public List<User> getAllActiveUsers() {
        List<User> users = new ArrayList<>();
        String sql = """
            SELECT user_id, username, password, role, full_name, email, phone_number, 
                   is_active, created_date, last_login 
            FROM users 
            WHERE is_active = TRUE 
            ORDER BY full_name
            """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(createUserFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting all active users: " + e.getMessage());
            e.printStackTrace();
        }

        return users;
    }

    /**
     * Get users by role
     *
     * @param role User role (ADMIN or STAFF)
     * @return List of users with specified role
     */
    public List<User> getUsersByRole(User.Role role) {
        List<User> users = new ArrayList<>();
        String sql = """
            SELECT user_id, username, password, role, full_name, email, phone_number, 
                   is_active, created_date, last_login 
            FROM users 
            WHERE role = ? AND is_active = TRUE 
            ORDER BY full_name
            """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, role.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(createUserFromResultSet(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting users by role: " + e.getMessage());
            e.printStackTrace();
        }

        return users;
    }

    /**
     * Check if username already exists
     *
     * @param username Username to check
     * @param excludeUserId User ID to exclude from check (for updates)
     * @return true if username exists, false otherwise
     */
    public boolean isUsernameExists(String username, String excludeUserId) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ? AND user_id != ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, excludeUserId != null ? excludeUserId : "");

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error checking username existence: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Update last login timestamp for user
     *
     * @param userId User ID
     */
    private void updateLastLogin(String userId) {
        String sql = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE user_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating last login: " + e.getMessage());
        }
    }

    /**
     * Create User object from ResultSet
     * Factory method to create appropriate User subclass based on role
     *
     * @param rs ResultSet containing user data
     * @return User object (Admin or Staff)
     * @throws SQLException if database error occurs
     */
    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        String role = rs.getString("role");

        User user;
        if ("ADMIN".equals(role)) {
            user = new Admin();
        } else {
            user = new Staff();
        }

        // Set common properties
        user.setUserId(rs.getString("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password")); // This is the hashed password
        user.setRole(User.Role.valueOf(role));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setActive(rs.getBoolean("is_active"));
        user.setCreatedDate(rs.getTimestamp("created_date"));
        user.setLastLogin(rs.getTimestamp("last_login"));

        return user;
    }

    public String generateNextUserId() {
        String sql = "SELECT MAX(CAST(SUBSTRING(user_id, 4) AS UNSIGNED)) as max_id FROM users WHERE user_id LIKE 'USR%'";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int maxId = rs.getInt("max_id");
                return String.format("USR%03d", maxId + 1);
            }

        } catch (SQLException e) {
            System.err.println("Error generating user ID: " + e.getMessage());
        }

        return "USR001"; // Default if no users exist
    }
}