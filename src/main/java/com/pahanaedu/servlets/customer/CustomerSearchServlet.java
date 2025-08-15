package com.pahanaedu.servlets.customer;

import com.pahanaedu.models.Customer;
import com.pahanaedu.service.interfaces.CustomerService;
import com.pahanaedu.service.impl.CustomerServiceImpl;
import com.pahanaedu.servlets.common.BaseServlet;
import com.pahanaedu.util.ValidationUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;

/**
 * Servlet for handling Customer search operations
 * Provides advanced search functionality for customers
 *
 * Design Patterns Used:
 * - Strategy Pattern: Different search strategies
 * - Builder Pattern: Search criteria building
 * - Command Pattern: Search command execution
 *
 * @author Pahana Edu Development Team
 * @version 1.0
 */
@WebServlet(name = "CustomerSearchServlet", urlPatterns = {"/customers/search", "/customer/search"})
public class CustomerSearchServlet extends BaseServlet {

    private CustomerService customerService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.customerService = new CustomerServiceImpl();
    }

    /**
     * Handle GET requests - Show search form and results
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
            String action = getParameter(request, PARAM_ACTION, "form");

            switch (action.toLowerCase()) {
                case "form":
                    showSearchForm(request, response);
                    break;
                case "search":
                    performSearch(request, response);
                    break;
                case "quick":
                    performQuickSearch(request, response);
                    break;
                case "ajax":
                    performAjaxSearch(request, response);
                    break;
                case "stats":
                    getSearchStats(request, response);
                    break;
                default:
                    showSearchForm(request, response);
                    break;
            }

        } catch (Exception e) {
            handleException(request, response, e);
        }
    }

    /**
     * Handle POST requests - Process search requests
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Check authentication
            if (!isUserLoggedIn(request)) {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
                return;
            }

            // Get action parameter
            String action = getParameter(request, PARAM_ACTION, "search");

            switch (action.toLowerCase()) {
                case "search":
                    performAdvancedSearch(request, response);
                    break;
                case "export":
                    exportSearchResults(request, response);
                    break;
                default:
                    performSearch(request, response);
                    break;
            }

        } catch (Exception e) {
            handleException(request, response, e);
        }
    }

    /**
     * Show search form
     */
    private void showSearchForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set request attributes
        request.setAttribute("csrfToken", generateCSRFToken(request));

        // Get recent searches from session (if any)
        List<String> recentSearches = getRecentSearches(request);
        request.setAttribute("recentSearches", recentSearches);

        // Forward to search form JSP
        forwardToJSP(request, response, "customer/customer-search.jsp");
    }

    /**
     * Perform basic search
     */
    private void performSearch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get search parameters
        String searchTerm = getSanitizedParameter(request, "q");
        String searchType = getParameter(request, "type", "name");

        if (!ValidationUtils.isNotEmpty(searchTerm)) {
            setErrorMessage(request, "Please enter a search term");
            showSearchForm(request, response);
            return;
        }

        try {
            List<Customer> customers = null;

            switch (searchType.toLowerCase()) {
                case "name":
                    customers = customerService.searchCustomersByName(searchTerm);
                    break;
                case "account":
                    Customer customer = customerService.findCustomerByAccountNumber(searchTerm);
                    customers = customer != null ? List.of(customer) : List.of();
                    break;
                case "phone":
                    customers = searchByPhone(searchTerm);
                    break;
                case "email":
                    customers = searchByEmail(searchTerm);
                    break;
                default:
                    customers = customerService.searchCustomersByName(searchTerm);
                    break;
            }

            // Add to recent searches
            addToRecentSearches(request, searchTerm);

            // Set request attributes
            request.setAttribute("customers", customers);
            request.setAttribute("searchTerm", searchTerm);
            request.setAttribute("searchType", searchType);
            request.setAttribute("resultCount", customers.size());
            request.setAttribute("csrfToken", generateCSRFToken(request));

            // Log search action
            logAction(request, "CUSTOMER_SEARCH",
                    "Type: " + searchType + ", Term: " + searchTerm + ", Results: " + customers.size());

            // Forward to search results JSP
            forwardToJSP(request, response, "customer/customer-search-results.jsp");

        } catch (Exception e) {
            System.err.println("Error performing search: " + e.getMessage());
            setErrorMessage(request, "Error performing search");
            showSearchForm(request, response);
        }
    }

    /**
     * Perform quick search (for autocomplete)
     */
    private void performQuickSearch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String searchTerm = getSanitizedParameter(request, "q");
        int limit = getIntParameter(request, "limit", 10);

        if (!ValidationUtils.isNotEmpty(searchTerm) || searchTerm.length() < 2) {
            sendJsonResponse(response, "[]");
            return;
        }

        try {
            List<Customer> customers = customerService.searchCustomersByName(searchTerm);

            // Limit results for quick search
            if (customers.size() > limit) {
                customers = customers.subList(0, limit);
            }

            // Build JSON response
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < customers.size(); i++) {
                Customer customer = customers.get(i);
                if (i > 0) json.append(",");
                json.append("{")
                        .append("\"accountNumber\":\"").append(escapeJson(customer.getAccountNumber())).append("\",")
                        .append("\"name\":\"").append(escapeJson(customer.getName())).append("\",")
                        .append("\"phone\":\"").append(escapeJson(customer.getPhoneNumber())).append("\",")
                        .append("\"email\":\"").append(escapeJson(customer.getEmail())).append("\"")
                        .append("}");
            }
            json.append("]");

            sendJsonResponse(response, json.toString());

        } catch (Exception e) {
            System.err.println("Error in quick search: " + e.getMessage());
            sendJsonResponse(response, "[]");
        }
    }

    /**
     * Perform AJAX search
     */
    private void performAjaxSearch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String searchTerm = getSanitizedParameter(request, "q");
        String searchType = getParameter(request, "type", "name");

        if (!ValidationUtils.isNotEmpty(searchTerm)) {
            sendJsonResponse(response, "{\"success\": false, \"message\": \"Search term required\"}");
            return;
        }

        try {
            List<Customer> customers = null;

            switch (searchType.toLowerCase()) {
                case "name":
                    customers = customerService.searchCustomersByName(searchTerm);
                    break;
                case "account":
                    Customer customer = customerService.findCustomerByAccountNumber(searchTerm);
                    customers = customer != null ? List.of(customer) : List.of();
                    break;
                default:
                    customers = customerService.searchCustomersByName(searchTerm);
                    break;
            }

            // Build JSON response
            StringBuilder json = new StringBuilder();
            json.append("{\"success\": true, \"count\": ").append(customers.size()).append(", \"customers\": [");

            for (int i = 0; i < customers.size(); i++) {
                Customer customer = customers.get(i);
                if (i > 0) json.append(",");
                json.append("{")
                        .append("\"accountNumber\":\"").append(escapeJson(customer.getAccountNumber())).append("\",")
                        .append("\"name\":\"").append(escapeJson(customer.getName())).append("\",")
                        .append("\"address\":\"").append(escapeJson(customer.getAddress())).append("\",")
                        .append("\"phone\":\"").append(escapeJson(customer.getPhoneNumber())).append("\",")
                        .append("\"email\":\"").append(escapeJson(customer.getEmail())).append("\",")
                        .append("\"isActive\":").append(customer.isActive())
                        .append("}");
            }

            json.append("]}");

            sendJsonResponse(response, json.toString());

        } catch (Exception e) {
            System.err.println("Error in AJAX search: " + e.getMessage());
            sendJsonResponse(response, "{\"success\": false, \"message\": \"Search failed\"}");
        }
    }

    /**
     * Perform advanced search with multiple criteria
     */
    private void performAdvancedSearch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Validate CSRF token
        if (!isValidCSRFToken(request)) {
            setErrorMessage(request, "Invalid request");
            showSearchForm(request, response);
            return;
        }

        try {
            // Get search criteria
            String name = getSanitizedParameter(request, "name");
            String accountNumber = getSanitizedParameter(request, "accountNumber");
            String phone = getSanitizedParameter(request, "phone");
            String email = getSanitizedParameter(request, "email");
            String activeStatus = getParameter(request, "activeStatus", "all");

            // Start with all customers
            List<Customer> customers = customerService.getAllActiveCustomers();

            // Apply filters
            if (ValidationUtils.isNotEmpty(name)) {
                customers = customers.stream()
                        .filter(c -> c.getName().toLowerCase().contains(name.toLowerCase()))
                        .toList();
            }

            if (ValidationUtils.isNotEmpty(accountNumber)) {
                customers = customers.stream()
                        .filter(c -> c.getAccountNumber().toLowerCase().contains(accountNumber.toLowerCase()))
                        .toList();
            }

            if (ValidationUtils.isNotEmpty(phone)) {
                customers = customers.stream()
                        .filter(c -> c.getPhoneNumber() != null &&
                                c.getPhoneNumber().contains(phone))
                        .toList();
            }

            if (ValidationUtils.isNotEmpty(email)) {
                customers = customers.stream()
                        .filter(c -> c.getEmail() != null &&
                                c.getEmail().toLowerCase().contains(email.toLowerCase()))
                        .toList();
            }

            // Filter by active status
            if (!"all".equals(activeStatus)) {
                boolean isActive = "active".equals(activeStatus);
                customers = customers.stream()
                        .filter(c -> c.isActive() == isActive)
                        .toList();
            }

            // Set search criteria for form persistence
            request.setAttribute("searchName", name);
            request.setAttribute("searchAccountNumber", accountNumber);
            request.setAttribute("searchPhone", phone);
            request.setAttribute("searchEmail", email);
            request.setAttribute("searchActiveStatus", activeStatus);

            // Set results
            request.setAttribute("customers", customers);
            request.setAttribute("resultCount", customers.size());
            request.setAttribute("isAdvancedSearch", true);
            request.setAttribute("csrfToken", generateCSRFToken(request));

            // Log advanced search
            logAction(request, "CUSTOMER_ADVANCED_SEARCH",
                    "Criteria: name=" + name + ", account=" + accountNumber + ", Results: " + customers.size());

            // Forward to search results JSP
            forwardToJSP(request, response, "customer/customer-search-results.jsp");

        } catch (Exception e) {
            System.err.println("Error in advanced search: " + e.getMessage());
            setErrorMessage(request, "Error performing advanced search");
            showSearchForm(request, response);
        }
    }

    /**
     * Export search results
     */
    private void exportSearchResults(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check admin permission for export
        if (!isAdmin(request)) {
            setErrorMessage(request, "Only administrators can export customer data");
            showSearchForm(request, response);
            return;
        }

        // Validate CSRF token
        if (!isValidCSRFToken(request)) {
            sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Invalid request");
            return;
        }

        try {
            // Get export format
            String format = getParameter(request, "format", "csv");
            String searchTerm = getSanitizedParameter(request, "searchTerm");

            List<Customer> customers;
            if (ValidationUtils.isNotEmpty(searchTerm)) {
                customers = customerService.searchCustomersByName(searchTerm);
            } else {
                customers = customerService.getAllActiveCustomers();
            }

            if ("csv".equals(format)) {
                exportToCSV(response, customers);
            } else if ("json".equals(format)) {
                exportToJSON(response, customers);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Unsupported export format");
                return;
            }

            // Log export action
            logAction(request, "EXPORT_CUSTOMERS",
                    "Format: " + format + ", Count: " + customers.size());

        } catch (Exception e) {
            System.err.println("Error exporting customers: " + e.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Export failed");
        }
    }

    /**
     * Search customers by phone number
     */
    private List<Customer> searchByPhone(String phone) {
        try {
            return customerService.getAllActiveCustomers().stream()
                    .filter(c -> c.getPhoneNumber() != null &&
                            c.getPhoneNumber().contains(phone))
                    .toList();
        } catch (Exception e) {
            System.err.println("Error searching by phone: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Search customers by email
     */
    private List<Customer> searchByEmail(String email) {
        try {
            return customerService.getAllActiveCustomers().stream()
                    .filter(c -> c.getEmail() != null &&
                            c.getEmail().toLowerCase().contains(email.toLowerCase()))
                    .toList();
        } catch (Exception e) {
            System.err.println("Error searching by email: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Get recent searches from session
     */
    @SuppressWarnings("unchecked")
    private List<String> getRecentSearches(HttpServletRequest request) {
        try {
            List<String> searches = (List<String>) request.getSession().getAttribute("recentCustomerSearches");
            return searches != null ? searches : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Add search term to recent searches
     */
    @SuppressWarnings("unchecked")
    private void addToRecentSearches(HttpServletRequest request, String searchTerm) {
        try {
            List<String> recentSearches = (List<String>) request.getSession().getAttribute("recentCustomerSearches");
            if (recentSearches == null) {
                recentSearches = new ArrayList<>();
            }

            // Remove if already exists
            recentSearches.remove(searchTerm);

            // Add to beginning
            recentSearches.add(0, searchTerm);

            // Keep only last 10 searches
            if (recentSearches.size() > 10) {
                recentSearches = recentSearches.subList(0, 10);
            }

            request.getSession().setAttribute("recentCustomerSearches", recentSearches);

        } catch (Exception e) {
            System.err.println("Error adding to recent searches: " + e.getMessage());
        }
    }

    /**
     * Export customers to CSV format
     */
    private void exportToCSV(HttpServletResponse response, List<Customer> customers) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"customers.csv\"");

        try (PrintWriter writer = response.getWriter()) {
            // Write CSV header
            writer.println("Account Number,Name,Address,Phone,Email,Credit Limit,Active,Registration Date");

            // Write customer data
            for (Customer customer : customers) {
                writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                        escapeCSV(customer.getAccountNumber()),
                        escapeCSV(customer.getName()),
                        escapeCSV(customer.getAddress()),
                        escapeCSV(customer.getPhoneNumber()),
                        escapeCSV(customer.getEmail()),
                        customer.getCreditLimit() != null ? customer.getCreditLimit().toString() : "0.00",
                        customer.isActive() ? "Yes" : "No",
                        customer.getRegistrationDate() != null ? customer.getRegistrationDate().toString() : ""
                );
            }
        }
    }

    /**
     * Export customers to JSON format
     */
    private void exportToJSON(HttpServletResponse response, List<Customer> customers) throws IOException {
        response.setContentType("application/json");
        response.setHeader("Content-Disposition", "attachment; filename=\"customers.json\"");

        StringBuilder json = new StringBuilder();
        json.append("{\n  \"customers\": [\n");

        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            if (i > 0) json.append(",\n");

            json.append("    {\n")
                    .append("      \"accountNumber\": \"").append(escapeJson(customer.getAccountNumber())).append("\",\n")
                    .append("      \"name\": \"").append(escapeJson(customer.getName())).append("\",\n")
                    .append("      \"address\": \"").append(escapeJson(customer.getAddress())).append("\",\n")
                    .append("      \"phone\": \"").append(escapeJson(customer.getPhoneNumber())).append("\",\n")
                    .append("      \"email\": \"").append(escapeJson(customer.getEmail())).append("\",\n")
                    .append("      \"creditLimit\": ").append(customer.getCreditLimit() != null ? customer.getCreditLimit() : 0).append(",\n")
                    .append("      \"isActive\": ").append(customer.isActive()).append(",\n")
                    .append("      \"registrationDate\": \"").append(customer.getRegistrationDate() != null ? customer.getRegistrationDate() : "").append("\"\n")
                    .append("    }");
        }

        json.append("\n  ],\n")
                .append("  \"count\": ").append(customers.size()).append(",\n")
                .append("  \"exportDate\": \"").append(new java.util.Date()).append("\"\n")
                .append("}");

        try (PrintWriter writer = response.getWriter()) {
            writer.print(json.toString());
        }
    }

    /**
     * Get search statistics
     */
    private void getSearchStats(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request)) {
            sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Admin access required");
            return;
        }

        try {
            int totalCustomers = customerService.getActiveCustomerCount();
            List<Customer> customersWithCredit = customerService.getCustomersWithCreditLimits();

            // Build stats JSON
            String statsJson = String.format(
                    "{\"totalCustomers\": %d, \"customersWithCredit\": %d, \"activeCustomers\": %d}",
                    totalCustomers, customersWithCredit.size(), totalCustomers
            );

            sendJsonResponse(response, statsJson);

        } catch (Exception e) {
            System.err.println("Error getting search stats: " + e.getMessage());
            sendJsonResponse(response, "{\"error\": \"Failed to get statistics\"}");
        }
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

    /**
     * Escape string for CSV
     */
    private String escapeCSV(String value) {
        if (value == null) return "";
        return value.replace("\"", "\"\"");
    }

    @Override
    public void destroy() {
        super.destroy();
        this.customerService = null;
    }
}