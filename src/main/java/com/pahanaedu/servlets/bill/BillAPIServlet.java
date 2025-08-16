package com.pahanaedu.servlets.bill;

import com.pahanaedu.models.Bill;
import com.pahanaedu.models.BillItem;
import com.pahanaedu.service.interfaces.BillService;
import com.pahanaedu.service.impl.BillServiceImpl;
import com.pahanaedu.servlets.common.BaseServlet;
import com.pahanaedu.util.ValidationUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.List;

/**
 * Simple REST API for Bill operations
 * Demonstrates RESTful design for billing system
 */
@WebServlet(name = "BillAPIServlet", urlPatterns = {"/api/bills", "/api/bills/*"})
public class BillAPIServlet extends BaseServlet {

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
            sendApiError(response, 401, "Authentication required");
            return;
        }

        String pathInfo = request.getPathInfo();
        String billNumber = extractBillNumber(pathInfo);

        try {
            if (billNumber != null) {
                getBill(response, billNumber);
            } else {
                getBills(request, response);
            }
        } catch (Exception e) {
            sendApiError(response, 500, "Internal server error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isUserLoggedIn(request)) {
            sendApiError(response, 401, "Authentication required");
            return;
        }

        try {
            String jsonBody = readRequestBody(request);
            Bill bill = parseBillFromJson(jsonBody);

            if (bill == null) {
                sendApiError(response, 400, "Invalid bill data");
                return;
            }

            boolean created = billService.createBill(bill);

            if (created) {
                Bill createdBill = billService.findBillByNumber(bill.getBillNumber());
                sendApiSuccess(response, 201, "Bill created", billToJson(createdBill));
            } else {
                sendApiError(response, 400, "Failed to create bill");
            }

        } catch (Exception e) {
            sendApiError(response, 500, "Internal server error");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isUserLoggedIn(request)) {
            sendApiError(response, 401, "Authentication required");
            return;
        }

        String pathInfo = request.getPathInfo();
        String billNumber = extractBillNumber(pathInfo);

        if (billNumber == null) {
            sendApiError(response, 400, "Bill number required");
            return;
        }

        try {
            boolean cancelled = billService.cancelBill(billNumber);

            if (cancelled) {
                sendApiSuccess(response, 200, "Bill cancelled", null);
            } else {
                sendApiError(response, 400, "Failed to cancel bill");
            }

        } catch (Exception e) {
            sendApiError(response, 500, "Internal server error");
        }
    }

    private void getBill(HttpServletResponse response, String billNumber) throws IOException {
        Bill bill = billService.findBillByNumber(billNumber);

        if (bill != null) {
            sendApiSuccess(response, 200, "Bill found", billToJson(bill));
        } else {
            sendApiError(response, 404, "Bill not found");
        }
    }

    private void getBills(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String customer = getSanitizedParameter(request, "customer");
        String status = getSanitizedParameter(request, "status");
        int limit = getIntParameter(request, "limit", 20);

        List<Bill> bills;

        if (ValidationUtils.isNotEmpty(customer)) {
            bills = billService.getBillsByCustomer(customer);
        } else if (ValidationUtils.isNotEmpty(status)) {
            try {
                Bill.Status billStatus = Bill.Status.valueOf(status.toUpperCase());
                bills = billService.getBillsByStatus(billStatus);
            } catch (IllegalArgumentException e) {
                bills = List.of();
            }
        } else {
            bills = billService.getAllBills(limit, 0);
        }

        String billsJson = billsToJson(bills);
        sendApiSuccess(response, 200, "Bills retrieved", billsJson);
    }

    private String extractBillNumber(String pathInfo) {
        if (pathInfo == null || pathInfo.length() <= 1) {
            return null;
        }
        String path = pathInfo.substring(1);
        return ValidationUtils.isValidBillNumber(path) ? path : null;
    }

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

    private Bill parseBillFromJson(String json) {
        try {
            if (!ValidationUtils.isNotEmpty(json)) return null;

            String billNumber = extractJsonValue(json, "billNumber");
            String customerAccount = extractJsonValue(json, "customerAccount");
            String paymentMethod = extractJsonValue(json, "paymentMethod");
            String notes = extractJsonValue(json, "notes");

            if (!ValidationUtils.isNotEmpty(customerAccount)) return null;

            Bill bill = new Bill(billNumber, customerAccount, getCurrentUser(null).getUserId());

            if (ValidationUtils.isNotEmpty(paymentMethod)) {
                try {
                    bill.setPaymentMethod(Bill.PaymentMethod.valueOf(paymentMethod.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    // Default to CASH if invalid
                    bill.setPaymentMethod(Bill.PaymentMethod.CASH);
                }
            }

            bill.setNotes(notes);
            return bill;

        } catch (Exception e) {
            return null;
        }
    }

    private String extractJsonValue(String json, String key) {
        try {
            String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]*)\"|\"" + key + "\"\\s*:\\s*([^,}\\s]+)";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(json);
            return m.find() ? (m.group(1) != null ? m.group(1) : m.group(2)) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String billToJson(Bill bill) {
        if (bill == null) return "null";

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"billNumber\":\"").append(bill.getBillNumber()).append("\",")
                .append("\"customerAccount\":\"").append(bill.getCustomerAccountNumber()).append("\",")
                .append("\"customerName\":\"").append(escapeJson(bill.getCustomerName())).append("\",")
                .append("\"totalAmount\":").append(bill.getTotalAmount()).append(",")
                .append("\"paymentMethod\":\"").append(bill.getPaymentMethod()).append("\",")
                .append("\"status\":\"").append(bill.getStatus()).append("\",")
                .append("\"billDate\":\"").append(bill.getBillDate()).append("\",")
                .append("\"notes\":\"").append(escapeJson(bill.getNotes())).append("\",")
                .append("\"items\":[");

        List<BillItem> items = bill.getBillItems();
        for (int i = 0; i < items.size(); i++) {
            BillItem item = items.get(i);
            if (i > 0) json.append(",");
            json.append("{")
                    .append("\"itemId\":\"").append(item.getItemId()).append("\",")
                    .append("\"itemName\":\"").append(escapeJson(item.getItemName())).append("\",")
                    .append("\"quantity\":").append(item.getQuantity()).append(",")
                    .append("\"unitPrice\":").append(item.getUnitPrice()).append(",")
                    .append("\"lineTotal\":").append(item.getLineTotal())
                    .append("}");
        }

        json.append("]}");
        return json.toString();
    }

    private String billsToJson(List<Bill> bills) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < bills.size(); i++) {
            if (i > 0) json.append(",");
            json.append(billToJson(bills.get(i)));
        }
        json.append("]");
        return json.toString();
    }

    private void sendApiSuccess(HttpServletResponse response, int statusCode, String message, String data) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        StringBuilder json = new StringBuilder();
        json.append("{\"success\":true,\"message\":\"").append(message).append("\"");
        if (data != null) {
            json.append(",\"data\":").append(data);
        }
        json.append("}");
        response.getWriter().write(json.toString());
    }

    private void sendApiError(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        String json = String.format("{\"success\":false,\"error\":\"%s\",\"code\":%d}", message, statusCode);
        response.getWriter().write(json);
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