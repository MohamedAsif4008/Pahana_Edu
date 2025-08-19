package com.pahanaedu.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Application initializer that runs when the web application starts
 * Automatically migrates passwords for admin and staff users
 * 
 * @author Pahana Edu Development Team
 * @version 1.0
 */
@WebListener
public class ApplicationInitializer implements ServletContextListener {
    
    // Database configuration - update these values
    private static final String DB_URL = "jdbc:mysql://localhost:3306/pahana_edu?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "12345678";
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("=== Application Initializer Starting ===");
        
        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Run password migration
            migrateDefaultPasswords();
            
            System.out.println("=== Application Initializer Completed Successfully ===");
            
        } catch (Exception e) {
            System.err.println("=== Application Initializer Failed: " + e.getMessage() + " ===");
            e.printStackTrace();
        }
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("=== Application Initializer Destroyed ===");
    }
    
    /**
     * Migrate default passwords for admin and staff users
     */
    private void migrateDefaultPasswords() throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            System.out.println("Connected to database successfully");
            
            // Get all users
            List<UserInfo> users = getAllUsers(connection);
            System.out.println("Found " + users.size() + " users to migrate");
            
            // Migrate each user's password
            for (UserInfo user : users) {
                String newPassword = "ADMIN".equals(user.role) ? "Admin123" : "Staff123";
                String hashedPassword = PasswordUtil.hashPassword(newPassword);
                
                updateUserPassword(connection, user.userId, hashedPassword);
                System.out.println("Migrated user: " + user.username + " (" + user.role + ")");
            }
            
            System.out.println("Default passwords set:");
            System.out.println("- Admin users: Admin123");
            System.out.println("- Staff users: Staff123");
        }
    }
    
    /**
     * Get all active users from the database
     */
    private List<UserInfo> getAllUsers(Connection connection) throws SQLException {
        List<UserInfo> users = new ArrayList<>();
        
        String sql = "SELECT user_id, username, role FROM users WHERE is_active = 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                UserInfo user = new UserInfo();
                user.userId = rs.getString("user_id");
                user.username = rs.getString("username");
                user.role = rs.getString("role");
                users.add(user);
            }
        }
        
        return users;
    }
    
    /**
     * Update user password in the database
     */
    private void updateUserPassword(Connection connection, String userId, String hashedPassword) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, hashedPassword);
            stmt.setString(2, userId);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Inner class to hold user information
     */
    private static class UserInfo {
        String userId;
        String username;
        String role;
    }
}