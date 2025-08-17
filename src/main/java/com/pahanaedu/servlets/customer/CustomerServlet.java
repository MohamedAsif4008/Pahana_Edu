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
 * Servlet for handling Customer CRUD operations
 * Manages customer creation, reading, updating, and deletion
 *
 * Design Patterns Used:
 * - MVC Pattern: Controller for customer operations
 * - Command Pattern: Different actions based on request
 * - Service Layer Pattern: Business logic delegation
 * - RESTful Pattern: HTTP methods for operations
 *
 * @author Pahana Edu Development Team
 * @version 1.0
 */
@WebServlet(name = "CustomerServlet", urlPatterns = {"/customers", "/customer"})
public class CustomerServlet extends BaseServlet {

    private CustomerService customerService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.customerService = new CustomerServiceImpl();
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

            // Validate CSRF token
            if (!isValidCSRFToken(request)) {
                setErrorMessage(request, "Invalid request. Please try again.");
                showCustomerList(request, response);
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
                case "updatecredit":
                    updateCreditLimit(request, response);
                    break;
                default:
                    setErrorMessage(request, "Invalid action specified");
                    showCustomerList(request, response);
                    break;
            }

        } catch (Exception e) {
            handleException(request, response, e);
        }
    }

    /**
     * Show customer list with pagination and search
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
                // Get all customers with pagination
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
            request.setAttribute("pageSize", size);
            request.setAttribute("totalCustomers", totalCustomers);
            request.setAttribute("searchTerm", searchTerm);

            // Generate CSRF token
            request.setAttribute("csrfToken", generateCSRFToken(request));

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
     * Show customer details
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
            request.setAttribute("csrfToken", generateCSRFToken(request));

            // Log action
            logAction(request, "VIEW_CUSTOMER", "Account: " + accountNumber);

            // Forward to customer details JSP
            forwardToJSP(request, response, "customer/details.jsp");

        } catch (Exception e) {
            System.err.println("Error showing customer details: " + e.getMessage());
            setErrorMessage(request, "Error loading customer details");
            showCustomerList(request, response);
        }
    }

    /**
     * Show create customer form
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
        request.setAttribute("csrfToken", generateCSRFToken(request));

        // Forward to create customer form JSP
        forwardToJSP(request, response, "customer/create.jsp");
    }

    /**
     * Show edit customer form
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
            request.setAttribute("csrfToken", generateCSRFToken(request));

            // Forward to edit customer form JSP
            forwardToJSP(request, response, "customer/edit.jsp");

        } catch (Exception e) {
            System.err.println("Error showing edit form: " + e.getMessage());
            setErrorMessage(request, "Error loading customer for editing");
            showCustomerList(request, response);
        }
    }

    /**
     * Create new customer
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

        // Validate required parameters
        if (!validateRequiredParams(request, "accountNumber", "name")) {
            setErrorMessage(request, "Account number and name are required");
            showCreateCustomerForm(request, response);
            return;
        }

        try {
            // Get parameters
            String accountNumber = getSanitizedParameter(request, "accountNumber");
            String name = getSanitizedParameter(request, "name");
            String address = getSanitizedParameter(request, "address");
            String phoneNumber = getSanitizedParameter(request, "phoneNumber");
            String email = getSanitizedParameter(request, "email");
            String creditLimitStr = request.getParameter("creditLimit");

            // Create customer object
            Customer customer = new Customer(accountNumber, name, address, phoneNumber, email);

            // Set credit limit if provided
            if (ValidationUtils.isNotEmpty(creditLimitStr)) {
                try {
                    BigDecimal creditLimit = new BigDecimal(creditLimitStr);
                    customer.setCreditLimit(creditLimit);
                } catch (NumberFormatException e) {
                    setErrorMessage(request, "Invalid credit limit format");
                    showCreateCustomerForm(request, response);
                    return;
                }
            }

            // Create customer
            boolean created = customerService.createCustomer(customer);

            if (created) {
                setSuccessMessage(request, "Customer created successfully");
                logAction(request, "CREATE_CUSTOMER", "Account: " + accountNumber);

                // Redirect to customer list
                redirectTo(response, request.getContextPath() + "/customers");
            } else {
                setErrorMessage(request, "Failed to create customer. Please check the information and try again.");
                showCreateCustomerForm(request, response);
            }

        } catch (Exception e) {
            System.err.println("Error creating customer: " + e.getMessage());
            setErrorMessage(request, "Error creating customer: " + e.getMessage());
            showCreateCustomerForm(request, response);
        }
    }

    /**
     * Update existing customer
     */
    private void updateCustomer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check permission
        User currentUser = getCurrentUser(request);
        if (!currentUser.hasPermission("CUSTOMER_MANAGEMENT")) {
            setErrorMessage(request, "You don't have permission to update customers");
            showCustomerList(request, response);
            return;
        }

        // Validate required parameters
        if (!validateRequiredParams(request, "accountNumber", "name")) {
            setErrorMessage(request, "Account number and name are required");
            showCustomerList(request, response);
            return;
        }

        try {
            // Get parameters
            String accountNumber = getSanitizedParameter(request, "accountNumber");
            String name = getSanitizedParameter(request, "name");
            String address = getSanitizedParameter(request, "address");
            String phoneNumber = getSanitizedParameter(request, "phoneNumber");
            String email = getSanitizedParameter(request, "email");
            String creditLimitStr = request.getParameter("creditLimit");
            boolean isActive = "on".equals(request.getParameter("isActive"));

            // Find existing customer
            Customer customer = customerService.findCustomerByAccountNumber(accountNumber);
            if (customer == null) {
                setErrorMessage(request, "Customer not found");
                showCustomerList(request, response);
                return;
            }

            // Update customer properties
            customer.setName(name);
            customer.setAddress(address);
            customer.setPhoneNumber(phoneNumber);
            customer.setEmail(email);
            customer.setActive(isActive);

            // Update credit limit if provided
            if (ValidationUtils.isNotEmpty(creditLimitStr)) {
                try {
                    BigDecimal creditLimit = new BigDecimal(creditLimitStr);
                    customer.setCreditLimit(creditLimit);
                } catch (NumberFormatException e) {
                    setErrorMessage(request, "Invalid credit limit format");
                    showEditCustomerForm(request, response);
                    return;
                }
            }

            // Update customer
            boolean updated = customerService.updateCustomer(customer);

            if (updated) {
                setSuccessMessage(request, "Customer updated successfully");
                logAction(request, "UPDATE_CUSTOMER", "Account: " + accountNumber);

                // Redirect to customer details
                redirectTo(response, request.getContextPath() + "/customers?action=view&id=" + accountNumber);
            } else {
                setErrorMessage(request, "Failed to update customer");
                showEditCustomerForm(request, response);
            }

        } catch (Exception e) {
            System.err.println("Error updating customer: " + e.getMessage());
            setErrorMessage(request, "Error updating customer: " + e.getMessage());
            showCustomerList(request, response);
        }
    }

    /**
     * Confirm customer deletion
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

            // Set request attributes
            request.setAttribute("customer", customer);
            request.setAttribute("csrfToken", generateCSRFToken(request));

            // Forward to delete confirmation JSP
            forwardToJSP(request, response, "customer/delete.jsp");

        } catch (Exception e) {
            System.err.println("Error showing delete confirmation: " + e.getMessage());
            setErrorMessage(request, "Error processing delete request");
            showCustomerList(request, response);
        }
    }

    /**
     * Delete (deactivate) customer
     */
    private void deleteCustomer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check permission
        User currentUser = getCurrentUser(request);
        if (!currentUser.hasPermission("CUSTOMER_MANAGEMENT")) {
            setErrorMessage(request, "You don't have permission to delete customers");
            showCustomerList(request, response);
            return;
        }

        String accountNumber = getSanitizedParameter(request, "accountNumber");
        if (!ValidationUtils.isValidAccountNumber(accountNumber)) {
            setErrorMessage(request, "Invalid customer account number");
            showCustomerList(request, response);
            return;
        }

        try {
            boolean deleted = customerService.deactivateCustomer(accountNumber);

            if (deleted) {
                setSuccessMessage(request, "Customer deactivated successfully");
                logAction(request, "DELETE_CUSTOMER", "Account: " + accountNumber);
            } else {
                setErrorMessage(request, "Failed to deactivate customer");
            }

            // Redirect to customer list
            redirectTo(response, request.getContextPath() + "/customers");

        } catch (Exception e) {
            System.err.println("Error deleting customer: " + e.getMessage());
            setErrorMessage(request, "Error deleting customer: " + e.getMessage());
            showCustomerList(request, response);
        }
    }

    /**
     * Update customer credit limit
     */
    private void updateCreditLimit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check permission (admin only for credit limit changes)
        if (!isAdmin(request)) {
            setErrorMessage(request, "Only administrators can update credit limits");
            showCustomerList(request, response);
            return;
        }

        // Validate required parameters
        if (!validateRequiredParams(request, "accountNumber", "creditLimit")) {
            setErrorMessage(request, "Account number and credit limit are required");
            showCustomerList(request, response);
            return;
        }

        try {
            String accountNumber = getSanitizedParameter(request, "accountNumber");
            String creditLimitStr = request.getParameter("creditLimit");

            BigDecimal newCreditLimit;
            try {
                newCreditLimit = new BigDecimal(creditLimitStr);
            } catch (NumberFormatException e) {
                setErrorMessage(request, "Invalid credit limit format");
                showCustomerList(request, response);
                return;
            }

            boolean updated = customerService.updateCreditLimit(accountNumber, newCreditLimit);

            if (updated) {
                setSuccessMessage(request, "Credit limit updated successfully");
                logAction(request, "UPDATE_CREDIT_LIMIT",
                        "Account: " + accountNumber + ", New Limit: " + newCreditLimit);
            } else {
                setErrorMessage(request, "Failed to update credit limit");
            }

            // Redirect to customer details
            redirectTo(response, request.getContextPath() + "/customers?action=view&id=" + accountNumber);

        } catch (Exception e) {
            System.err.println("Error updating credit limit: " + e.getMessage());
            setErrorMessage(request, "Error updating credit limit: " + e.getMessage());
            showCustomerList(request, response);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        this.customerService = null;
    }
}