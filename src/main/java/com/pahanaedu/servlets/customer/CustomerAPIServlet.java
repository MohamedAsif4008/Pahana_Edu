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
import java.io.BufferedReader;
import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Pattern;

/**
 * REST API Servlet for Customer operations
 * Provides JSON-based API for customer management
 *
 * Design Patterns Used:
 * - REST Pattern: RESTful API design
 * - Command Pattern: HTTP method-based commands
 * - Factory Pattern: Response object creation
 * - Strategy Pattern: Different response formats
 *
 * @author Pahana Edu Development Team
 * @version 1.0
 */
@WebServlet(name = "CustomerAPIServlet", urlPatterns = {"/api/customers", "/api/customers/*"})
public class CustomerAPIServlet extends BaseServlet {

    private CustomerService customerService;
    private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile("/api/customers/([A-Z]{3}\\d{3,6})");

    @Override
    public void init() throws ServletException {
        super.init();
        this.customerService = new CustomerServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Check authentication
            if (!isUserLoggedIn(request)) {
                sendApiError(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
                return;
            }

            // Parse request path
            String pathInfo = request.getPathInfo();
            String accountNumber = extractAccountNumber(pathInfo);

            if (accountNumber != null) {
                // Get specific customer
                getCustomer(request, response, accountNumber);
            } else {
                // Get customers with optional search/filtering
                getCustomers(request, response);
            }

        } catch (Exception e) {
            System.err.println("API Error in GET: " + e.getMessage());
            sendApiError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Check authentication and permission
            if (!isUserLoggedIn(request)) {
                sendApiError(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
                return;
            }

            User currentUser = getCurrentUser(request);
            if (!currentUser.hasPermission("CUSTOMER_MANAGEMENT")) {
                sendApiError(response, HttpServletResponse.SC_FORBIDDEN, "Permission denied");
                return;
            }

            // Parse JSON request body
            String jsonBody = readRequestBody(request);
            Customer customer = parseCustomerFromJson(jsonBody);

            if (customer == null) {
                sendApiError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid customer data");
                return;
            }

            // Create customer
            boolean created = customerService.createCustomer(customer);

            if (created) {
                // Return created customer
                Customer createdCustomer = customerService.findCustomerByAccountNumber(customer.getAccountNumber());
                sendApiSuccess(response, HttpServletResponse.SC_CREATED, "Customer created successfully",
                        customerToJson(createdCustomer));

                logAction(request, "API_CREATE_CUSTOMER", "Account: " + customer.getAccountNumber());
            } else {
                sendApiError(response, HttpServletResponse.SC_BAD_REQUEST, "Failed to create customer");
            }

        } catch (Exception e) {
            System.err.println("API Error in POST: " + e.getMessage());
            sendApiError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Check authentication and permission
            if (!isUserLoggedIn(request)) {
                sendApiError(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
                return;
            }

            User currentUser = getCurrentUser(request);
            if (!currentUser.hasPermission("CUSTOMER_MANAGEMENT")) {
                sendApiError(response, HttpServletResponse.SC_FORBIDDEN, "Permission denied");
                return;
            }

            // Extract account number from path
            String pathInfo = request.getPathInfo();
            String accountNumber = extractAccountNumber(pathInfo);

            if (accountNumber == null) {
                sendApiError(response, HttpServletResponse.SC_BAD_REQUEST, "Account number required");
                return;
            }

            // Check if customer exists
            Customer existingCustomer = customerService.findCustomerByAccountNumber(accountNumber);
            if (existingCustomer == null) {
                sendApiError(response, HttpServletResponse.SC_NOT_FOUND, "Customer not found");
                return;
            }

            // Parse JSON request body
            String jsonBody = readRequestBody(request);
            Customer updateData = parseCustomerFromJson(jsonBody);

            if (updateData == null) {
                sendApiError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid customer data");
                return;
            }

            // Update customer properties
            existingCustomer.setName(updateData.getName());
            existingCustomer.setAddress(updateData.getAddress());
            existingCustomer.setPhoneNumber(updateData.getPhoneNumber());
            existingCustomer.setEmail(updateData.getEmail());

            if (updateData.getCreditLimit() != null) {
                existingCustomer.setCreditLimit(updateData.getCreditLimit());
            }

            // Update customer
            boolean updated = customerService.updateCustomer(existingCustomer);

            if (updated) {
                // Return updated customer
                Customer updatedCustomer = customerService.findCustomerByAccountNumber(accountNumber);
                sendApiSuccess(response, HttpServletResponse.SC_OK, "Customer updated successfully",
                        customerToJson(updatedCustomer));

                logAction(request, "API_UPDATE_CUSTOMER", "Account: " + accountNumber);
            } else {
                sendApiError(response, HttpServletResponse.SC_BAD_REQUEST, "Failed to update customer");
            }

        } catch (Exception e) {
            System.err.println("API Error in PUT: " + e.getMessage());
            sendApiError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }


    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Check authentication and permission
            if (!isUserLoggedIn(request)) {
                sendApiError(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
                return;
            }

            User currentUser = getCurrentUser(request);
            if (!currentUser.hasPermission("CUSTOMER_MANAGEMENT")) {
                sendApiError(response, HttpServletResponse.SC_FORBIDDEN, "Permission denied");
                return;
            }

            // Extract account number from path
            String pathInfo = request.getPathInfo();
            String accountNumber = extractAccountNumber(pathInfo);

            if (accountNumber == null) {
                sendApiError(response, HttpServletResponse.SC_BAD_REQUEST, "Account number required");
                return;
            }

            // Check if customer exists
            Customer customer = customerService.findCustomerByAccountNumber(accountNumber);
            if (customer == null) {
                sendApiError(response, HttpServletResponse.SC_NOT_FOUND, "Customer not found");
                return;
            }

            // Deactivate customer
            boolean deleted = customerService.deactivateCustomer(accountNumber);

            if (deleted) {
                sendApiSuccess(response, HttpServletResponse.SC_OK, "Customer deactivated successfully", null);
                logAction(request, "API_DELETE_CUSTOMER", "Account: " + accountNumber);
            } else {
                sendApiError(response, HttpServletResponse.SC_BAD_REQUEST, "Failed to deactivate customer");
            }

        } catch (Exception e) {
            System.err.println("API Error in DELETE: " + e.getMessage());
            sendApiError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    /**
     * Get specific customer
     */
    private void getCustomer(HttpServletRequest request, HttpServletResponse response, String accountNumber)
            throws IOException {

        try {
            Customer customer = customerService.findCustomerByAccountNumber(accountNumber);

            if (customer != null) {
                sendApiSuccess(response, HttpServletResponse.SC_OK, "Customer found", customerToJson(customer));
                logAction(request, "API_GET_CUSTOMER", "Account: " + accountNumber);
            } else {
                sendApiError(response, HttpServletResponse.SC_NOT_FOUND, "Customer not found");
            }

        } catch (Exception e) {
            System.err.println("Error getting customer: " + e.getMessage());
            sendApiError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving customer");
        }
    }

    /**
     * Get customers with optional search/filtering
     */
    private void getCustomers(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            // Get query parameters
            String search = getSanitizedParameter(request, "search");
            String active = getParameter(request, "active", "true");
            int limit = getIntParameter(request, "limit", 50);
            int offset = getIntParameter(request, "offset", 0);

            List<Customer> customers;

            if (ValidationUtils.isNotEmpty(search)) {
                // Search customers
                customers = customerService.searchCustomersByName(search);
            } else {
                // Get all customers
                if ("true".equals(active)) {
                    customers = customerService.getAllActiveCustomers();
                } else {
                    customers = customerService.getAllActiveCustomers(); // For now, only active customers
                }
            }

            // Apply pagination
            int totalCount = customers.size();
            int endIndex = Math.min(offset + limit, customers.size());
            if (offset < customers.size()) {
                customers = customers.subList(offset, endIndex);
            } else {
                customers = List.of();
            }

            // Build JSON response
            String customersJson = customersToJson(customers, totalCount, offset, limit);
            sendApiSuccess(response, HttpServletResponse.SC_OK, "Customers retrieved successfully", customersJson);

            logAction(request, "API_GET_CUSTOMERS",
                    "Search: " + search + ", Count: " + customers.size());

        } catch (Exception e) {
            System.err.println("Error getting customers: " + e.getMessage());
            sendApiError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving customers");
        }
    }

    /**
     * Extract account number from request path
     */
    private String extractAccountNumber(String pathInfo) {
        if (pathInfo == null || pathInfo.length() <= 1) {
            return null;
        }

        // Remove leading slash and extract account number
        String path = pathInfo.substring(1);
        if (ValidationUtils.isValidAccountNumber(path)) {
            return path;
        }

        return null;
    }

    /**
     * Read JSON request body
     */
    private String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
        }
        return body.toString();
    }

    /**
     * Parse Customer object from JSON (simple implementation)
     */
    private Customer parseCustomerFromJson(String json) {
        try {
            // Simple JSON parsing (in production, use a proper JSON library)
            if (!ValidationUtils.isNotEmpty(json)) {
                return null;
            }

            Customer customer = new Customer();

            // Extract fields using simple string operations
            String accountNumber = extractJsonValue(json, "accountNumber");
            String name = extractJsonValue(json, "name");
            String address = extractJsonValue(json, "address");
            String phone = extractJsonValue(json, "phone");
            String email = extractJsonValue(json, "email");
            String creditLimitStr = extractJsonValue(json, "creditLimit");

            if (!ValidationUtils.isNotEmpty(name)) {
                return null; // Name is required
            }

            customer.setAccountNumber(accountNumber);
            customer.setName(name);
            customer.setAddress(address);
            customer.setPhoneNumber(phone);
            customer.setEmail(email);

            // Parse credit limit
            if (ValidationUtils.isNotEmpty(creditLimitStr)) {
                try {
                    customer.setCreditLimit(new BigDecimal(creditLimitStr));
                } catch (NumberFormatException e) {
                    // Invalid credit limit, set to zero
                    customer.setCreditLimit(BigDecimal.ZERO);
                }
            }

            return customer;

        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            return null;
        }
    }

    /**
     * Simple JSON value extraction
     */
    private String extractJsonValue(String json, String key) {
        try {
            String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]*)\"|\"" + key + "\"\\s*:\\s*([^,}\\s]+)";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(json);

            if (m.find()) {
                return m.group(1) != null ? m.group(1) : m.group(2);
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Convert Customer object to JSON
     */
    private String customerToJson(Customer customer) {
        if (customer == null) {
            return "null";
        }

        return String.format(
                "{\"accountNumber\":\"%s\",\"name\":\"%s\",\"address\":\"%s\",\"phone\":\"%s\",\"email\":\"%s\",\"creditLimit\":%s,\"isActive\":%s,\"registrationDate\":\"%s\"}",
                escapeJson(customer.getAccountNumber()),
                escapeJson(customer.getName()),
                escapeJson(customer.getAddress()),
                escapeJson(customer.getPhoneNumber()),
                escapeJson(customer.getEmail()),
                customer.getCreditLimit() != null ? customer.getCreditLimit() : 0,
                customer.isActive(),
                customer.getRegistrationDate() != null ? customer.getRegistrationDate() : ""
        );
    }

    /**
     * Convert list of customers to JSON with pagination info
     */
    private String customersToJson(List<Customer> customers, int totalCount, int offset, int limit) {
        StringBuilder json = new StringBuilder();
        json.append("{\"customers\":[");

        for (int i = 0; i < customers.size(); i++) {
            if (i > 0) json.append(",");
            json.append(customerToJson(customers.get(i)));
        }

        json.append("],\"pagination\":{")
                .append("\"totalCount\":").append(totalCount).append(",")
                .append("\"offset\":").append(offset).append(",")
                .append("\"limit\":").append(limit).append(",")
                .append("\"count\":").append(customers.size())
                .append("}}");

        return json.toString();
    }

    /**
     * Send API success response
     */
    private void sendApiSuccess(HttpServletResponse response, int statusCode, String message, String data)
            throws IOException {

        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        StringBuilder json = new StringBuilder();
        json.append("{\"success\":true,\"message\":\"").append(escapeJson(message)).append("\"");

        if (data != null) {
            json.append(",\"data\":").append(data);
        }

        json.append(",\"timestamp\":\"").append(new java.util.Date()).append("\"}");

        response.getWriter().write(json.toString());
    }

    /**
     * Send API error response
     */
    private void sendApiError(HttpServletResponse response, int statusCode, String message)
            throws IOException {

        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String json = String.format(
                "{\"success\":false,\"error\":\"%s\",\"code\":%d,\"timestamp\":\"%s\"}",
                escapeJson(message), statusCode, new java.util.Date()
        );

        response.getWriter().write(json);
    }

    /**
     * Escape string for JSON
     */
    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    @Override
    public void destroy() {
        super.destroy();
        this.customerService = null;
    }
}