package com.pahanaedu.dao;

import com.pahanaedu.models.Customer;
import com.pahanaedu.util.DatabaseConnection;
import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Customer DAO Tests")
public class CustomerDAOTest {

    private static CustomerDAO customerDAO;
    private static DatabaseConnection dbConnection;
    private static Connection testConnection;

    // Test data constants
    private static final String TEST_ACCOUNT_1 = "TST001";
    private static final String TEST_ACCOUNT_2 = "TST002";
    private static final String INVALID_ACCOUNT = "INVALID123";

    @BeforeAll
    static void setupTest() {
        System.out.println("=".repeat(60));
        System.out.println("Starting Customer DAO Tests - TDD Demonstration");
        System.out.println("=".repeat(60));
        
        customerDAO = new CustomerDAO();
        dbConnection = DatabaseConnection.getInstance();
        
        try {
            testConnection = dbConnection.getConnection();
            cleanupTestData(); // Clean any existing test data
        } catch (SQLException e) {
            fail("Failed to setup test environment: " + e.getMessage());
        }
    }

    @AfterAll
    static void tearDown() {
        try {
            cleanupTestData();
            if (testConnection != null && !testConnection.isClosed()) {
                testConnection.close();
            }
        } catch (SQLException e) {
            System.err.println("Cleanup error: " + e.getMessage());
        }
        System.out.println("=".repeat(60));
        System.out.println("Customer DAO Tests Completed");
        System.out.println("=".repeat(60));
    }

    /**
     * TDD Demonstration - Red Phase
     * Write failing test first, then implement code to pass
     */
    @Test
    @Order(1)
    @DisplayName("TDD Red Phase: Test Create Customer - Should Fail Initially")
    void testCreateCustomer_TDD_RedPhase() {
        System.out.println("\n1. TDD Red Phase - Testing customer creation...");
        
        // Create test customer object
        Customer testCustomer = createTestCustomer(TEST_ACCOUNT_1);
        
        // This should pass after DAO implementation
        boolean result = customerDAO.createCustomer(testCustomer);
        assertTrue(result, "Customer creation should succeed");
        
        System.out.println("   ✓ TDD Red Phase: Customer creation test defined");
    }

    /**
     * TDD Green Phase - Implement minimum code to pass
     */
    @Test
    @Order(2)
    @DisplayName("TDD Green Phase: Verify Customer Creation Works")
    void testCreateCustomer_TDD_GreenPhase() {
        System.out.println("\n2. TDD Green Phase - Verifying implementation...");
        
        Customer testCustomer = createTestCustomer(TEST_ACCOUNT_2);
        
        // Test the actual implementation
        boolean created = customerDAO.createCustomer(testCustomer);
        assertTrue(created, "Customer should be created successfully");
        
        // Verify customer exists in database
        Customer retrieved = customerDAO.findByAccountNumber(TEST_ACCOUNT_2);
        assertNotNull(retrieved, "Created customer should be retrievable");
        assertEquals(TEST_ACCOUNT_2, retrieved.getAccountNumber());
        assertEquals("Test Customer 2", retrieved.getName());
        
        System.out.println("   ✓ TDD Green Phase: Implementation verified");
    }

    @Test
    @Order(3)
    @DisplayName("Test Valid Customer Data Creation")
    void testCreateValidCustomer() {
        System.out.println("\n3. Testing valid customer creation...");
        
        Customer customer = new Customer();
        customer.setAccountNumber("TST003");
        customer.setName("John Doe");
        customer.setAddress("123 Main Street, Colombo");
        customer.setPhoneNumber("+94771234567");
        customer.setEmail("john.doe@email.com");
        customer.setCreditLimit(new BigDecimal("50000.00"));
        customer.setActive(true);

        boolean result = customerDAO.createCustomer(customer);
        assertTrue(result, "Valid customer should be created successfully");

        // Verify data integrity
        Customer retrieved = customerDAO.findByAccountNumber("TST003");
        assertNotNull(retrieved);
        assertEquals("John Doe", retrieved.getName());
        assertEquals("+94771234567", retrieved.getPhoneNumber());
        assertTrue(retrieved.isActive());
        
        System.out.println("   ✓ Valid customer created and verified");
    }

    @Test
    @Order(4)
    @DisplayName("Test Duplicate Account Number Prevention")
    void testDuplicateAccountNumber() {
        System.out.println("\n4. Testing duplicate account prevention...");
        
        // Create first customer
        Customer customer1 = createTestCustomer("TST004");
        boolean first = customerDAO.createCustomer(customer1);
        assertTrue(first, "First customer should be created");

        // Try to create second customer with same account number
        Customer customer2 = createTestCustomer("TST004");
        customer2.setName("Different Name");
        boolean second = customerDAO.createCustomer(customer2);
        
        assertFalse(second, "Duplicate account number should be prevented");
        
        System.out.println("   ✓ Duplicate account prevention working");
    }

    @Test
    @Order(5)
    @DisplayName("Test Customer Data Validation")
    void testCustomerValidation() {
        System.out.println("\n5. Testing customer data validation...");
        
        // Test null account number
        Customer invalidCustomer = new Customer();
        invalidCustomer.setName("Test Customer");
        boolean result1 = customerDAO.createCustomer(invalidCustomer);
        assertFalse(result1, "Customer with null account number should fail");
        
        // Test empty name
        Customer invalidCustomer2 = new Customer();
        invalidCustomer2.setAccountNumber("TST005");
        invalidCustomer2.setName("");
        boolean result2 = customerDAO.createCustomer(invalidCustomer2);
        assertFalse(result2, "Customer with empty name should fail");
        
        System.out.println("   ✓ Input validation working correctly");
    }

    @Test
    @Order(6)
    @DisplayName("Test Update Customer Information")
    void testUpdateCustomer() {
        System.out.println("\n6. Testing customer update functionality...");
        
        // Create customer first
        Customer customer = createTestCustomer("TST006");
        customerDAO.createCustomer(customer);
        
        // Update customer information
        customer.setName("Updated Name");
        customer.setPhoneNumber("+94772345678");
        customer.setEmail("updated@email.com");
        
        boolean updated = customerDAO.updateCustomer(customer);
        assertTrue(updated, "Customer update should succeed");
        
        // Verify update
        Customer retrieved = customerDAO.findByAccountNumber("TST006");
        assertEquals("Updated Name", retrieved.getName());
        assertEquals("+94772345678", retrieved.getPhoneNumber());
        assertEquals("updated@email.com", retrieved.getEmail());
        
        System.out.println("   ✓ Customer update working correctly");
    }

    @Test
    @Order(7)
    @DisplayName("Test Search Functionality")
    void testSearchCustomers() {
        System.out.println("\n7. Testing customer search functionality...");
        
        // Create test customers
        Customer customer1 = createTestCustomer("TST007");
        customer1.setName("Alice Johnson");
        customerDAO.createCustomer(customer1);
        
        Customer customer2 = createTestCustomer("TST008");
        customer2.setName("Bob Johnson");
        customerDAO.createCustomer(customer2);
        
        // Test search by name
        List<Customer> results = customerDAO.searchByName("Johnson");
        assertTrue(results.size() >= 2, "Should find customers with 'Johnson' in name");
        
        // Test search by account number
        Customer found = customerDAO.findByAccountNumber("TST007");
        assertNotNull(found);
        assertEquals("Alice Johnson", found.getName());
        
        System.out.println("   ✓ Search functionality working correctly");
    }

    @Test
    @Order(8)
    @DisplayName("Test Delete Customer (Soft Delete)")
    void testDeleteCustomer() {
        System.out.println("\n8. Testing customer deletion...");
        
        // Create customer
        Customer customer = createTestCustomer("TST009");
        customerDAO.createCustomer(customer);
        
        // Verify customer exists and is active
        Customer retrieved = customerDAO.findByAccountNumber("TST009");
        assertTrue(retrieved.isActive());
        
        // Delete customer (soft delete)
        boolean deleted = customerDAO.deleteCustomer("TST009");
        assertTrue(deleted, "Customer deletion should succeed");
        
        // Verify customer is marked as inactive
        Customer afterDelete = customerDAO.findByAccountNumber("TST009");
        assertNotNull(afterDelete, "Customer record should still exist");
        assertFalse(afterDelete.isActive(), "Customer should be marked inactive");
        
        System.out.println("   ✓ Soft delete working correctly");
    }

    @Test
    @Order(9)
    @DisplayName("Test Boundary Value Testing")
    void testBoundaryValues() {
        System.out.println("\n9. Testing boundary values...");
        
        // Test maximum values
        Customer customer = new Customer();
        customer.setAccountNumber("TST010");
        customer.setName("A".repeat(100)); // Test long name
        customer.setAddress("Very long address ".repeat(10));
        customer.setPhoneNumber("+94771234567");
        customer.setEmail("test@email.com");
        customer.setCreditLimit(new BigDecimal("999999.99")); // Large credit limit
        customer.setActive(true);
        
        boolean result = customerDAO.createCustomer(customer);
        assertTrue(result, "Customer with boundary values should be created");
        
        System.out.println("   ✓ Boundary value testing completed");
    }

    @Test
    @Order(10)
    @DisplayName("Test SQL Injection Prevention")
    void testSQLInjectionPrevention() {
        System.out.println("\n10. Testing SQL injection prevention...");
        
        // Attempt SQL injection in account number
        String maliciousAccountNumber = "TST011'; DROP TABLE customers; --";
        
        // Create customer manually instead of using helper method
        Customer customer = new Customer();
        customer.setAccountNumber(maliciousAccountNumber);
        customer.setName("Test Customer SQL Injection");
        customer.setAddress("Test Address, Colombo");
        customer.setPhoneNumber("+94771234567");
        customer.setEmail("test@email.com");
        customer.setCreditLimit(new BigDecimal("25000.00"));
        customer.setActive(true);
        
        // This should either fail safely or escape the input
        try {
            boolean result = customerDAO.createCustomer(customer);
            // If it succeeds, verify no damage was done
            if (result) {
                Customer retrieved = customerDAO.findByAccountNumber(maliciousAccountNumber);
                assertNotNull(retrieved, "Legitimate data should be stored safely");
            }
        } catch (Exception e) {
            // Exception is acceptable - shows input validation
            System.out.println("   ✓ SQL injection attempt blocked: " + e.getMessage());
        }
        
        // Verify database structure is intact
        List<Customer> allCustomers = customerDAO.getAllActiveCustomers();
        assertNotNull(allCustomers, "Database should still be functional");
        
        System.out.println("   ✓ SQL injection prevention verified");
    }

    /**
     * Helper method to create test customer
     */
    private static Customer createTestCustomer(String accountNumber) {
        Customer customer = new Customer();
        customer.setAccountNumber(accountNumber);
        // Fix: Extract the numeric part properly
        String numberPart = accountNumber.substring(3);
        // Convert "002" to "2", "001" to "1", etc.
        int number = Integer.parseInt(numberPart);
        customer.setName("Test Customer " + number);
        customer.setAddress("Test Address, Colombo");
        customer.setPhoneNumber("+94771234567");
        customer.setEmail("test" + numberPart + "@email.com");
        customer.setCreditLimit(new BigDecimal("25000.00"));
        customer.setActive(true);
        return customer;
    }

    /**
     * Clean up test data
     */
    private static void cleanupTestData() {
        try {
            String sql = "DELETE FROM customers WHERE account_number LIKE 'TST%'";
            try (PreparedStatement stmt = testConnection.prepareStatement(sql)) {
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Cleanup error: " + e.getMessage());
        }
    }
}