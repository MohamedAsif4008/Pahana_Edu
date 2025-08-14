package com.pahanaedu.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton pattern implementation for database connection management
 * Demonstrates design patterns and proper resource management
 *
 * @author Pahana Edu Development Team
 * @version 1.0
 */
public class DatabaseConnection {

    // Singleton instance
    private static DatabaseConnection instance;
    private static final Object lock = new Object();

    // Database configuration
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    private String dbDriver;

    /**
     * Private constructor to prevent instantiation (Singleton pattern)
     * Loads database configuration from properties file
     */
    private DatabaseConnection() {
        loadDatabaseConfig();
        initializeDriver();
    }

    /**
     * Thread-safe singleton instance retrieval
     * Double-checked locking pattern for performance
     *
     * @return DatabaseConnection singleton instance
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    /**
     * Load database configuration from properties file
     * Demonstrates proper exception handling
     */
    private void loadDatabaseConfig() {
        Properties properties = new Properties();

        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("database.properties")) {

            if (input == null) {
                throw new RuntimeException("Unable to find database.properties file");
            }

            properties.load(input);

            // Load configuration values
            this.dbDriver = properties.getProperty("db.driver");
            this.dbUrl = properties.getProperty("db.url");
            this.dbUsername = properties.getProperty("db.username");
            this.dbPassword = properties.getProperty("db.password");

            // Validate required properties
            validateConfiguration();

        } catch (IOException e) {
            throw new RuntimeException("Error loading database configuration: " + e.getMessage(), e);
        }
    }

    /**
     * Validate that all required configuration properties are present
     */
    private void validateConfiguration() {
        if (dbDriver == null || dbDriver.trim().isEmpty()) {
            throw new RuntimeException("Database driver not specified in configuration");
        }
        if (dbUrl == null || dbUrl.trim().isEmpty()) {
            throw new RuntimeException("Database URL not specified in configuration");
        }
        if (dbUsername == null || dbUsername.trim().isEmpty()) {
            throw new RuntimeException("Database username not specified in configuration");
        }
        if (dbPassword == null) {
            throw new RuntimeException("Database password not specified in configuration");
        }
    }

    /**
     * Initialize JDBC driver
     * Demonstrates proper exception handling and logging
     */
    private void initializeDriver() {
        try {
            Class.forName(dbDriver);
            System.out.println("MySQL JDBC Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found. Make sure mysql-connector-java is in classpath", e);
        }
    }

    /**
     * Get a database connection
     * Each call returns a new connection - caller is responsible for closing
     *
     * @return Connection object to the database
     * @throws SQLException if connection cannot be established
     */
    public Connection getConnection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);

            // Set connection properties for better performance and consistency
            connection.setAutoCommit(true);

            return connection;

        } catch (SQLException e) {
            System.err.println("Failed to create database connection: " + e.getMessage());
            throw new SQLException("Database connection failed: " + e.getMessage(), e);
        }
    }

    /**
     * Test database connectivity
     * Used for health checks and initial setup validation
     *
     * @return true if connection is successful, false otherwise
     */
    public boolean testConnection() {
        try (Connection connection = getConnection()) {
            boolean isValid = connection != null &&
                    !connection.isClosed() &&
                    connection.isValid(5); // 5 second timeout

            if (isValid) {
                System.out.println("Database connection test: SUCCESS");
            } else {
                System.err.println("Database connection test: FAILED - Invalid connection");
            }

            return isValid;

        } catch (SQLException e) {
            System.err.println("Database connection test: FAILED - " + e.getMessage());
            return false;
        }
    }

    /**
     * Get database configuration info (for debugging/logging)
     * Does not expose sensitive information like passwords
     *
     * @return String containing safe configuration info
     */
    public String getConfigInfo() {
        return String.format("Database: %s, User: %s, Driver: %s",
                maskUrl(dbUrl), dbUsername, dbDriver);
    }

    /**
     * Mask sensitive parts of URL for logging
     *
     * @param url Database URL
     * @return Masked URL safe for logging
     */
    private String maskUrl(String url) {
        if (url == null) return "null";

        // Extract just the database name for logging
        int lastSlash = url.lastIndexOf('/');
        int questionMark = url.indexOf('?', lastSlash);

        if (lastSlash != -1) {
            if (questionMark != -1) {
                return url.substring(lastSlash + 1, questionMark);
            } else {
                return url.substring(lastSlash + 1);
            }
        }

        return "unknown";
    }

    /**
     * Utility method to close database resources safely
     * Demonstrates proper resource management
     *
     * @param connection Connection to close
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
}