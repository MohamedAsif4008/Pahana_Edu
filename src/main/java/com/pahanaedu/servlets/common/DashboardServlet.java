package com.pahanaedu.servlets.common;

import com.pahanaedu.models.User;
import com.pahanaedu.servlets.common.BaseServlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet for handling dashboard operations
 * Shows main dashboard with statistics and overview
 *
 * Design Patterns Used:
 * - MVC Pattern: Controller for dashboard
 * - Command Pattern: Dashboard actions
 *
 * @author Pahana Edu Development Team
 * @version 1.0
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard", "/home"})
public class DashboardServlet extends BaseServlet {

    /**
     * Handle GET requests - Show dashboard
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

            // Get current user
            User currentUser = getCurrentUser(request);
            if (currentUser == null) {
                redirectTo(response, request.getContextPath() + "/login");
                return;
            }

            // Set basic dashboard data
            loadDashboardData(request);

            // Forward to dashboard JSP
            forwardToJSP(request, response, "common/dashboard.jsp");

        } catch (Exception e) {
            System.err.println("Dashboard error: " + e.getMessage());
            setErrorMessage(request, "Error loading dashboard. Please try again.");
            forwardToJSP(request, response, "common/dashboard.jsp");
        }
    }

    /**
     * Load dashboard statistics and data
     */
    private void loadDashboardData(HttpServletRequest request) {
        try {
            // TODO: Replace with actual database queries
            // For now, set some dummy data for testing

            request.setAttribute("totalCustomers", 5);  // Based on your test data
            request.setAttribute("totalItems", 8);      // Based on your test data
            request.setAttribute("totalBills", 0);      // No bills yet
            request.setAttribute("lowStockCount", 2);   // Dummy data

            // Recent activity (dummy data)
            request.setAttribute("recentCustomers", "John Doe, Jane Smith");
            request.setAttribute("recentBills", "No recent bills");

            System.out.println("Dashboard data loaded successfully");

        } catch (Exception e) {
            System.err.println("Error loading dashboard data: " + e.getMessage());

            // Set default values if error occurs
            request.setAttribute("totalCustomers", 0);
            request.setAttribute("totalItems", 0);
            request.setAttribute("totalBills", 0);
            request.setAttribute("lowStockCount", 0);
        }
    }

    /**
     * Handle POST requests (for future dashboard actions)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // For now, just redirect to GET
        doGet(request, response);
    }
}