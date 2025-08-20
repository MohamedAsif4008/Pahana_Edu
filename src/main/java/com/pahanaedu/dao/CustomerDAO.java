package com.pahanaedu.dao;

import com.pahanaedu.models.Customer;
import com.pahanaedu.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Customer management
 * Handles all database operations related to customers
 *
 * Design Patterns Used:
 * - DAO Pattern: Encapsulates database access logic
 * - Singleton Pattern: Uses DatabaseConnection singleton
 *
 * @author Pahana Edu Development Team
 * @version 1.0
 */
public class CustomerDAO {

    private final DatabaseConnection dbConnection;

    /**
     * Constructor - initialize with database connection
     */
    public CustomerDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Create a new customer
     *
     * @param customer Customer object to create
     * @return true if creation successful, false otherwise
     */
    public boolean createCustomer(Customer customer) {
        // Add basic validation
        if (customer == null || customer.getAccountNumber() == null || 
            customer.getName() == null || customer.getName().trim().isEmpty()) {
            return false;
        }
        
        String sql = """
            INSERT INTO customers (account_number, name, address, phone_number, email, credit_limit, is_active) 
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getAccountNumber());
            stmt.setString(2, customer.getName());
            stmt.setString(3, customer.getAddress());
            stmt.setString(4, customer.getPhoneNumber());
            stmt.setString(5, customer.getEmail());
            stmt.setBigDecimal(6, customer.getCreditLimit());
            stmt.setBoolean(7, customer.isActive());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error creating customer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Find customer by account number
     *
     * @param accountNumber Account number to search for
     * @return Customer object if found, null otherwise
     */
    public Customer findByAccountNumber(String accountNumber) {
        String sql = """
            SELECT account_number, name, address, phone_number, email, credit_limit, 
                   is_active, registration_date, updated_date 
            FROM customers 
            WHERE account_number = ?
            """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createCustomerFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding customer by account number: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Search customers by name (partial match)
     *
     * @param name Name to search for
     * @return List of matching customers
     */
    public List<Customer> searchByName(String name) {
        List<Customer> customers = new ArrayList<>();
        String sql = """
            SELECT account_number, name, address, phone_number, email, credit_limit, 
                   is_active, registration_date, updated_date 
            FROM customers 
            WHERE name LIKE ? AND is_active = TRUE 
            ORDER BY name
            """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + name + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(createCustomerFromResultSet(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error searching customers by name: " + e.getMessage());
            e.printStackTrace();
        }

        return customers;
    }

    /**
     * Update an existing customer
     *
     * @param customer Customer object with updated information
     * @return true if update successful, false otherwise
     */
    public boolean updateCustomer(Customer customer) {
        String sql = """
            UPDATE customers 
            SET name = ?, address = ?, phone_number = ?, email = ?, credit_limit = ?, 
                is_active = ?, updated_date = CURRENT_TIMESTAMP
            WHERE account_number = ?
            """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getAddress());
            stmt.setString(3, customer.getPhoneNumber());
            stmt.setString(4, customer.getEmail());
            stmt.setBigDecimal(5, customer.getCreditLimit());
            stmt.setBoolean(6, customer.isActive());
            stmt.setString(7, customer.getAccountNumber());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating customer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deactivate customer (soft delete)
     *
     * @param accountNumber Account number to deactivate
     * @return true if deactivation successful, false otherwise
     */
    public boolean deactivateCustomer(String accountNumber) {
        String sql = "UPDATE customers SET is_active = FALSE, updated_date = CURRENT_TIMESTAMP WHERE account_number = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountNumber);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deactivating customer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete customer (soft delete - alias for deactivateCustomer)
     *
     * @param accountNumber Account number to delete
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteCustomer(String accountNumber) {
        return deactivateCustomer(accountNumber);
    }

    /**
     * Get all active customers
     *
     * @return List of all active customers
     */
    public List<Customer> getAllActiveCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = """
            SELECT account_number, name, address, phone_number, email, credit_limit, 
                   is_active, registration_date, updated_date 
            FROM customers 
            WHERE is_active = TRUE 
            ORDER BY name
            """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                customers.add(createCustomerFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting all active customers: " + e.getMessage());
            e.printStackTrace();
        }

        return customers;
    }

    /**
     * Check if account number already exists
     *
     * @param accountNumber Account number to check
     * @param excludeAccount Account number to exclude from check (for updates)
     * @return true if account number exists, false otherwise
     */
    public boolean isAccountNumberExists(String accountNumber, String excludeAccount) {
        String sql = "SELECT COUNT(*) FROM customers WHERE account_number = ? AND account_number != ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountNumber);
            stmt.setString(2, excludeAccount != null ? excludeAccount : "");

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error checking account number existence: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Generate next customer account number
     *
     * @return Next available account number
     */
    public String generateNextAccountNumber() {
        String sql = "SELECT MAX(CAST(SUBSTRING(account_number, 4) AS UNSIGNED)) as max_id FROM customers WHERE account_number LIKE 'CUS%'";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int maxId = rs.getInt("max_id");
                return String.format("CUS%03d", maxId + 1);
            }

        } catch (SQLException e) {
            System.err.println("Error generating account number: " + e.getMessage());
        }

        return "CUS001"; // Default if no customers exist
    }

    /**
     * Get customer count for reporting
     *
     * @return Total number of active customers
     */
    public int getActiveCustomerCount() {
        String sql = "SELECT COUNT(*) FROM customers WHERE is_active = TRUE";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting customer count: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Create Customer object from ResultSet
     *
     * @param rs ResultSet containing customer data
     * @return Customer object
     * @throws SQLException if database error occurs
     */
    private Customer createCustomerFromResultSet(ResultSet rs) throws SQLException {
        Customer customer = new Customer();

        customer.setAccountNumber(rs.getString("account_number"));
        customer.setName(rs.getString("name"));
        customer.setAddress(rs.getString("address"));
        customer.setPhoneNumber(rs.getString("phone_number"));
        customer.setEmail(rs.getString("email"));
        customer.setCreditLimit(rs.getBigDecimal("credit_limit"));
        customer.setActive(rs.getBoolean("is_active"));
        customer.setRegistrationDate(rs.getTimestamp("registration_date"));
        customer.setUpdatedDate(rs.getTimestamp("updated_date"));

        return customer;
    }
}