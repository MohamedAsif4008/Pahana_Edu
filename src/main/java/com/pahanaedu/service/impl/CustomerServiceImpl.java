package com.pahanaedu.service.impl;

import com.pahanaedu.dao.CustomerDAO;
import com.pahanaedu.models.Customer;
import com.pahanaedu.service.interfaces.CustomerService;
import com.pahanaedu.util.ValidationUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service implementation for Customer management operations
 * Implements business logic for customer operations
 *
 * Design Patterns Used:
 * - Service Layer Pattern: Encapsulates business logic
 * - Dependency Injection: Uses DAO for data access
 * - Strategy Pattern: Different validation strategies
 * - Template Method Pattern: Common validation workflow
 *
 * @author Pahana Edu Development Team
 * @version 1.0
 */
public class CustomerServiceImpl implements CustomerService {

    private final CustomerDAO customerDAO;

    /**
     * Constructor with dependency injection
     */
    public CustomerServiceImpl() {
        this.customerDAO = new CustomerDAO();
    }

    /**
     * Constructor for testing with DAO injection
     */
    public CustomerServiceImpl(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    @Override
    public boolean createCustomer(Customer customer) {
        // Validate customer data
        if (!validateCustomer(customer)) {
            return false;
        }

        // Check if account number already exists
        if (!isAccountNumberAvailable(customer.getAccountNumber(), null)) {
            System.err.println("Account number already exists: " + customer.getAccountNumber());
            return false;
        }

        // Generate account number if not provided
        if (customer.getAccountNumber() == null || customer.getAccountNumber().trim().isEmpty()) {
            customer.setAccountNumber(generateNextAccountNumber());
        }

        // Set default values
        if (customer.getCreditLimit() == null) {
            customer.setCreditLimit(BigDecimal.ZERO);
        }

        try {
            boolean created = customerDAO.createCustomer(customer);
            if (created) {
                System.out.println("Customer created successfully: " + customer.getAccountNumber());
            }
            return created;
        } catch (Exception e) {
            System.err.println("Error creating customer: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateCustomer(Customer customer) {
        // Validate customer data for update
        if (!validateCustomerForUpdate(customer)) {
            return false;
        }

        // Check if account number is available (excluding current customer)
        if (!isAccountNumberAvailable(customer.getAccountNumber(), customer.getAccountNumber())) {
            System.err.println("Account number already exists: " + customer.getAccountNumber());
            return false;
        }

        // Verify customer exists
        Customer existingCustomer = findCustomerByAccountNumber(customer.getAccountNumber());
        if (existingCustomer == null) {
            System.err.println("Customer not found: " + customer.getAccountNumber());
            return false;
        }

        try {
            boolean updated = customerDAO.updateCustomer(customer);
            if (updated) {
                System.out.println("Customer updated successfully: " + customer.getAccountNumber());
            }
            return updated;
        } catch (Exception e) {
            System.err.println("Error updating customer: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deactivateCustomer(String accountNumber) {
        if (!ValidationUtils.isNotEmpty(accountNumber)) {
            System.err.println("Account number is required");
            return false;
        }

        // Verify customer exists and is active
        Customer customer = findCustomerByAccountNumber(accountNumber);
        if (customer == null) {
            System.err.println("Customer not found: " + accountNumber);
            return false;
        }

        if (!customer.isActive()) {
            System.err.println("Customer is already inactive: " + accountNumber);
            return false;
        }

        try {
            boolean deactivated = customerDAO.deactivateCustomer(accountNumber);
            if (deactivated) {
                System.out.println("Customer deactivated successfully: " + accountNumber);
            }
            return deactivated;
        } catch (Exception e) {
            System.err.println("Error deactivating customer: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Customer findCustomerByAccountNumber(String accountNumber) {
        if (!ValidationUtils.isNotEmpty(accountNumber)) {
            return null;
        }

        try {
            return customerDAO.findByAccountNumber(accountNumber);
        } catch (Exception e) {
            System.err.println("Error finding customer by account number: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Customer> searchCustomersByName(String name) {
        if (!ValidationUtils.isNotEmpty(name)) {
            System.err.println("Search name is required");
            return List.of();
        }

        // Sanitize search input
        String sanitizedName = ValidationUtils.sanitizeInput(name);
        if (!ValidationUtils.isNotEmpty(sanitizedName)) {
            System.err.println("Invalid search name after sanitization");
            return List.of();
        }

        try {
            return customerDAO.searchByName(sanitizedName);
        } catch (Exception e) {
            System.err.println("Error searching customers by name: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<Customer> getAllActiveCustomers() {
        try {
            return customerDAO.getAllActiveCustomers();
        } catch (Exception e) {
            System.err.println("Error getting all active customers: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public boolean validateCustomer(Customer customer) {
        if (customer == null) {
            System.err.println("Customer object is required");
            return false;
        }

        // Validate account number format if provided
        if (customer.getAccountNumber() != null && !customer.getAccountNumber().trim().isEmpty()) {
            if (!ValidationUtils.isValidAccountNumber(customer.getAccountNumber())) {
                System.err.println("Invalid account number format: " + customer.getAccountNumber());
                return false;
            }
        }

        // Validate customer name
        if (!ValidationUtils.isValidName(customer.getName())) {
            System.err.println("Customer name is required and must be valid");
            return false;
        }

        // Validate address if provided
        if (customer.getAddress() != null && !customer.getAddress().trim().isEmpty()) {
            if (!ValidationUtils.isValidAddress(customer.getAddress())) {
                System.err.println("Invalid address format");
                return false;
            }
        }

        // Validate phone number if provided
        if (customer.getPhoneNumber() != null && !customer.getPhoneNumber().trim().isEmpty()) {
            if (!ValidationUtils.isValidPhoneNumber(customer.getPhoneNumber())) {
                System.err.println("Invalid phone number format: " + customer.getPhoneNumber());
                return false;
            }
        }

        // Validate email if provided
        if (customer.getEmail() != null && !customer.getEmail().trim().isEmpty()) {
            if (!ValidationUtils.isValidEmail(customer.getEmail())) {
                System.err.println("Invalid email format: " + customer.getEmail());
                return false;
            }
        }

        // Validate credit limit
        if (customer.getCreditLimit() != null) {
            if (!ValidationUtils.isNonNegative(customer.getCreditLimit())) {
                System.err.println("Credit limit must be non-negative");
                return false;
            }

            // Business rule: Credit limit should not exceed reasonable limit
            BigDecimal maxCreditLimit = new BigDecimal("1000000.00"); // 1 million
            if (customer.getCreditLimit().compareTo(maxCreditLimit) > 0) {
                System.err.println("Credit limit exceeds maximum allowed limit");
                return false;
            }
        }

        // Validate that customer has at least one contact method
        if (!hasValidContactInfo(customer)) {
            System.err.println("Customer must have at least one valid contact method (phone or email)");
            return false;
        }

        return true;
    }

    /**
     * Validate customer data for updates (account number is required)
     */
    private boolean validateCustomerForUpdate(Customer customer) {
        if (customer == null) {
            System.err.println("Customer object is required");
            return false;
        }

        // Validate account number is required for updates
        if (!ValidationUtils.isValidAccountNumber(customer.getAccountNumber())) {
            System.err.println("Valid account number is required for updates");
            return false;
        }

        // Use existing validation for other fields
        return validateCustomer(customer);
    }

    @Override
    public boolean isAccountNumberAvailable(String accountNumber, String excludeAccountNumber) {
        if (!ValidationUtils.isNotEmpty(accountNumber)) {
            return false;
        }

        try {
            return !customerDAO.isAccountNumberExists(accountNumber, excludeAccountNumber);
        } catch (Exception e) {
            System.err.println("Error checking account number availability: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String generateNextAccountNumber() {
        try {
            return customerDAO.generateNextAccountNumber();
        } catch (Exception e) {
            System.err.println("Error generating account number: " + e.getMessage());
            return "CUS001"; // Fallback default
        }
    }

    @Override
    public int getActiveCustomerCount() {
        try {
            return customerDAO.getActiveCustomerCount();
        } catch (Exception e) {
            System.err.println("Error getting active customer count: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public boolean isValidForBilling(Customer customer) {
        if (customer == null) {
            return false;
        }

        // Check if customer is active
        if (!customer.isActive()) {
            System.err.println("Customer is not active: " + customer.getAccountNumber());
            return false;
        }

        // Check required fields for billing
        if (!ValidationUtils.isNotEmpty(customer.getAccountNumber()) ||
                !ValidationUtils.isNotEmpty(customer.getName())) {
            System.err.println("Customer missing required billing information");
            return false;
        }

        return true;
    }

    @Override
    public boolean updateCreditLimit(String accountNumber, BigDecimal newCreditLimit) {
        // Validate inputs
        if (!ValidationUtils.isValidAccountNumber(accountNumber)) {
            System.err.println("Valid account number is required");
            return false;
        }

        if (!ValidationUtils.isNonNegative(newCreditLimit)) {
            System.err.println("Credit limit must be non-negative");
            return false;
        }

        // Business rule: Credit limit validation
        BigDecimal maxCreditLimit = new BigDecimal("1000000.00");
        if (newCreditLimit.compareTo(maxCreditLimit) > 0) {
            System.err.println("Credit limit exceeds maximum allowed limit");
            return false;
        }

        // Find customer and update
        Customer customer = findCustomerByAccountNumber(accountNumber);
        if (customer == null) {
            System.err.println("Customer not found: " + accountNumber);
            return false;
        }

        customer.setCreditLimit(newCreditLimit);
        return updateCustomer(customer);
    }

    @Override
    public List<Customer> getCustomersWithCreditLimits() {
        try {
            List<Customer> allCustomers = customerDAO.getAllActiveCustomers();
            return allCustomers.stream()
                    .filter(customer -> customer.getCreditLimit() != null &&
                            customer.getCreditLimit().compareTo(BigDecimal.ZERO) > 0)
                    .toList();
        } catch (Exception e) {
            System.err.println("Error getting customers with credit limits: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public boolean hasValidContactInfo(Customer customer) {
        if (customer == null) {
            return false;
        }

        boolean hasPhone = customer.getPhoneNumber() != null &&
                !customer.getPhoneNumber().trim().isEmpty() &&
                ValidationUtils.isValidPhoneNumber(customer.getPhoneNumber());

        boolean hasEmail = customer.getEmail() != null &&
                !customer.getEmail().trim().isEmpty() &&
                ValidationUtils.isValidEmail(customer.getEmail());

        return hasPhone || hasEmail;
    }

    /**
     * Business method: Check if customer can make purchases based on credit limit
     *
     * @param customer Customer to check
     * @param purchaseAmount Amount of purchase
     * @return true if customer can make the purchase
     */
    public boolean canMakePurchase(Customer customer, BigDecimal purchaseAmount) {
        if (customer == null || purchaseAmount == null) {
            return false;
        }

        if (!isValidForBilling(customer)) {
            return false;
        }

        // If customer has no credit limit, they can make any purchase
        if (customer.getCreditLimit() == null ||
                customer.getCreditLimit().compareTo(BigDecimal.ZERO) == 0) {
            return true;
        }

        // Check if purchase amount is within credit limit
        return purchaseAmount.compareTo(customer.getCreditLimit()) <= 0;
    }

    /**
     * Business method: Get customer summary for reporting
     *
     * @param accountNumber Customer account number
     * @return Customer summary string
     */
    public String getCustomerSummary(String accountNumber) {
        Customer customer = findCustomerByAccountNumber(accountNumber);
        if (customer == null) {
            return "Customer not found";
        }

        StringBuilder summary = new StringBuilder();
        summary.append("Account: ").append(customer.getAccountNumber()).append("\n");
        summary.append("Name: ").append(customer.getDisplayName()).append("\n");
        summary.append("Status: ").append(customer.isActive() ? "Active" : "Inactive").append("\n");
        summary.append("Contact: ").append(customer.getContactInfo()).append("\n");

        if (customer.getCreditLimit() != null && customer.getCreditLimit().compareTo(BigDecimal.ZERO) > 0) {
            summary.append("Credit Limit: Rs. ").append(customer.getCreditLimit()).append("\n");
        }

        return summary.toString();
    }
}