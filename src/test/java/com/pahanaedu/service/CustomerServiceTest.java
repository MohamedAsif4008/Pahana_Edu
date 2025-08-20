package com.pahanaedu.service;

import com.pahanaedu.dao.CustomerDAO;
import com.pahanaedu.models.Customer;
import com.pahanaedu.service.impl.CustomerServiceImpl;
import com.pahanaedu.service.interfaces.CustomerService;
import com.pahanaedu.util.DatabaseConnection;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for CustomerService business logic
 * Uses ONLY JUnit - No external mocking frameworks
 * Tests real service layer with database integration
 * 
 * @author Pahana Edu Development Team
 * @version 1.0
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Customer Service Tests - JUnit Only")
public class CustomerServiceTest {

    private static CustomerService customerService;
    private static DatabaseConnection dbConnection;
    private static Connection testConnection;

    @BeforeAll
    static void setUpClass() {
        System.out.println("=".repeat(60));
        System.out.println("Starting Customer Service Tests - JUnit Only Implementation");
        System.out.println("=".repeat(60));
        
        // Initialize service with real DAO (no mocking)
        CustomerDAO customerDAO = new CustomerDAO();
        customerService = new CustomerServiceImpl(customerDAO);
        dbConnection = DatabaseConnection.getInstance();
        
        try {
            testConnection = dbConnection.getConnection();
            cleanupTestData();
        } catch (SQLException e) {
            fail("Failed to setup test environment: " + e.getMessage());
        }
    }

    @AfterAll
    static void tearDownClass() {
        try {
            cleanupTestData();
            if (testConnection != null && !testConnection.isClosed()) {
                testConnection.close();
            }
        } catch (SQLException e) {
            System.err.println("Cleanup error: " + e.getMessage());
        }
        System.out.println("=".repeat(60));
        System.out.println("Customer Service Tests Completed");
        System.out.println("=".repeat(60));
    }

    @BeforeEach
    void setUp() {
        System.out.println("Setting up individual test...");
    }

    @AfterEach
    void tearDown() {
        System.out.println("Individual test completed");
    }

    @Test
    @Order(1)
    @DisplayName("TDD Demo: Test Create Customer Service - Red Phase")
    void testCreateCustomer_TDD_RedPhase() {
        System.out.println("\n1. TDD Red Phase - Testing service layer customer creation...");
        
        // Red Phase: Write test first, expecting it to work after implementation
        Customer customer = createTestCustomer("SVC001");
        
        // This should pass after service implementation
        boolean result = customerService.createCustomer(customer);
        assertTrue(result, "Customer creation through service should succeed");
        
        // Verify through service layer
        Customer retrieved = customerService.findByAccountNumber("SVC001");
        assertNotNull(retrieved, "Created customer should be retrievable through service");
        
        System.out.println("   ✓ TDD Red Phase: Service layer test defined and passing");
    }

    @Test
    @Order(2)
    @DisplayName("TDD Demo: Test Business Logic Validation - Green Phase")
    void testCreateCustomer_TDD_GreenPhase() {
        System.out.println("\n2. TDD Green Phase - Testing business logic implementation...");
        
        // Green Phase: Test business rules implementation
        Customer validCustomer = createTestCustomer("SVC002");
        validCustomer.setCreditLimit(new BigDecimal("25000.00"));
        
        boolean result = customerService.createCustomer(validCustomer);
        assertTrue(result, "Valid customer should be created through service");
        
        // Test business rule: Credit limit validation
        Customer highCreditCustomer = createTestCustomer("SVC003");
        highCreditCustomer.setCreditLimit(new BigDecimal("75000.00"));
        
        boolean highCreditResult = customerService.createCustomerWithCreditCheck(highCreditCustomer);
        assertTrue(highCreditResult, "High credit customer should be created with additional validation");
        
        System.out.println("   ✓ TDD Green Phase: Business logic implemented and tested");
    }

    @Test
    @Order(3)
    @DisplayName("Test Service Layer Input Validation")
    void testServiceValidation() {
        System.out.println("\n3. Testing service layer input validation...");
        
        // Test null customer
        boolean result1 = customerService.createCustomer(null);
        assertFalse(result1, "Service should reject null customer");
        
        // Test customer with invalid data
        Customer invalidCustomer = new Customer();
        invalidCustomer.setAccountNumber("INVALID");
        invalidCustomer.setName("");
        boolean result2 = customerService.createCustomer(invalidCustomer);
        assertFalse(result2, "Service should reject customer with invalid data");
        
        // Test customer with null account number
        Customer invalidCustomer2 = new Customer();
        invalidCustomer2.setName("Valid Name");
        boolean result3 = customerService.createCustomer(invalidCustomer2);
        assertFalse(result3, "Service should reject customer with null account number");
        
        System.out.println("   ✓ Service layer validation working correctly");
    }

    @Test
    @Order(4)
    @DisplayName("Test Duplicate Prevention Business Rule")
    void testDuplicatePreventionBusinessRule() {
        System.out.println("\n4. Testing duplicate prevention business rule...");
        
        // Create first customer
        Customer customer1 = createTestCustomer("SVC004");
        boolean first = customerService.createCustomer(customer1);
        assertTrue(first, "First customer should be created");
        
        // Try to create duplicate
        Customer customer2 = createTestCustomer("SVC004");
        customer2.setName("Different Name");
        customer2.setEmail("different@email.com");
        
        boolean second = customerService.createCustomer(customer2);
        assertFalse(second, "Service should prevent duplicate account numbers");
        
        System.out.println("   ✓ Duplicate prevention business rule working correctly");
    }

    @Test
    @Order(5)
    @DisplayName("Test Customer Update Through Service")
    void testUpdateCustomerThroughService() {
        System.out.println("\n5. Testing customer update through service layer...");
        
        // Create customer first
        Customer customer = createTestCustomer("SVC005");
        customerService.createCustomer(customer);
        
        // Update through service
        customer.setName("Updated Service Name");
        customer.setEmail("updated.service@email.com");
        customer.setPhoneNumber("+94772345678");
        
        boolean updated = customerService.updateCustomer(customer);
        assertTrue(updated, "Customer update through service should succeed");
        
        // Verify update through service
        Customer retrieved = customerService.findByAccountNumber("SVC005");
        assertEquals("Updated Service Name", retrieved.getName());
        assertEquals("updated.service@email.com", retrieved.getEmail());
        assertEquals("+94772345678", retrieved.getPhoneNumber());
        
        System.out.println("   ✓ Customer update through service working correctly");
    }

    @Test
    @Order(6)
    @DisplayName("Test Search Functionality Through Service")
    void testSearchThroughService() {
        System.out.println("\n6. Testing search functionality through service...");
        
        // Create test customers
        Customer customer1 = createTestCustomer("SVC006");
        customer1.setName("Alice Johnson Service");
        customerService.createCustomer(customer1);
        
        Customer customer2 = createTestCustomer("SVC007");
        customer2.setName("Bob Johnson Service");
        customerService.createCustomer(customer2);
        
        Customer customer3 = createTestCustomer("SVC008");
        customer3.setName("Charlie Smith Service");
        customerService.createCustomer(customer3);
        
        // Test search by name through service
        List<Customer> johnsonResults = customerService.searchByName("Johnson");
        assertNotNull(johnsonResults, "Search results should not be null");
        assertTrue(johnsonResults.size() >= 2, "Should find customers with 'Johnson' in name");
        
        // Test search with empty string
        List<Customer> emptyResults = customerService.searchByName("");
        assertNotNull(emptyResults, "Empty search should return empty list, not null");
        assertTrue(emptyResults.isEmpty(), "Empty search should return empty list");
        
        // Test search with null
        List<Customer> nullResults = customerService.searchByName(null);
        assertNotNull(nullResults, "Null search should return empty list, not null");
        assertTrue(nullResults.isEmpty(), "Null search should return empty list");
        
        System.out.println("   ✓ Search functionality through service working correctly");
    }

    @Test
    @Order(7)
    @DisplayName("Test Get All Active Customers Through Service")
    void testGetAllActiveCustomersThroughService() {
        System.out.println("\n7. Testing get all active customers through service...");
        
        // Create mix of active and inactive customers
        Customer activeCustomer1 = createTestCustomer("SVC009");
        activeCustomer1.setActive(true);
        customerService.createCustomer(activeCustomer1);
        
        Customer activeCustomer2 = createTestCustomer("SVC010");
        activeCustomer2.setActive(true);
        customerService.createCustomer(activeCustomer2);
        
        // Create and then deactivate a customer
        Customer toDeactivate = createTestCustomer("SVC011");
        customerService.createCustomer(toDeactivate);
        customerService.deactivateCustomer("SVC011");
        
        // Get all active customers through service
        List<Customer> activeCustomers = customerService.getAllActiveCustomers();
        assertNotNull(activeCustomers, "Active customers list should not be null");
        
        // Verify only active customers are returned
        long testActiveCount = activeCustomers.stream()
                .filter(c -> c.getAccountNumber().startsWith("SVC"))
                .filter(Customer::isActive)
                .count();
        
        assertTrue(testActiveCount >= 2, "Should find at least 2 active test customers");
        
        System.out.println("   ✓ Get all active customers through service working correctly");
    }

    @Test
    @Order(8)
    @DisplayName("Test Customer Activation/Deactivation Business Logic")
    void testActivationDeactivationBusinessLogic() {
        System.out.println("\n8. Testing activation/deactivation business logic...");
        
        // Create customer
        Customer customer = createTestCustomer("SVC012");
        customerService.createCustomer(customer);
        
        // Test deactivation
        boolean deactivated = customerService.deactivateCustomer("SVC012");
        assertTrue(deactivated, "Customer deactivation should succeed");
        
        Customer afterDeactivation = customerService.findByAccountNumber("SVC012");
        assertNotNull(afterDeactivation, "Customer should still exist after deactivation");
        assertFalse(afterDeactivation.isActive(), "Customer should be inactive");
        
        // Test reactivation
        boolean reactivated = customerService.activateCustomer("SVC012");
        assertTrue(reactivated, "Customer reactivation should succeed");
        
        Customer afterReactivation = customerService.findByAccountNumber("SVC012");
        assertTrue(afterReactivation.isActive(), "Customer should be active again");
        
        // Test activation of non-existent customer
        boolean nonExistentActivation = customerService.activateCustomer("NONEXISTENT");
        assertFalse(nonExistentActivation, "Activation of non-existent customer should fail");
        
        System.out.println("   ✓ Activation/deactivation business logic working correctly");
    }

    @Test
    @Order(9)
    @DisplayName("Test Account Number Generation Business Logic")
    void testAccountNumberGeneration() {
        System.out.println("\n9. Testing account number generation business logic...");
        
        // Test account number generation
        String newAccountNumber = customerService.generateNewAccountNumber();
        assertNotNull(newAccountNumber, "Generated account number should not be null");
        assertTrue(newAccountNumber.startsWith("CUS"), "Account number should start with 'CUS'");
        assertTrue(newAccountNumber.length() >= 6, "Account number should be at least 6 characters");
        
        // Create a customer with the first generated number to increment the counter
        Customer customer = new Customer();
        customer.setAccountNumber(newAccountNumber);
        customer.setName("Test Customer for Generation");
        customer.setPhoneNumber("+94771234567");
        customer.setEmail("test@email.com");
        customerService.createCustomer(customer);
        
        // Test that generated number is unique
        String anotherAccountNumber = customerService.generateNewAccountNumber();
        assertNotEquals(newAccountNumber, anotherAccountNumber, "Sequential account numbers should be different");
        
        System.out.println("   ✓ Account number generation working correctly");
        System.out.println("     Generated numbers: " + newAccountNumber + ", " + anotherAccountNumber);
    }

    @Test
    @Order(10)
    @DisplayName("Test Credit Limit Business Rules")
    void testCreditLimitBusinessRules() {
        System.out.println("\n10. Testing credit limit business rules...");
        
        // Test normal credit limit
        Customer normalCustomer = createTestCustomer("SVC013");
        normalCustomer.setCreditLimit(new BigDecimal("25000.00"));
        
        boolean normalResult = customerService.createCustomerWithCreditCheck(normalCustomer);
        assertTrue(normalResult, "Customer with normal credit limit should be created");
        
        // Test high credit limit (business rule triggers additional validation)
        Customer highCreditCustomer = createTestCustomer("SVC014");
        highCreditCustomer.setCreditLimit(new BigDecimal("75000.00"));
        
        boolean highCreditResult = customerService.createCustomerWithCreditCheck(highCreditCustomer);
        assertTrue(highCreditResult, "Customer with high credit limit should be created with additional validation");
        
        // Verify both customers were created
        Customer normalRetrieved = customerService.findByAccountNumber("SVC013");
        Customer highCreditRetrieved = customerService.findByAccountNumber("SVC014");
        
        assertNotNull(normalRetrieved, "Normal credit customer should be retrievable");
        assertNotNull(highCreditRetrieved, "High credit customer should be retrievable");
        
        assertEquals(new BigDecimal("25000.00"), normalRetrieved.getCreditLimit());
        assertEquals(new BigDecimal("75000.00"), highCreditRetrieved.getCreditLimit());
        
        System.out.println("   ✓ Credit limit business rules working correctly");
    }

    @Test
    @Order(11)
    @DisplayName("Test Service Layer Error Handling")
    void testServiceErrorHandling() {
        System.out.println("\n11. Testing service layer error handling...");
        
        // Test finding non-existent customer
        Customer nonExistent = customerService.findByAccountNumber("DOESNOTEXIST");
        assertNull(nonExistent, "Non-existent customer should return null");
        
        // Test updating non-existent customer
        Customer fakeCustomer = createTestCustomer("FAKE123");
        boolean updateResult = customerService.updateCustomer(fakeCustomer);
        assertFalse(updateResult, "Updating non-existent customer should fail");
        
        // Test deletion of non-existent customer
        boolean deleteResult = customerService.deleteCustomer("DOESNOTEXIST");
        assertFalse(deleteResult, "Deleting non-existent customer should fail gracefully");
        
        System.out.println("   ✓ Service layer error handling working correctly");
    }

    @Test
    @Order(12)
    @DisplayName("Test Service Transaction Integrity")
    void testServiceTransactionIntegrity() {
        System.out.println("\n12. Testing service transaction integrity...");
        
        // Create customer and verify it exists
        Customer customer = createTestCustomer("SVC015");
        boolean created = customerService.createCustomer(customer);
        assertTrue(created, "Customer should be created successfully");
        
        Customer beforeUpdate = customerService.findByAccountNumber("SVC015");
        assertNotNull(beforeUpdate, "Customer should exist before update");
        String originalName = beforeUpdate.getName();
        
        // Update customer
        customer.setName("Transaction Test Name");
        customer.setEmail("transaction@test.com");
        
        boolean updated = customerService.updateCustomer(customer);
        assertTrue(updated, "Customer update should succeed");
        
        // Verify update persistence
        Customer afterUpdate = customerService.findByAccountNumber("SVC015");
        assertNotNull(afterUpdate, "Customer should exist after update");
        assertEquals("Transaction Test Name", afterUpdate.getName());
        assertEquals("transaction@test.com", afterUpdate.getEmail());
        assertNotEquals(originalName, afterUpdate.getName());
        
        System.out.println("   ✓ Service transaction integrity working correctly");
    }

    @Test
    @Order(13)
    @DisplayName("Test Service Performance with Multiple Operations")
    void testServicePerformance() {
        System.out.println("\n13. Testing service performance with multiple operations...");
        
        long startTime = System.currentTimeMillis();
        
        // Create multiple customers through service with valid account numbers
        for (int i = 1; i <= 20; i++) {
            Customer customer = new Customer();
            customer.setAccountNumber("PER" + String.format("%03d", i));
            customer.setName("Performance Test Customer " + i);
            customer.setAddress("Test Address, Colombo");
            customer.setPhoneNumber("+94771234567");
            customer.setEmail("perf" + i + "@email.com");
            customer.setCreditLimit(new BigDecimal("25000.00"));
            customer.setActive(true);
            customerService.createCustomer(customer);
        }
        
        long creationTime = System.currentTimeMillis();
        
        // Search operations
        List<Customer> allActive = customerService.getAllActiveCustomers();
        List<Customer> searchResults = customerService.searchByName("Performance");
        
        long searchTime = System.currentTimeMillis();
        
        // Verify results
        assertNotNull(allActive, "All active customers should not be null");
        assertNotNull(searchResults, "Search results should not be null");
        assertTrue(searchResults.size() >= 20, "Should find performance test customers");
        
        // Performance verification
        long totalCreationTime = creationTime - startTime;
        long totalSearchTime = searchTime - creationTime;
        
        assertTrue(totalCreationTime < 10000, "Creation should complete within 10 seconds");
        assertTrue(totalSearchTime < 2000, "Search should complete within 2 seconds");
        
        System.out.println("   ✓ Service performance testing completed");
        System.out.println("     Creation time: " + totalCreationTime + "ms for 20 customers");
        System.out.println("     Search time: " + totalSearchTime + "ms");
    }

    /**
     * Helper method to create test customer
     */
    private static Customer createTestCustomer(String accountNumber) {
        Customer customer = new Customer();
        customer.setAccountNumber(accountNumber);
        // Fix: Extract the numeric part properly
        String numberPart = accountNumber.substring(accountNumber.length() - 3);
        int number = Integer.parseInt(numberPart);
        customer.setName("Test Customer " + number);
        customer.setAddress("Test Address, Colombo");
        customer.setPhoneNumber("+94771234567");
        customer.setEmail("test" + accountNumber.toLowerCase() + "@email.com");
        customer.setCreditLimit(new BigDecimal("25000.00"));
        customer.setActive(true);
        return customer;
    }

    /**
     * Clean up test data
     */
    private static void cleanupTestData() {
        try {
            String sql = "DELETE FROM customers WHERE account_number LIKE 'SVC%' OR account_number LIKE 'PERF%' OR account_number LIKE 'FAKE%'";
            try (PreparedStatement stmt = testConnection.prepareStatement(sql)) {
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Cleanup error: " + e.getMessage());
        }
    }
}