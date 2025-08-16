package com.pahanaedu.servlets.bill;

import com.pahanaedu.models.Bill;
import com.pahanaedu.service.interfaces.BillService;
import com.pahanaedu.service.impl.BillServiceImpl;
import com.pahanaedu.servlets.common.BaseServlet;
import com.pahanaedu.util.ValidationUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Simple Bill Search Servlet
 * Demonstrates search and filtering functionality
 */
@WebServlet(name = "BillSearchServlet", urlPatterns = {"/bills/search"})
public class BillSearchServlet extends BaseServlet {

    private BillService billService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.billService = new BillServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isUserLoggedIn(request)) {
            redirectTo(response, request.getContextPath() + "/login");
            return;
        }

        String action = getParameter(request, PARAM_ACTION, "search");

        try {
            switch (action) {
                case "search":
                    performSearch(request, response);
                    break;
                case "ajax":
                    performAjaxSearch(request, response);
                    break;
                default:
                    performSearch(request, response);
            }
        } catch (Exception e) {
            handleException(request, response, e);
        }
    }

    private void performSearch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String searchTerm = getSanitizedParameter(request, "q");
        String searchType = getParameter(request, "type", "customer");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");

        List<Bill> bills = null;

        try {
            if (ValidationUtils.isNotEmpty(startDate) && ValidationUtils.isNotEmpty(endDate)) {
                // Search by date range
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date start = new Date(sdf.parse(startDate).getTime());
                Date end = new Date(sdf.parse(endDate).getTime());
                bills = billService.getBillsByDateRange(start, end);
            } else if (ValidationUtils.isNotEmpty(searchTerm)) {
                // Search by term
                switch (searchType) {
                    case "customer":
                        bills = billService.getBillsByCustomer(searchTerm);
                        break;
                    case "billNumber":
                        Bill bill = billService.findBillByNumber(searchTerm);
                        bills = bill != null ? List.of(bill) : List.of();
                        break;
                    case "status":
                        try {
                            Bill.Status status = Bill.Status.valueOf(searchTerm.toUpperCase());
                            bills = billService.getBillsByStatus(status);
                        } catch (IllegalArgumentException e) {
                            bills = List.of();
                        }
                        break;
                    default:
                        bills = billService.getBillsByCustomer(searchTerm);
                }
            } else {
                // Get recent bills
                bills = billService.getAllBills(20, 0);
            }

            request.setAttribute("bills", bills);
            request.setAttribute("searchTerm", searchTerm);
            request.setAttribute("searchType", searchType);
            request.setAttribute("startDate", startDate);
            request.setAttribute("endDate", endDate);
            request.setAttribute("resultCount", bills.size());

            logAction(request, "BILL_SEARCH", "Term: " + searchTerm + ", Results: " + bills.size());

            forwardToJSP(request, response, "bill/bill-search-results.jsp");

        } catch (Exception e) {
            System.err.println("Error in bill search: " + e.getMessage());
            setErrorMessage(request, "Error performing search");
            redirectTo(response, request.getContextPath() + "/bills");
        }
    }

    private void performAjaxSearch(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String searchTerm = getSanitizedParameter(request, "q");

        if (!ValidationUtils.isNotEmpty(searchTerm) || searchTerm.length() < 2) {
            sendJsonResponse(response, "[]");
            return;
        }

        try {
            List<Bill> bills = billService.getBillsByCustomer(searchTerm);

            // Limit results for AJAX
            if (bills.size() > 10) {
                bills = bills.subList(0, 10);
            }

            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < bills.size(); i++) {
                Bill bill = bills.get(i);
                if (i > 0) json.append(",");
                json.append("{")
                        .append("\"billNumber\":\"").append(bill.getBillNumber()).append("\",")
                        .append("\"customerAccount\":\"").append(bill.getCustomerAccountNumber()).append("\",")
                        .append("\"customerName\":\"").append(escapeJson(bill.getCustomerName())).append("\",")
                        .append("\"totalAmount\":").append(bill.getTotalAmount()).append(",")
                        .append("\"status\":\"").append(bill.getStatus()).append("\",")
                        .append("\"billDate\":\"").append(bill.getBillDate()).append("\"")
                        .append("}");
            }
            json.append("]");

            sendJsonResponse(response, json.toString());

        } catch (Exception e) {
            sendJsonResponse(response, "[]");
        }
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    @Override
    public void destroy() {
        super.destroy();
        this.billService = null;
    }
}