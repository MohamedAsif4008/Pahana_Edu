package com.pahanaedu.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple password migration utility that works from command line
 * This directly connects to database without using DatabaseConnection class
 */
public class SimplePasswordMigration {
    
    // Database configuration - update these values
    private static final String DB_URL = "jdbc:mysql://localhost:3306/pahana_edu?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "12345678";
    
    public static void main(String[] args) {
        System.out.println("Starting Simple Password Migration...");
        
        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Run migration
            migrateAllPasswords();
            
            System.out.println("\nMigration completed successfully!");
            System.out.println("Default passwords:");
            System.out.println("- Admin users: Admin123");
            System.out.println("- Staff users: Staff123");
            
        } catch (Exception e) {
            System.err.println("Migration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void migrateAllPasswords() throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            System.out.println("Connected to database successfully");
            
            // Get all users
            List<UserInfo> users = getAllUsers(connection);
            System.out.println("Found " + users.size() + " users to migrate");
            
            // Migrate each user's password
            for (UserInfo user : users) {
                String newPassword = "ADMIN".equals(user.role) ? "Admin123" : "Staff123";
                String hashedPassword = PasswordUtil.hashPassword(newPassword);
                
                updateUserPassword(connection, user.id, hashedPassword);
                System.out.println("Migrated user: " + user.username + " (" + user.role + ")");
            }
        }
    }
    
    private static List<UserInfo> getAllUsers(Connection connection) throws SQLException {
        List<UserInfo> users = new ArrayList<>();
        
        String sql = "SELECT id, username, role FROM users WHERE is_active = 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                UserInfo user = new UserInfo();
                user.id = rs.getInt("id");
                user.username = rs.getString("username");
                user.role = rs.getString("role");
                users.add(user);
            }
        }
        
        return users;
    }
    
    private static void updateUserPassword(Connection connection, int userId, String hashedPassword) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, hashedPassword);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }
    
    private static class UserInfo {
        int id;
        String username;
        String role;
    }
}
