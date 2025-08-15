package com.pahanaedu.service.interfaces;

import com.pahanaedu.models.Customer;
import java.util.List;

/**
 * Service interface for Customer management operations
 * Defines business logic operations for customer management
 *
 * Design Pattern: Service Layer Pattern
 * - Separates business logic from data access
 * - Provides transaction boundaries
 * - Encapsulates business rules
 *
 * @author Pahana Edu Development Team
 * @version 1.0
 */
public interface CustomerService {

    /**
     * Create a new customer with validation
     *
     * @param customer Customer object to create
     * @return true if creation successful, false otherwise
     */
    boolean createCustomer(Customer customer);

    /**
     * Update an existing customer with validation
     *
     * @param customer Customer object with updated information
     * @return true if update successful, false otherwise
     */
    boolean updateCustomer(Customer customer);

    /**
     * Deactivate customer account (soft delete)
     *
     * @param accountNumber Customer account number to deactivate
     * @return true if deactivation successful, false otherwise
     */
    boolean deactivateCustomer(String accountNumber);

    /**
     * Find customer by account number
     *
     * @param accountNumber Account number to search for
     * @return Customer object if found, null otherwise
     */
    Customer findCustomerByAccountNumber(String accountNumber);

    /**
     * Search customers by name (partial match)
     *
     * @param name Name to search for
     * @return List of matching customers
     */
    List<Customer> searchCustomersByName(String name);

    /**
     * Get all active customers
     *
     * @return List of all active customers
     */
    List<Customer> getAllActiveCustomers();

    /**
     * Validate customer data
     *
     * @param customer Customer object to validate
     * @return true if customer data is valid, false otherwise
     */
    boolean validateCustomer(Customer customer);

    /**
     * Check if account number is available
     *
     * @param accountNumber Account number to check
     * @param excludeAccountNumber Account number to exclude from check (for updates)
     * @return true if account number is available, false otherwise
     */
    boolean isAccountNumberAvailable(String accountNumber, String excludeAccountNumber);

    /**
     * Generate next customer account number
     *
     * @return Next available account number
     */
    String generateNextAccountNumber();

    /**
     * Get customer count for reporting
     *
     * @return Total number of active customers
     */
    int getActiveCustomerCount();

    /**
     * Validate customer for billing operations
     *
     * @param customer Customer to validate
     * @return true if customer is valid for billing, false otherwise
     */
    boolean isValidForBilling(Customer customer);

    /**
     * Update customer credit limit with validation
     *
     * @param accountNumber Customer account number
     * @param newCreditLimit New credit limit
     * @return true if update successful, false otherwise
     */
    boolean updateCreditLimit(String accountNumber, java.math.BigDecimal newCreditLimit);

    /**
     * Get customers with credit limits
     *
     * @return List of customers who have credit limits
     */
    List<Customer> getCustomersWithCreditLimits();

    /**
     * Validate customer contact information
     *
     * @param customer Customer to validate
     * @return true if contact information is valid, false otherwise
     */
    boolean hasValidContactInfo(Customer customer);
}