package com.pahanaedu.servlets.customer;

import com.pahanaedu.models.Customer;
import com.pahanaedu.models.User;
import com.pahanaedu.service.interfaces.CustomerService;
import com.pahanaedu.service.impl.CustomerServiceImpl;
import com.pahanaedu.servlets.common.BaseServlet;
import com.pahanaedu.util.ValidationUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * CustomerServlet - Fixed to match working pattern from original code
 */
@WebServlet(name = "CustomerServlet", urlPatterns = {"/customers", "/customer"})
public class CustomerServlet extends BaseServlet {

    private CustomerService customerService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.customerService = new CustomerServiceImpl();
        System.out.println("CustomerServlet initialized successfully");
    }

    /**
     * Handle GET requests - Display customers, show forms, get customer details
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Check authentication
            if (!isUserLoggedIn(request)) {
                redirectTo(response, request.getContextPath() + "/login");
                return;
            }

            // Get action parameter
            String action = getParameter(request, PARAM_ACTION, "list");

            switch (action.toLowerCase()) {
                case "list":
                    showCustomerList(request, response);
                    break;
                case "view":
                    showCustomerDetails(request, response);
                    break;
                case "create":
                    showCreateCustomerForm(request, response);
                    break;
                case "edit":
                    showEditCustomerForm(request, response);
                    break;
                case "delete":
                    confirmDeleteCustomer(request, response);
                    break;
                default:
                    showCustomerList(request, response);
                    break;
            }

        } catch (Exception e) {
            System.err.println("Error in CustomerServlet doGet: " + e.getMessage());
            e.printStackTrace();
            handleException(request, response, e);
        }
    }

    /**
     * Handle POST requests - Create and update customers
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Check authentication and authorization
            if (!isUserLoggedIn(request)) {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
                return;
            }

            // Get action parameter
            String action = getParameter(request, PARAM_ACTION, "create");

            switch (action.toLowerCase()) {
                case "create":
                    createCustomer(request, response);
                    break;
                case "update":
                    updateCustomer(request, response);
                    break;
                case "delete":
                    deleteCustomer(request, response);
                    break;
                default:
                    setErrorMessage(request, "Invalid action specified");
                    showCustomerList(request, response);
                    break;
            }

        } catch (Exception e) {
            System.err.println("Error in CustomerServlet doPost: " + e.getMessage());
            e.printStackTrace();
            handleException(request, response, e);
        }
    }

    /**
     * Show customer list with pagination and search - FIXED
     */
    private void showCustomerList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Get pagination parameters
            int[] pagination = getPaginationParams(request);
            int page = pagination[0];
            int size = pagination[1];

            // Get search parameter
            String searchTerm = getSanitizedParameter(request, "search");

            List<Customer> customers;
            int totalCustomers;

            if (ValidationUtils.isNotEmpty(searchTerm)) {
                // Search customers by name
                customers = customerService.searchCustomersByName(searchTerm);
                totalCustomers = customers.size();

                // Apply pagination to search results
                int startIndex = (page - 1) * size;
                int endIndex = Math.min(startIndex + size, customers.size());
                if (startIndex < customers.size()) {
                    customers = customers.subList(startIndex, endIndex);
                } else {
                    customers = List.of();
                }
            } else {
                // Get all active customers (for simplicity)
                customers = customerService.getAllActiveCustomers();
                totalCustomers = customers.size();

                // Apply pagination
                int startIndex = (page - 1) * size;
                int endIndex = Math.min(startIndex + size, customers.size());
                if (startIndex < customers.size()) {
                    customers = customers.subList(startIndex, endIndex);
                } else {
                    customers = List.of();
                }
            }

            // Calculate pagination info
            int totalPages = (int) Math.ceil((double) totalCustomers / size);

            // Set request attributes
            request.setAttribute("customers", customers);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalCustomers", totalCustomers);
            request.setAttribute("searchTerm", searchTerm);

            // Count active customers for stats
            long activeCount = customers.stream().filter(Customer::isActive).count();
            request.setAttribute("activeCustomersCount", activeCount);

            // Log action
            logAction(request, "VIEW_CUSTOMER_LIST", "Page: " + page + ", Search: " + searchTerm);

            // Forward to customer list JSP
            forwardToJSP(request, response, "customer/list.jsp");

        } catch (Exception e) {
            System.err.println("Error showing customer list: " + e.getMessage());
            setErrorMessage(request, "Error loading customer list");
            forwardToJSP(request, response, "common/error.jsp");
        }
    }

    /**
     * Show customer details - FIXED to match working pattern
     */
    private void showCustomerDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accountNumber = getSanitizedParameter(request, PARAM_ID);
        if (!ValidationUtils.isValidAccountNumber(accountNumber)) {
            setErrorMessage(request, "Invalid customer account number");
            showCustomerList(request, response);
            return;
        }

        try {
            Customer customer = customerService.findCustomerByAccountNumber(accountNumber);
            if (customer == null) {
                setErrorMessage(request, "Customer not found");
                showCustomerList(request, response);
                return;
            }

            // Set request attributes
            request.setAttribute("customer", customer);

            // Log action
            logAction(request, "VIEW_CUSTOMER", "Account: " + accountNumber);

            // Forward to customer details JSP
            forwardToJSP(request, response, "customer/view.jsp");

        } catch (Exception e) {
            System.err.println("Error showing customer details: " + e.getMessage());
            setErrorMessage(request, "Error loading customer details");
            showCustomerList(request, response);
        }
    }

    /**
     * Show create customer form - FIXED
     */
    private void showCreateCustomerForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check if user has permission to create customers
        User currentUser = getCurrentUser(request);
        if (!currentUser.hasPermission("CUSTOMER_MANAGEMENT")) {
            setErrorMessage(request, "You don't have permission to create customers");
            showCustomerList(request, response);
            return;
        }

        // Generate next account number
        String nextAccountNumber = customerService.generateNextAccountNumber();

        // Set request attributes
        request.setAttribute("nextAccountNumber", nextAccountNumber);

        // Forward to create customer form JSP
        forwardToJSP(request, response, "customer/create.jsp");
    }

    /**
     * Show edit customer form - FIXED
     */
    private void showEditCustomerForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accountNumber = getSanitizedParameter(request, PARAM_ID);
        if (!ValidationUtils.isValidAccountNumber(accountNumber)) {
            setErrorMessage(request, "Invalid customer account number");
            showCustomerList(request, response);
            return;
        }

        try {
            Customer customer = customerService.findCustomerByAccountNumber(accountNumber);
            if (customer == null) {
                setErrorMessage(request, "Customer not found");
                showCustomerList(request, response);
                return;
            }

            // Check permission
            User currentUser = getCurrentUser(request);
            if (!currentUser.hasPermission("CUSTOMER_MANAGEMENT")) {
                setErrorMessage(request, "You don't have permission to edit customers");
                showCustomerList(request, response);
                return;
            }

            // Set request attributes
            request.setAttribute("customer", customer);

            // Forward to edit customer form JSP
            forwardToJSP(request, response, "customer/edit.jsp");

        } catch (Exception e) {
            System.err.println("Error showing edit form: " + e.getMessage());
            setErrorMessage(request, "Error loading customer for editing");
            showCustomerList(request, response);
        }
    }

    /**
     * Confirm delete customer - FIXED
     */
    private void confirmDeleteCustomer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accountNumber = getSanitizedParameter(request, PARAM_ID);
        if (!ValidationUtils.isValidAccountNumber(accountNumber)) {
            setErrorMessage(request, "Invalid customer account number");
            showCustomerList(request, response);
            return;
        }

        try {
            Customer customer = customerService.findCustomerByAccountNumber(accountNumber);
            if (customer == null) {
                setErrorMessage(request, "Customer not found");
                showCustomerList(request, response);
                return;
            }

            // Check permission
            User currentUser = getCurrentUser(request);
            if (!currentUser.hasPermission("CUSTOMER_MANAGEMENT")) {
                setErrorMessage(request, "You don't have permission to delete customers");
                showCustomerList(request, response);
                return;
            }

            // Set request attributes for confirmation page (optional)
            request.setAttribute("customer", customer);

            // For simplicity, directly delete (you could create a confirmation page)
            deleteCustomer(request, response);

        } catch (Exception e) {
            System.err.println("Error confirming delete: " + e.getMessage());
            setErrorMessage(request, "Error processing delete request");
            showCustomerList(request, response);
        }
    }

    /**
     * Create new customer - FIXED
     */
    private void createCustomer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check permission
        User currentUser = getCurrentUser(request);
        if (!currentUser.hasPermission("CUSTOMER_MANAGEMENT")) {
            setErrorMessage(request, "You don't have permission to create customers");
            showCustomerList(request, response);
            return;
        }

        try {
            // Get and validate form parameters
            String accountNumber = getSanitizedParameter(request, "accountNumber");
            String name = getSanitizedParameter(request, "name");
            String email = getSanitizedParameter(request, "email");
            String phoneNumber = getSanitizedParameter(request, "phoneNumber");
            String address = getSanitizedParameter(request, "address");
            String creditLimitStr = getSanitizedParameter(request, "creditLimit");
            String isActiveStr = getParameter(request, "isActive", "false");

            // Basic validation
            if (!ValidationUtils.isNotEmpty(accountNumber)) {
                setErrorMessage(request, "Account number is required");
                showCreateCustomerForm(request, response);
                return;
            }

            if (!ValidationUtils.isNotEmpty(name)) {
                setErrorMessage(request, "Customer name is required");
                showCreateCustomerForm(request, response);
                return;
            }

            // Check if account number already exists
            if (customerService.findCustomerByAccountNumber(accountNumber) != null) {
                setErrorMessage(request, "Account number already exists. Please use a different account number.");
                showCreateCustomerForm(request, response);
                return;
            }

            // Create customer object
            Customer customer = new Customer();
            customer.setAccountNumber(accountNumber);
            customer.setName(name);
            customer.setEmail(email);
            customer.setPhoneNumber(phoneNumber);
            customer.setAddress(address);
            customer.setActive("true".equals(isActiveStr));

            // Parse credit limit
            if (ValidationUtils.isNotEmpty(creditLimitStr)) {
                try {
                    BigDecimal creditLimit = new BigDecimal(creditLimitStr);
                    customer.setCreditLimit(creditLimit);
                } catch (NumberFormatException e) {
                    customer.setCreditLimit(BigDecimal.ZERO);
                }
            } else {
                customer.setCreditLimit(BigDecimal.ZERO);
            }

            // Save customer
            boolean success = customerService.createCustomer(customer);

            if (success) {
                setSuccessMessage(request, "Customer created successfully!");
                logAction(request, "CREATE_CUSTOMER", "Account: " + accountNumber + ", Name: " + name);

                // Redirect to customer list to prevent form resubmission
                response.sendRedirect(request.getContextPath() + "/customers");
            } else {
                setErrorMessage(request, "Failed to create customer. Please try again.");
                showCreateCustomerForm(request, response);
            }

        } catch (Exception e) {
            System.err.println("Error creating customer: " + e.getMessage());
            e.printStackTrace();
            setErrorMessage(request, "Error creating customer: " + e.getMessage());
            showCreateCustomerForm(request, response);
        }
    }

    /**
     * Update existing customer - FIXED
     */
    private void updateCustomer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String accountNumber = getSanitizedParameter(request, "accountNumber");

            // Debug logging
            System.out.println("Update customer - Account: " + accountNumber);

            // Find existing customer
            Customer existingCustomer = customerService.findCustomerByAccountNumber(accountNumber);
            if (existingCustomer == null) {
                setErrorMessage(request, "Customer not found");
                showCustomerList(request, response);
                return;
            }

            // Update customer details
            String name = getSanitizedParameter(request, "name");
            String email = getSanitizedParameter(request, "email");
            String phoneNumber = getSanitizedParameter(request, "phoneNumber");
            String address = getSanitizedParameter(request, "address");
            String creditLimitStr = getSanitizedParameter(request, "creditLimit");
            String isActiveStr = getParameter(request, "isActive", "false");

            // Debug logging
            System.out.println("Update params - Name: '" + name + "', Email: '" + email + "', Active: " + isActiveStr);

            // Validate name
            if (!ValidationUtils.isNotEmpty(name)) {
                System.out.println("Name validation failed - name is: '" + name + "'");
                setErrorMessage(request, "Customer name is required and must be valid");
                request.setAttribute("customer", existingCustomer);
                forwardToJSP(request, response, "customer/edit.jsp");
                return;
            }

            // Update fields
            existingCustomer.setName(name);
            existingCustomer.setEmail(email);
            existingCustomer.setPhoneNumber(phoneNumber);
            existingCustomer.setAddress(address);
            existingCustomer.setActive("true".equals(isActiveStr));

            // Parse credit limit
            if (ValidationUtils.isNotEmpty(creditLimitStr)) {
                try {
                    BigDecimal creditLimit = new BigDecimal(creditLimitStr);
                    existingCustomer.setCreditLimit(creditLimit);
                } catch (NumberFormatException e) {
                    existingCustomer.setCreditLimit(BigDecimal.ZERO);
                }
            } else {
                existingCustomer.setCreditLimit(BigDecimal.ZERO);
            }

            // Update customer
            boolean success = customerService.updateCustomer(existingCustomer);

            if (success) {
                setSuccessMessage(request, "Customer updated successfully!");
                logAction(request, "UPDATE_CUSTOMER", "Account: " + accountNumber);
                response.sendRedirect(request.getContextPath() + "/customers");
            } else {
                setErrorMessage(request, "Failed to update customer. Please try again.");
                request.setAttribute("customer", existingCustomer);
                forwardToJSP(request, response, "customer/edit.jsp");
            }

        } catch (Exception e) {
            System.err.println("Error updating customer: " + e.getMessage());
            e.printStackTrace();
            setErrorMessage(request, "Error updating customer: " + e.getMessage());
            showCustomerList(request, response);
        }
    }

    /**
     * Delete customer (deactivate) - FIXED
     */
    private void deleteCustomer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Try both parameter names
            String accountNumber = getSanitizedParameter(request, PARAM_ID);
            if (accountNumber == null || accountNumber.trim().isEmpty()) {
                accountNumber = getSanitizedParameter(request, "accountNumber");
            }

            if (!ValidationUtils.isValidAccountNumber(accountNumber)) {
                setErrorMessage(request, "Invalid customer account number");
                showCustomerList(request, response);
                return;
            }

            // Find existing customer
            Customer existingCustomer = customerService.findCustomerByAccountNumber(accountNumber);
            if (existingCustomer == null) {
                setErrorMessage(request, "Customer not found");
                showCustomerList(request, response);
                return;
            }

            // Check permission
            User currentUser = getCurrentUser(request);
            if (!currentUser.hasPermission("CUSTOMER_MANAGEMENT")) {
                setErrorMessage(request, "You don't have permission to delete customers");
                showCustomerList(request, response);
                return;
            }

            // Deactivate customer (soft delete)
            boolean success = customerService.deactivateCustomer(accountNumber);

            if (success) {
                setSuccessMessage(request, "Customer deactivated successfully!");
                logAction(request, "DELETE_CUSTOMER", "Account: " + accountNumber);
                response.sendRedirect(request.getContextPath() + "/customers");
            } else {
                setErrorMessage(request, "Failed to deactivate customer. Please try again.");
                showCustomerList(request, response);
            }

        } catch (Exception e) {
            System.err.println("Error deleting customer: " + e.getMessage());
            e.printStackTrace();
            setErrorMessage(request, "Error deleting customer: " + e.getMessage());
            showCustomerList(request, response);
        }
    }
}