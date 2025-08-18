package com.pahanaedu.util;

import com.pahanaedu.dao.UserDAO;
import com.pahanaedu.models.User;
import com.pahanaedu.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for migrating existing passwords to hashed passwords
 * This is a one-time migration utility
 */
public class PasswordMigrationUtil {

    private static final DatabaseConnection dbConnection = DatabaseConnection.getInstance();

    /**
     * Main migration method - call this once to update all existing passwords
     */
    public static void migrateAllPasswords() {
        System.out.println("=== Starting Password Migration ===");
        
        try {
            // Get all users
            List<User> users = getAllUsers();
            System.out.println("Found " + users.size() + " users to migrate");
            
            int successCount = 0;
            int errorCount = 0;
            
            for (User user : users) {
                try {
                    boolean migrated = migrateUserPassword(user);
                    if (migrated) {
                        successCount++;
                        System.out.println("✓ Migrated password for user: " + user.getUsername());
                    } else {
                        errorCount++;
                        System.out.println("✗ Failed to migrate password for user: " + user.getUsername());
                    }
                } catch (Exception e) {
                    errorCount++;
                    System.err.println("✗ Error migrating password for user " + user.getUsername() + ": " + e.getMessage());
                }
            }
            
            System.out.println("=== Migration Complete ===");
            System.out.println("Successfully migrated: " + successCount + " users");
            System.out.println("Failed to migrate: " + errorCount + " users");
            
        } catch (Exception e) {
            System.err.println("Migration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get all users from database
     */
    private static List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, password, role, full_name, email, phone_number, is_active, created_date, last_login FROM users";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                User user = createUserFromResultSet(rs);
                users.add(user);
            }
        }
        
        return users;
    }

    /**
     * Migrate password for a single user
     */
    private static boolean migrateUserPassword(User user) {
        try {
            // Determine new password based on role
            String newPassword;
            if (user.getRole() == User.Role.ADMIN) {
                newPassword = "Admin123";
            } else {
                newPassword = "Staff123";
            }
            
            // Hash the new password
            String hashedPassword = PasswordUtil.hashPassword(newPassword);
            
            // Update the password in database
            String sql = "UPDATE users SET password = ? WHERE user_id = ?";
            
            try (Connection conn = dbConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, hashedPassword);
                stmt.setString(2, user.getUserId());
                
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
            
        } catch (Exception e) {
            System.err.println("Error migrating password for user " + user.getUsername() + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Create User object from ResultSet
     */
    private static User createUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User() {
            @Override
            public String getDisplayRole() {
                return getRole() != null ? getRole().toString() : "Unknown";
            }

            @Override
            public boolean hasPermission(String permission) {
                if (getRole() == null) return false;
                
                switch (permission) {
                    case "USER_MANAGEMENT":
                        return getRole() == User.Role.ADMIN;
                    case "CUSTOMER_MANAGEMENT":
                    case "ITEM_MANAGEMENT":
                    case "BILL_MANAGEMENT":
                        return getRole() == User.Role.ADMIN || getRole() == User.Role.STAFF;
                    default:
                        return false;
                }
            }
        };
        
        user.setUserId(rs.getString("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setActive(rs.getBoolean("is_active"));
        user.setCreatedDate(rs.getTimestamp("created_date"));
        user.setLastLogin(rs.getTimestamp("last_login"));
        
        // Set role
        String roleStr = rs.getString("role");
        if ("ADMIN".equals(roleStr)) {
            user.setRole(User.Role.ADMIN);
        } else {
            user.setRole(User.Role.STAFF);
        }
        
        return user;
    }

    /**
     * Test method to verify migration worked
     */
    public static void testPasswordVerification() {
        System.out.println("=== Testing Password Verification ===");
        
        try {
            List<User> users = getAllUsers();
            
            for (User user : users) {
                String testPassword = (user.getRole() == User.Role.ADMIN) ? "Admin123" : "Staff123";
                boolean isValid = PasswordUtil.verifyPassword(testPassword, user.getPassword());
                
                System.out.println("User: " + user.getUsername() + 
                                 " | Role: " + user.getRole() + 
                                 " | Password Valid: " + (isValid ? "✓" : "✗"));
            }
            
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}