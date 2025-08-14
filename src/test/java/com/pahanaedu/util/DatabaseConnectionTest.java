package com.pahanaedu.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for DatabaseConnection utility
 * Demonstrates proper testing practices and validation for Pahana Edu system
 *
 * @author Pahana Edu Development Team
 * @version 1.0
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatabaseConnectionTest {

    @BeforeAll
    static void setUp() {
        System.out.println("=".repeat(60));
        System.out.println("Starting Pahana Edu Database Connection Tests...");
        System.out.println("=".repeat(60));
    }

    @Test
    @Order(1)
    @DisplayName("Test Singleton Pattern Implementation")
    public void testSingletonPattern() {
        System.out.println("\n1. Testing Singleton Pattern...");

        // Test that getInstance returns the same instance every time
        DatabaseConnection instance1 = DatabaseConnection.getInstance();
        DatabaseConnection instance2 = DatabaseConnection.getInstance();

        assertNotNull(instance1, "First instance should not be null");
        assertNotNull(instance2, "Second instance should not be null");
        assertSame(instance1, instance2, "Both instances should be the same object (Singleton pattern)");

        System.out.println("   ✓ Singleton pattern implemented correctly");
        System.out.println("   ✓ Both instances reference the same object");
    }

    @Test
    @Order(2)
    @DisplayName("Test Database Connection Establishment")
    public void testDatabaseConnection() {
        System.out.println("\n2. Testing Database Connection...");

        DatabaseConnection dbConnection = DatabaseConnection.getInstance();

        // Test connection availability
        assertTrue(dbConnection.testConnection(),
                "Database connection should be successful. Check your MySQL server and database configuration.");

        System.out.println("   ✓ Database connection established successfully");
        System.out.println("   ✓ MySQL server is accessible");
    }

    @Test
    @Order(3)
    @DisplayName("Test Database Structure and Operations")
    public void testDatabaseOperations() {
        System.out.println("\n3. Testing Database Operations...");

        DatabaseConnection dbConnection = DatabaseConnection.getInstance();
        Connection connection = null;

        try {
            connection = dbConnection.getConnection();
            assertNotNull(connection, "Connection should not be null");
            assertFalse(connection.isClosed(), "Connection should be open");
            assertTrue(connection.isValid(5), "Connection should be valid within 5 seconds");

            // Test basic connectivity
            testBasicQuery(connection);

            // Test Pahana Edu specific database structure
            testPahanaEduTables(connection);

            // Test sample data
            testSampleData(connection);

        } catch (SQLException e) {
            fail("Database operation failed: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection(connection);
        }

        System.out.println("   ✓ All database operations completed successfully");
    }

    /**
     * Test basic database connectivity
     */
    private void testBasicQuery(Connection connection) throws SQLException {
        String sql = "SELECT 1 as test_value, NOW() as current_ts";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            assertTrue(rs.next(), "Basic query should return results");
            assertEquals(1, rs.getInt("test_value"), "Test query should return 1");
            assertNotNull(rs.getTimestamp("current_ts"), "Should return current timestamp");

            System.out.println("   ✓ Basic database query executed successfully");
        }
    }

    /**
     * Test Pahana Edu specific table structure
     */
    private void testPahanaEduTables(Connection connection) throws SQLException {
        String[] expectedTables = {"users", "customers", "items", "bills", "bill_items"};

        for (String tableName : expectedTables) {
            String sql = "SELECT COUNT(*) as record_count FROM " + tableName;
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                assertTrue(rs.next(), "Table " + tableName + " should exist and be queryable");
                int count = rs.getInt("record_count");
                assertTrue(count >= 0, "Record count should be non-negative");

                System.out.println("   ✓ Table '" + tableName + "' exists with " + count + " records");
            }
        }
    }

    /**
     * Test sample data in key tables
     */
    private void testSampleData(Connection connection) throws SQLException {
        // Test users table
        String userSql = "SELECT COUNT(*) as user_count FROM users WHERE is_active = TRUE";
        try (PreparedStatement stmt = connection.prepareStatement(userSql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int userCount = rs.getInt("user_count");
                System.out.println("   ✓ Found " + userCount + " active users");
            }
        }

        // Test customers table
        String customerSql = "SELECT COUNT(*) as customer_count FROM customers WHERE is_active = TRUE";
        try (PreparedStatement stmt = connection.prepareStatement(customerSql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int customerCount = rs.getInt("customer_count");
                System.out.println("   ✓ Found " + customerCount + " active customers");
            }
        }

        // Test items table
        String itemSql = "SELECT COUNT(*) as item_count, SUM(stock_quantity) as total_stock FROM items WHERE is_active = TRUE";
        try (PreparedStatement stmt = connection.prepareStatement(itemSql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int itemCount = rs.getInt("item_count");
                int totalStock = rs.getInt("total_stock");
                System.out.println("   ✓ Found " + itemCount + " active items with total stock of " + totalStock);
            }
        }
    }

    @Test
    @Order(4)
    @DisplayName("Test Configuration Information")
    public void testConfigInfo() {
        System.out.println("\n4. Testing Configuration Information...");

        DatabaseConnection dbConnection = DatabaseConnection.getInstance();
        String configInfo = dbConnection.getConfigInfo();

        assertNotNull(configInfo, "Config info should not be null");
        assertFalse(configInfo.trim().isEmpty(), "Config info should not be empty");

        // Should not contain sensitive information
        assertFalse(configInfo.toLowerCase().contains("password"),
                "Config info should not expose password");
        assertFalse(configInfo.contains("12345678"),
                "Config info should not expose actual password value");

        System.out.println("   ✓ Configuration: " + configInfo);
        System.out.println("   ✓ Sensitive information properly masked");
    }

    @Test
    @Order(5)
    @DisplayName("Test Multiple Concurrent Connections")
    public void testMultipleConnections() {
        System.out.println("\n5. Testing Multiple Connections...");

        DatabaseConnection dbConnection = DatabaseConnection.getInstance();

        try {
            // Test that we can get multiple connections simultaneously
            Connection conn1 = dbConnection.getConnection();
            Connection conn2 = dbConnection.getConnection();
            Connection conn3 = dbConnection.getConnection();

            assertNotNull(conn1, "First connection should not be null");
            assertNotNull(conn2, "Second connection should not be null");
            assertNotNull(conn3, "Third connection should not be null");

            // Each connection should be different objects
            assertNotSame(conn1, conn2, "Each getConnection() call should return a new connection");
            assertNotSame(conn2, conn3, "Each getConnection() call should return a new connection");
            assertNotSame(conn1, conn3, "Each getConnection() call should return a new connection");

            // All connections should be valid
            assertTrue(conn1.isValid(5), "First connection should be valid");
            assertTrue(conn2.isValid(5), "Second connection should be valid");
            assertTrue(conn3.isValid(5), "Third connection should be valid");

            // Test concurrent queries
            testConcurrentQueries(conn1, conn2, conn3);

            // Clean up all connections
            DatabaseConnection.closeConnection(conn1);
            DatabaseConnection.closeConnection(conn2);
            DatabaseConnection.closeConnection(conn3);

            System.out.println("   ✓ Multiple connections handled correctly");
            System.out.println("   ✓ Connection cleanup completed");

        } catch (SQLException e) {
            fail("Multiple connections test failed: " + e.getMessage());
        }
    }

    /**
     * Test concurrent queries on different connections
     */
    private void testConcurrentQueries(Connection conn1, Connection conn2, Connection conn3) throws SQLException {
        // Query 1: Get user count
        String query1 = "SELECT COUNT(*) as count FROM users";
        try (PreparedStatement stmt = conn1.prepareStatement(query1);
             ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());
        }

        // Query 2: Get customer count
        String query2 = "SELECT COUNT(*) as count FROM customers";
        try (PreparedStatement stmt = conn2.prepareStatement(query2);
             ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());
        }

        // Query 3: Get item count
        String query3 = "SELECT COUNT(*) as count FROM items";
        try (PreparedStatement stmt = conn3.prepareStatement(query3);
             ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());
        }

        System.out.println("   ✓ Concurrent queries executed successfully");
    }

    @Test
    @Order(6)
    @DisplayName("Test Connection Resource Management")
    public void testResourceManagement() {
        System.out.println("\n6. Testing Resource Management...");

        DatabaseConnection dbConnection = DatabaseConnection.getInstance();

        // Test null connection handling
        DatabaseConnection.closeConnection(null);
        System.out.println("   ✓ Null connection handling works correctly");

        // Test closing already closed connection
        try {
            Connection conn = dbConnection.getConnection();
            conn.close();
            DatabaseConnection.closeConnection(conn); // Should not throw exception
            System.out.println("   ✓ Closed connection handling works correctly");
        } catch (SQLException e) {
            fail("Resource management test failed: " + e.getMessage());
        }

        System.out.println("   ✓ Resource management tests completed");
        System.out.println("\n" + "=".repeat(60));
        System.out.println("All Database Connection Tests Completed Successfully!");
        System.out.println("=".repeat(60));
    }
}