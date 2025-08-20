package com.pahanaedu.servlets.common;

import com.pahanaedu.models.User;
import com.pahanaedu.service.interfaces.CustomerService;
import com.pahanaedu.service.interfaces.ItemService;
import com.pahanaedu.service.interfaces.BillService;
import com.pahanaedu.service.impl.CustomerServiceImpl;
import com.pahanaedu.service.impl.ItemServiceImpl;
import com.pahanaedu.service.impl.BillServiceImpl;
import com.pahanaedu.dao.CustomerDAO;
import com.pahanaedu.dao.ItemDAO;
import com.pahanaedu.dao.BillDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;


@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard", "/home"})
public class DashboardServlet extends BaseServlet {

    // Service instances
    private CustomerService customerService;
    private ItemService itemService;
    private BillService billService;

    @Override
    public void init() throws ServletException {
        super.init();
        
        // Initialize DAOs (they use singleton pattern internally)
        CustomerDAO customerDAO = new CustomerDAO();
        ItemDAO itemDAO = new ItemDAO();
        BillDAO billDAO = new BillDAO();
        
        // Initialize services
        this.customerService = new CustomerServiceImpl(customerDAO);
        this.itemService = new ItemServiceImpl(itemDAO);
        this.billService = new BillServiceImpl(billDAO, customerService, itemService);
    }

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

            // Load dynamic dashboard data
            loadDashboardData(request);

            // Forward to dashboard JSP
            forwardToJSP(request, response, "common/dashboard.jsp");

        } catch (Exception e) {
            System.err.println("Dashboard error: " + e.getMessage());
            e.printStackTrace();
            setErrorMessage(request, "Error loading dashboard. Please try again.");
            forwardToJSP(request, response, "common/dashboard.jsp");
        }
    }

    private void loadDashboardData(HttpServletRequest request) {
        try {
            // Get real-time statistics from services
            int totalCustomers = customerService.getActiveCustomerCount();
            int totalItems = itemService.getActiveItemCount();
            int totalBills = billService.getTotalBillCount();
            int lowStockCount = itemService.getLowStockItems().size();

            // Set dashboard attributes
            request.setAttribute("totalCustomers", totalCustomers);
            request.setAttribute("totalItems", totalItems);
            request.setAttribute("totalBills", totalBills);
            request.setAttribute("lowStockCount", lowStockCount);

            // Additional statistics for enhanced dashboard
            BigDecimal totalInventoryValue = itemService.calculateTotalInventoryValue();
            int outOfStockCount = itemService.getOutOfStockItems().size();
            
            request.setAttribute("totalInventoryValue", totalInventoryValue);
            request.setAttribute("outOfStockCount", outOfStockCount);

            // Recent activity data
            List<com.pahanaedu.models.Customer> recentCustomers = customerService.getAllActiveCustomers();
            if (recentCustomers.size() > 5) {
                recentCustomers = recentCustomers.subList(0, 5); // Get only first 5
            }
            request.setAttribute("recentCustomers", recentCustomers);

            // System status
            request.setAttribute("databaseStatus", "Connected");
            request.setAttribute("lastUpdate", new java.util.Date());

            System.out.println("Dashboard data loaded successfully:");
            System.out.println("- Total Customers: " + totalCustomers);
            System.out.println("- Total Items: " + totalItems);
            System.out.println("- Total Bills: " + totalBills);
            System.out.println("- Low Stock Items: " + lowStockCount);
            System.out.println("- Total Inventory Value: Rs. " + totalInventoryValue);

        } catch (Exception e) {
            System.err.println("Error loading dashboard data: " + e.getMessage());
            e.printStackTrace();

            // Set default values if error occurs
            request.setAttribute("totalCustomers", 0);
            request.setAttribute("totalItems", 0);
            request.setAttribute("totalBills", 0);
            request.setAttribute("lowStockCount", 0);
            request.setAttribute("totalInventoryValue", BigDecimal.ZERO);
            request.setAttribute("outOfStockCount", 0);
            request.setAttribute("databaseStatus", "Error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // For now, just redirect to GET
        doGet(request, response);
    }


    @Override
    public void destroy() {
        super.destroy();
        this.customerService = null;
        this.itemService = null;
        this.billService = null;
    }
}