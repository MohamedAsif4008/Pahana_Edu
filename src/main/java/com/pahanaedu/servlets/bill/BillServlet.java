package com.pahanaedu.servlets.bill;

import com.pahanaedu.models.Bill;
import com.pahanaedu.models.BillItem;
import com.pahanaedu.models.Customer;
import com.pahanaedu.models.Item;
import com.pahanaedu.service.interfaces.BillService;
import com.pahanaedu.service.interfaces.CustomerService;
import com.pahanaedu.service.interfaces.ItemService;
import com.pahanaedu.service.impl.BillServiceImpl;
import com.pahanaedu.service.impl.CustomerServiceImpl;
import com.pahanaedu.service.impl.ItemServiceImpl;
import com.pahanaedu.servlets.common.BaseServlet;
import com.pahanaedu.util.ValidationUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Servlet for Bill CRUD operations
 * Demonstrates transaction handling and business logic
 */
@WebServlet(name = "BillServlet", urlPatterns = {"/bills", "/bill"})
public class BillServlet extends BaseServlet {

    private BillService billService;
    private CustomerService customerService;
    private ItemService itemService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.billService = new BillServiceImpl();
        this.customerService = new CustomerServiceImpl();
        this.itemService = new ItemServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isUserLoggedIn(request)) {
            redirectTo(response, request.getContextPath() + "/login");
            return;
        }

        String action = getParameter(request, PARAM_ACTION, "list");

        try {
            switch (action) {
                case "list":
                    showBillList(request, response);
                    break;
                case "view":
                    showBillDetails(request, response);
                    break;
                case "create":
                    showCreateForm(request, response);
                    break;
                case "print":
                    printBill(request, response);
                    break;
                default:
                    showBillList(request, response);
            }
        } catch (Exception e) {
            handleException(request, response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isUserLoggedIn(request)) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }

        String action = getParameter(request, PARAM_ACTION, "create");

        try {
            switch (action) {
                case "create":
                    createBill(request, response);
                    break;
                case "cancel":
                    cancelBill(request, response);
                    break;
                default:
                    setErrorMessage(request, "Invalid action");
                    showBillList(request, response);
            }
        } catch (Exception e) {
            handleException(request, response, e);
        }
    }

    private void showBillList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int[] pagination = getPaginationParams(request);
        int page = pagination[0];
        int size = pagination[1];
        int offset = pagination[2];

        List<Bill> bills = billService.getAllBills(size, offset);
        int totalBills = billService.getTotalBillCount();
        int totalPages = (int) Math.ceil((double) totalBills / size);

        request.setAttribute("bills", bills);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalBills", totalBills);
        request.setAttribute("csrfToken", generateCSRFToken(request));

        forwardToJSP(request, response, "bill/list.jsp");
    }

    private void showBillDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String billNumber = getSanitizedParameter(request, PARAM_ID);
        if (!ValidationUtils.isValidBillNumber(billNumber)) {
            setErrorMessage(request, "Invalid bill number");
            showBillList(request, response);
            return;
        }

        Bill bill = billService.findBillByNumber(billNumber);
        if (bill == null) {
            setErrorMessage(request, "Bill not found");
            showBillList(request, response);
            return;
        }

        request.setAttribute("bill", bill);
        request.setAttribute("billItems", bill.getBillItems());
        forwardToJSP(request, response, "bill/view.jsp");
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nextBillNumber = billService.generateNextBillNumber();
        List<Customer> customers = customerService.getAllActiveCustomers();
        List<Item> items = itemService.getAllActiveItems();

        request.setAttribute("nextBillNumber", nextBillNumber);
        request.setAttribute("customers", customers);
        request.setAttribute("items", items);
        request.setAttribute("csrfToken", generateCSRFToken(request));

        forwardToJSP(request, response, "bill/create.jsp");
    }

    private void printBill(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String billNumber = getSanitizedParameter(request, PARAM_ID);
        Bill bill = billService.findBillByNumber(billNumber);

        if (bill == null) {
            setErrorMessage(request, "Bill not found");
            showBillList(request, response);
            return;
        }

        String billSummary = billService.getBillSummary(billNumber);

        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment; filename=\"bill_" + billNumber + ".txt\"");
        response.getWriter().write(billSummary);
    }

    private void createBill(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!validateRequiredParams(request, "customerAccountNumber", "paymentMethod")) {
            setErrorMessage(request, "Customer and payment method are required");
            showCreateForm(request, response);
            return;
        }

        try {
            String billNumber = getSanitizedParameter(request, "billNumber");
            String customerAccountNumber = getSanitizedParameter(request, "customerAccountNumber");
            String paymentMethodStr = request.getParameter("paymentMethod");
            String notes = getSanitizedParameter(request, "notes");

            // Validate customer exists
            Customer customer = customerService.findCustomerByAccountNumber(customerAccountNumber);
            if (customer == null) {
                setErrorMessage(request, "Customer not found");
                showCreateForm(request, response);
                return;
            }

            // Create bill
            Bill bill = new Bill(billNumber, customerAccountNumber, getCurrentUser(request).getUserId());
            bill.setPaymentMethod(Bill.PaymentMethod.valueOf(paymentMethodStr));
            bill.setNotes(notes);
            bill.setCustomer(customer);

            // Get bill items from request
            String[] itemIds = request.getParameterValues("itemId[]");
            String[] quantities = request.getParameterValues("quantity[]");

            if (itemIds != null && quantities != null) {
                for (int i = 0; i < itemIds.length; i++) {
                    if (i < quantities.length && ValidationUtils.isNotEmpty(itemIds[i])) {
                        String itemId = itemIds[i];
                        int quantity = Integer.parseInt(quantities[i]);

                        if (quantity > 0) {
                            Item item = itemService.findItemById(itemId);
                            if (item != null && item.canSell(quantity)) {
                                BillItem billItem = new BillItem(billNumber, item, quantity);
                                bill.addBillItem(billItem);
                            }
                        }
                    }
                }
            }

            if (bill.getBillItems().isEmpty()) {
                setErrorMessage(request, "Please add at least one item to the bill");
                showCreateForm(request, response);
                return;
            }

            // Create bill
            boolean created = billService.createBill(bill);

            if (created) {
                setSuccessMessage(request, "Bill created successfully");
                logAction(request, "CREATE_BILL", "Bill: " + billNumber);
                redirectTo(response, request.getContextPath() + "/bills?action=view&id=" + billNumber);
            } else {
                setErrorMessage(request, "Failed to create bill");
                showCreateForm(request, response);
            }

        } catch (NumberFormatException e) {
            setErrorMessage(request, "Invalid quantity format");
            showCreateForm(request, response);
        } catch (Exception e) {
            setErrorMessage(request, "Error creating bill: " + e.getMessage());
            showCreateForm(request, response);
        }
    }

    private void cancelBill(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String billNumber = getSanitizedParameter(request, "billNumber");
        if (!ValidationUtils.isValidBillNumber(billNumber)) {
            setErrorMessage(request, "Invalid bill number");
            showBillList(request, response);
            return;
        }

        boolean cancelled = billService.cancelBill(billNumber);

        if (cancelled) {
            setSuccessMessage(request, "Bill cancelled successfully");
            logAction(request, "CANCEL_BILL", "Bill: " + billNumber);
        } else {
            setErrorMessage(request, "Failed to cancel bill");
        }

        redirectTo(response, request.getContextPath() + "/bills");
    }

    @Override
    public void destroy() {
        super.destroy();
        this.billService = null;
        this.customerService = null;
        this.itemService = null;
    }
}