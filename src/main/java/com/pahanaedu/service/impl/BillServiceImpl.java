package com.pahanaedu.service.impl;

import com.pahanaedu.dao.BillDAO;
import com.pahanaedu.models.Bill;
import com.pahanaedu.models.BillItem;
import com.pahanaedu.models.Customer;
import com.pahanaedu.models.Item;
import com.pahanaedu.service.interfaces.BillService;
import com.pahanaedu.service.interfaces.CustomerService;
import com.pahanaedu.service.interfaces.ItemService;
import com.pahanaedu.util.ValidationUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service implementation for Bill management operations
 * Implements complex billing business logic with transaction management
 *
 * Design Patterns Used:
 * - Service Layer Pattern: Encapsulates complex billing logic
 * - Transaction Pattern: Ensures data consistency
 * - Strategy Pattern: Different calculation strategies
 * - Facade Pattern: Simplifies complex billing operations
 * - Observer Pattern: For stock updates
 *
 * @author Pahana Edu Development Team
 * @version 1.0
 */
public class BillServiceImpl implements BillService {

    private final BillDAO billDAO;
    private final CustomerService customerService;
    private final ItemService itemService;

    // Business constants
    private static final BigDecimal MAX_DISCOUNT_PERCENTAGE = new BigDecimal("50.00");
    private static final BigDecimal MAX_TAX_PERCENTAGE = new BigDecimal("30.00");
    private static final BigDecimal MIN_BILL_AMOUNT = new BigDecimal("0.01");

    /**
     * Constructor with dependency injection
     */
    public BillServiceImpl() {
        this.billDAO = new BillDAO();
        this.customerService = new CustomerServiceImpl();
        this.itemService = new ItemServiceImpl();
    }

    /**
     * Constructor for testing with DAO injection
     */
    public BillServiceImpl(BillDAO billDAO, CustomerService customerService, ItemService itemService) {
        this.billDAO = billDAO;
        this.customerService = customerService;
        this.itemService = itemService;
    }

    @Override
    public boolean createBill(Bill bill) {
        // Validate bill data
        if (!validateBill(bill)) {
            return false;
        }

        // Validate customer exists and can make purchase
        Customer customer = customerService.findCustomerByAccountNumber(bill.getCustomerAccountNumber());
        if (customer == null) {
            System.err.println("Customer not found: " + bill.getCustomerAccountNumber());
            return false;
        }

        if (!customerService.isValidForBilling(customer)) {
            System.err.println("Customer is not valid for billing: " + bill.getCustomerAccountNumber());
            return false;
        }

        // Validate stock availability
        if (!validateStockAvailability(bill.getBillItems())) {
            System.err.println("Insufficient stock for one or more items");
            return false;
        }

        // Check customer credit limit
        if (!canCustomerMakePurchase(customer, bill.getTotalAmount())) {
            System.err.println("Purchase amount exceeds customer credit limit");
            return false;
        }

        // Generate bill number if not provided
        if (bill.getBillNumber() == null || bill.getBillNumber().trim().isEmpty()) {
            bill.setBillNumber(generateNextBillNumber());
        }

        // Set timestamps
        if (bill.getBillDate() == null) {
            bill.setBillDate(new Timestamp(System.currentTimeMillis()));
        }

        // Recalculate bill totals
        bill = recalculateBill(bill);

        try {
            boolean created = billDAO.createBill(bill);
            if (created) {
                System.out.println("Bill created successfully: " + bill.getBillNumber());
            }
            return created;
        } catch (Exception e) {
            System.err.println("Error creating bill: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Bill findBillByNumber(String billNumber) {
        if (!ValidationUtils.isNotEmpty(billNumber)) {
            return null;
        }

        try {
            return billDAO.findByBillNumber(billNumber);
        } catch (Exception e) {
            System.err.println("Error finding bill by number: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Bill> getBillsByCustomer(String customerAccountNumber) {
        if (!ValidationUtils.isValidAccountNumber(customerAccountNumber)) {
            System.err.println("Valid customer account number is required");
            return List.of();
        }

        try {
            return billDAO.getBillsByCustomer(customerAccountNumber);
        } catch (Exception e) {
            System.err.println("Error getting bills by customer: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<Bill> getAllBills(int limit, int offset) {
        if (!ValidationUtils.isPositive(limit) || !ValidationUtils.isNonNegative(offset)) {
            System.err.println("Invalid pagination parameters");
            return List.of();
        }

        try {
            return billDAO.getAllBills(limit, offset);
        } catch (Exception e) {
            System.err.println("Error getting all bills: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<Bill> getBillsByDateRange(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            System.err.println("Start date and end date are required");
            return List.of();
        }

        if (startDate.after(endDate)) {
            System.err.println("Start date cannot be after end date");
            return List.of();
        }

        try {
            return billDAO.getBillsByDateRange(startDate, endDate);
        } catch (Exception e) {
            System.err.println("Error getting bills by date range: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public boolean updateBillStatus(String billNumber, Bill.Status status) {
        if (!ValidationUtils.isNotEmpty(billNumber) || status == null) {
            System.err.println("Bill number and status are required");
            return false;
        }

        // Verify bill exists
        Bill bill = findBillByNumber(billNumber);
        if (bill == null) {
            System.err.println("Bill not found: " + billNumber);
            return false;
        }

        // Business rule: Can't change status of cancelled bills
        if (bill.isCancelled()) {
            System.err.println("Cannot change status of cancelled bill: " + billNumber);
            return false;
        }

        try {
            boolean updated = billDAO.updateBillStatus(billNumber, status);
            if (updated) {
                System.out.println("Bill status updated: " + billNumber + " -> " + status);
            }
            return updated;
        } catch (Exception e) {
            System.err.println("Error updating bill status: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean cancelBill(String billNumber) {
        if (!ValidationUtils.isNotEmpty(billNumber)) {
            System.err.println("Bill number is required");
            return false;
        }

        // Verify bill exists and can be cancelled
        Bill bill = findBillByNumber(billNumber);
        if (bill == null) {
            System.err.println("Bill not found: " + billNumber);
            return false;
        }

        if (!bill.canBeCancelled()) {
            System.err.println("Bill cannot be cancelled: " + billNumber);
            return false;
        }

        try {
            boolean cancelled = billDAO.cancelBill(billNumber);
            if (cancelled) {
                System.out.println("Bill cancelled successfully: " + billNumber);
            }
            return cancelled;
        } catch (Exception e) {
            System.err.println("Error cancelling bill: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean addItemToBill(String billNumber, String itemId, int quantity) {
        // Validate inputs
        if (!ValidationUtils.isNotEmpty(billNumber) || !ValidationUtils.isNotEmpty(itemId) ||
                !ValidationUtils.isPositive(quantity)) {
            System.err.println("Valid bill number, item ID, and positive quantity are required");
            return false;
        }

        // Verify bill exists and can be modified
        Bill bill = findBillByNumber(billNumber);
        if (bill == null || !bill.canBeModified()) {
            System.err.println("Bill not found or cannot be modified: " + billNumber);
            return false;
        }

        // Verify item exists and is available
        Item item = itemService.findItemById(itemId);
        if (item == null || !itemService.isValidForSale(item, quantity)) {
            System.err.println("Item not available for sale: " + itemId);
            return false;
        }

        // Create bill item
        BillItem billItem = new BillItem(billNumber, item, quantity);
        bill.addBillItem(billItem);

        // Update bill in database
        return createBill(bill);
    }

    @Override
    public boolean removeItemFromBill(String billNumber, String itemId) {
        if (!ValidationUtils.isNotEmpty(billNumber) || !ValidationUtils.isNotEmpty(itemId)) {
            System.err.println("Bill number and item ID are required");
            return false;
        }

        // Verify bill exists and can be modified
        Bill bill = findBillByNumber(billNumber);
        if (bill == null || !bill.canBeModified()) {
            System.err.println("Bill not found or cannot be modified: " + billNumber);
            return false;
        }

        // Find and remove the item
        boolean removed = bill.getBillItems().removeIf(item -> item.getItemId().equals(itemId));

        if (!removed) {
            System.err.println("Item not found in bill: " + itemId);
            return false;
        }

        bill.recalculateAmounts();
        return createBill(bill);
    }

    @Override
    public boolean updateItemQuantity(String billNumber, String itemId, int newQuantity) {
        if (!ValidationUtils.isPositive(newQuantity)) {
            System.err.println("New quantity must be positive");
            return false;
        }

        // Verify bill exists and can be modified
        Bill bill = findBillByNumber(billNumber);
        if (bill == null || !bill.canBeModified()) {
            System.err.println("Bill not found or cannot be modified: " + billNumber);
            return false;
        }

        // Find and update the item
        for (BillItem billItem : bill.getBillItems()) {
            if (billItem.getItemId().equals(itemId)) {
                // Check stock availability
                if (!itemService.isAvailableForSale(itemId, newQuantity)) {
                    System.err.println("Insufficient stock for requested quantity");
                    return false;
                }

                billItem.setQuantity(newQuantity);
                bill.recalculateAmounts();
                return createBill(bill);
            }
        }

        System.err.println("Item not found in bill: " + itemId);
        return false;
    }

    @Override
    public boolean applyDiscount(String billNumber, BigDecimal discountAmount) {
        if (!ValidationUtils.isNonNegative(discountAmount)) {
            System.err.println("Discount amount must be non-negative");
            return false;
        }

        Bill bill = findBillByNumber(billNumber);
        if (bill == null || !bill.canBeModified()) {
            System.err.println("Bill not found or cannot be modified: " + billNumber);
            return false;
        }

        // Business rule: Discount cannot exceed subtotal
        if (discountAmount.compareTo(bill.getSubtotal()) > 0) {
            System.err.println("Discount amount cannot exceed subtotal");
            return false;
        }

        bill.applyDiscount(discountAmount);
        return createBill(bill);
    }

    @Override
    public boolean applyPercentageDiscount(String billNumber, double discountPercentage) {
        if (discountPercentage < 0 || discountPercentage > MAX_DISCOUNT_PERCENTAGE.doubleValue()) {
            System.err.println("Discount percentage must be between 0 and " + MAX_DISCOUNT_PERCENTAGE + "%");
            return false;
        }

        Bill bill = findBillByNumber(billNumber);
        if (bill == null || !bill.canBeModified()) {
            System.err.println("Bill not found or cannot be modified: " + billNumber);
            return false;
        }

        bill.applyPercentageDiscount(discountPercentage);
        return createBill(bill);
    }

    @Override
    public boolean applyTax(String billNumber, BigDecimal taxAmount) {
        if (!ValidationUtils.isNonNegative(taxAmount)) {
            System.err.println("Tax amount must be non-negative");
            return false;
        }

        Bill bill = findBillByNumber(billNumber);
        if (bill == null || !bill.canBeModified()) {
            System.err.println("Bill not found or cannot be modified: " + billNumber);
            return false;
        }

        bill.applyTax(taxAmount);
        return createBill(bill);
    }

    @Override
    public boolean applyPercentageTax(String billNumber, double taxPercentage) {
        if (taxPercentage < 0 || taxPercentage > MAX_TAX_PERCENTAGE.doubleValue()) {
            System.err.println("Tax percentage must be between 0 and " + MAX_TAX_PERCENTAGE + "%");
            return false;
        }

        Bill bill = findBillByNumber(billNumber);
        if (bill == null || !bill.canBeModified()) {
            System.err.println("Bill not found or cannot be modified: " + billNumber);
            return false;
        }

        bill.applyPercentageTax(taxPercentage);
        return createBill(bill);
    }

    @Override
    public boolean validateBill(Bill bill) {
        if (bill == null) {
            System.err.println("Bill object is required");
            return false;
        }

        // Validate bill number format if provided
        if (bill.getBillNumber() != null && !bill.getBillNumber().trim().isEmpty()) {
            if (!ValidationUtils.isValidBillNumber(bill.getBillNumber())) {
                System.err.println("Invalid bill number format: " + bill.getBillNumber());
                return false;
            }
        }

        // Validate customer account number
        if (!ValidationUtils.isValidAccountNumber(bill.getCustomerAccountNumber())) {
            System.err.println("Valid customer account number is required");
            return false;
        }

        // Validate created by user
        if (!ValidationUtils.isNotEmpty(bill.getCreatedBy())) {
            System.err.println("Created by user is required");
            return false;
        }

        // Validate payment method
        if (bill.getPaymentMethod() == null) {
            System.err.println("Payment method is required");
            return false;
        }

        // Validate bill items
        if (bill.getBillItems() == null || bill.getBillItems().isEmpty()) {
            System.err.println("Bill must have at least one item");
            return false;
        }

        for (BillItem billItem : bill.getBillItems()) {
            if (!validateBillItem(billItem)) {
                return false;
            }
        }

        // Validate total amount
        if (!ValidationUtils.isPositive(bill.getTotalAmount())) {
            System.err.println("Bill total amount must be positive");
            return false;
        }

        return true;
    }

    @Override
    public boolean validateBillItem(BillItem billItem) {
        if (billItem == null) {
            System.err.println("Bill item object is required");
            return false;
        }

        // Validate item ID
        if (!ValidationUtils.isValidItemId(billItem.getItemId())) {
            System.err.println("Valid item ID is required");
            return false;
        }

        // Validate quantity
        if (!ValidationUtils.isPositive(billItem.getQuantity())) {
            System.err.println("Bill item quantity must be positive");
            return false;
        }

        // Validate unit price
        if (!ValidationUtils.isPositive(billItem.getUnitPrice())) {
            System.err.println("Bill item unit price must be positive");
            return false;
        }

        // Validate line total
        if (!ValidationUtils.isNonNegative(billItem.getLineTotal())) {
            System.err.println("Bill item line total must be non-negative");
            return false;
        }

        return true;
    }

    @Override
    public String generateNextBillNumber() {
        try {
            return billDAO.generateNextBillNumber();
        } catch (Exception e) {
            System.err.println("Error generating bill number: " + e.getMessage());
            return "BILL000001"; // Fallback default
        }
    }

    @Override
    public int getTotalBillCount() {
        try {
            return billDAO.getTotalBillCount();
        } catch (Exception e) {
            System.err.println("Error getting total bill count: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public Bill recalculateBill(Bill bill) {
        if (bill == null) {
            return null;
        }

        // Recalculate all bill item totals
        for (BillItem billItem : bill.getBillItems()) {
            billItem.calculateLineTotal();
        }

        // Recalculate bill totals
        bill.recalculateAmounts();

        return bill;
    }

    @Override
    public boolean canCustomerMakePurchase(Customer customer, BigDecimal totalAmount) {
        if (customer == null || totalAmount == null) {
            return false;
        }

        if (!customerService.isValidForBilling(customer)) {
            return false;
        }

        // If customer has no credit limit, they can make any purchase
        if (customer.getCreditLimit() == null ||
                customer.getCreditLimit().compareTo(BigDecimal.ZERO) == 0) {
            return true;
        }

        // Check if purchase amount is within credit limit
        return totalAmount.compareTo(customer.getCreditLimit()) <= 0;
    }

    @Override
    public boolean processPayment(String billNumber, Bill.PaymentMethod paymentMethod) {
        if (!ValidationUtils.isNotEmpty(billNumber) || paymentMethod == null) {
            System.err.println("Bill number and payment method are required");
            return false;
        }

        Bill bill = findBillByNumber(billNumber);
        if (bill == null) {
            System.err.println("Bill not found: " + billNumber);
            return false;
        }

        // Update payment method and mark as paid
        bill.setPaymentMethod(paymentMethod);
        bill.setStatus(Bill.Status.PAID);

        return updateBillStatus(billNumber, Bill.Status.PAID);
    }

    @Override
    public String getBillSummary(String billNumber) {
        Bill bill = findBillByNumber(billNumber);
        if (bill == null) {
            return "Bill not found: " + billNumber;
        }

        StringBuilder summary = new StringBuilder();
        summary.append("=".repeat(50)).append("\n");
        summary.append("PAHANA EDU BILLING SYSTEM\n");
        summary.append("=".repeat(50)).append("\n");
        summary.append("Bill Number: ").append(bill.getBillNumber()).append("\n");
        summary.append("Date: ").append(bill.getBillDate()).append("\n");
        summary.append("Customer: ").append(bill.getCustomerName()).append("\n");
        summary.append("Account: ").append(bill.getCustomerAccountNumber()).append("\n");
        summary.append("-".repeat(50)).append("\n");

        // List items
        for (BillItem item : bill.getBillItems()) {
            summary.append(String.format("%-20s %3d x %8s = %10s\n",
                    item.getItemName(),
                    item.getQuantity(),
                    item.getFormattedUnitPrice(),
                    item.getFormattedLineTotal()));
        }

        summary.append("-".repeat(50)).append("\n");
        summary.append(String.format("%-35s %10s\n", "Subtotal:", bill.getFormattedSubtotal()));

        if (bill.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            summary.append(String.format("%-35s %10s\n", "Discount:", bill.getFormattedDiscount()));
        }

        if (bill.getTaxAmount().compareTo(BigDecimal.ZERO) > 0) {
            summary.append(String.format("%-35s %10s\n", "Tax:", bill.getFormattedTax()));
        }

        summary.append("=".repeat(50)).append("\n");
        summary.append(String.format("%-35s %10s\n", "TOTAL:", bill.getFormattedTotal()));
        summary.append("=".repeat(50)).append("\n");
        summary.append("Payment Method: ").append(bill.getPaymentMethodDisplay()).append("\n");
        summary.append("Status: ").append(bill.getStatusDisplay()).append("\n");
        summary.append("Created By: ").append(bill.getCreatedByName()).append("\n");
        summary.append("=".repeat(50)).append("\n");

        return summary.toString();
    }

    @Override
    public String getSalesReport(Date startDate, Date endDate) {
        List<Bill> bills = getBillsByDateRange(startDate, endDate);
        BigDecimal totalSales = calculateTotalSales(startDate, endDate);

        StringBuilder report = new StringBuilder();
        report.append("SALES REPORT\n");
        report.append("Period: ").append(startDate).append(" to ").append(endDate).append("\n");
        report.append("=".repeat(50)).append("\n");
        report.append("Total Bills: ").append(bills.size()).append("\n");
        report.append("Total Sales: Rs. ").append(totalSales).append("\n");

        // Group by payment method
        Map<Bill.PaymentMethod, Integer> paymentMethodCount = new HashMap<>();
        Map<Bill.PaymentMethod, BigDecimal> paymentMethodTotal = new HashMap<>();

        for (Bill bill : bills) {
            if (bill.isPaid()) {
                paymentMethodCount.merge(bill.getPaymentMethod(), 1, Integer::sum);
                paymentMethodTotal.merge(bill.getPaymentMethod(), bill.getTotalAmount(), BigDecimal::add);
            }
        }

        report.append("\nPayment Method Breakdown:\n");
        for (Bill.PaymentMethod method : Bill.PaymentMethod.values()) {
            int count = paymentMethodCount.getOrDefault(method, 0);
            BigDecimal total = paymentMethodTotal.getOrDefault(method, BigDecimal.ZERO);
            report.append(String.format("%s: %d bills, Rs. %.2f\n", method, count, total));
        }

        return report.toString();
    }

    @Override
    public List<String> getTopSellingItems(int limit) {
        if (!ValidationUtils.isPositive(limit)) {
            return List.of();
        }

        try {
            // Get all paid bills
            List<Bill> allBills = getAllBills(Integer.MAX_VALUE, 0);
            Map<String, Integer> itemQuantities = new HashMap<>();

            // Calculate total quantities sold per item
            for (Bill bill : allBills) {
                if (bill.isPaid()) {
                    for (BillItem billItem : bill.getBillItems()) {
                        String itemName = billItem.getItemName();
                        itemQuantities.merge(itemName, billItem.getQuantity(), Integer::sum);
                    }
                }
            }

            // Sort by quantity and return top items
            return itemQuantities.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(limit)
                    .map(entry -> entry.getKey() + " (" + entry.getValue() + " sold)")
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("Error getting top selling items: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public BigDecimal calculateTotalSales(Date startDate, Date endDate) {
        List<Bill> bills = getBillsByDateRange(startDate, endDate);
        return bills.stream()
                .filter(Bill::isPaid)
                .map(Bill::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public List<Bill> getBillsByPaymentMethod(Bill.PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            return List.of();
        }

        try {
            return getAllBills(Integer.MAX_VALUE, 0).stream()
                    .filter(bill -> paymentMethod.equals(bill.getPaymentMethod()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting bills by payment method: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<Bill> getBillsByStatus(Bill.Status status) {
        if (status == null) {
            return List.of();
        }

        try {
            return getAllBills(Integer.MAX_VALUE, 0).stream()
                    .filter(bill -> status.equals(bill.getStatus()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting bills by status: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public Bill createBillFromCart(String customerAccountNumber, List<BillItem> cartItems,
                                   Bill.PaymentMethod paymentMethod, String createdBy) {
        // Validate inputs
        if (!ValidationUtils.isValidAccountNumber(customerAccountNumber) ||
                cartItems == null || cartItems.isEmpty() ||
                paymentMethod == null || !ValidationUtils.isNotEmpty(createdBy)) {
            System.err.println("Invalid parameters for creating bill from cart");
            return null;
        }

        // Validate customer
        Customer customer = customerService.findCustomerByAccountNumber(customerAccountNumber);
        if (customer == null || !customerService.isValidForBilling(customer)) {
            System.err.println("Customer not valid for billing: " + customerAccountNumber);
            return null;
        }

        // Validate all cart items
        for (BillItem cartItem : cartItems) {
            if (!validateBillItem(cartItem)) {
                System.err.println("Invalid cart item: " + cartItem.getItemId());
                return null;
            }
        }

        // Validate stock availability
        if (!validateStockAvailability(cartItems)) {
            System.err.println("Insufficient stock for cart items");
            return null;
        }

        // Create bill
        Bill bill = new Bill(generateNextBillNumber(), customerAccountNumber, createdBy);
        bill.setPaymentMethod(paymentMethod);
        bill.setBillItems(new ArrayList<>(cartItems));
        bill.setCustomer(customer);

        // Set bill numbers for all items
        for (BillItem item : bill.getBillItems()) {
            item.setBillNumber(bill.getBillNumber());
        }

        // Recalculate totals
        bill = recalculateBill(bill);

        // Check credit limit
        if (!canCustomerMakePurchase(customer, bill.getTotalAmount())) {
            System.err.println("Purchase exceeds customer credit limit");
            return null;
        }

        // Create the bill
        if (createBill(bill)) {
            return bill;
        }

        return null;
    }

    @Override
    public boolean validateStockAvailability(List<BillItem> billItems) {
        if (billItems == null || billItems.isEmpty()) {
            return false;
        }

        for (BillItem billItem : billItems) {
            if (!itemService.isAvailableForSale(billItem.getItemId(), billItem.getQuantity())) {
                System.err.println("Insufficient stock for item: " + billItem.getItemId() +
                        " (requested: " + billItem.getQuantity() + ")");
                return false;
            }
        }

        return true;
    }

    /**
     * Business method: Calculate average bill amount
     */
    public BigDecimal calculateAverageBillAmount() {
        try {
            List<Bill> allBills = getAllBills(Integer.MAX_VALUE, 0);
            List<Bill> paidBills = allBills.stream()
                    .filter(Bill::isPaid)
                    .collect(Collectors.toList());

            if (paidBills.isEmpty()) {
                return BigDecimal.ZERO;
            }

            BigDecimal totalAmount = paidBills.stream()
                    .map(Bill::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return totalAmount.divide(BigDecimal.valueOf(paidBills.size()), 2, RoundingMode.HALF_UP);

        } catch (Exception e) {
            System.err.println("Error calculating average bill amount: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * Business method: Get daily sales summary
     */
    public String getDailySalesSummary(Date date) {
        List<Bill> dailyBills = getBillsByDateRange(date, date);
        BigDecimal dailySales = calculateTotalSales(date, date);

        StringBuilder summary = new StringBuilder();
        summary.append("Daily Sales Summary for ").append(date).append("\n");
        summary.append("=".repeat(40)).append("\n");
        summary.append("Total Bills: ").append(dailyBills.size()).append("\n");
        summary.append("Paid Bills: ").append(dailyBills.stream().mapToInt(b -> b.isPaid() ? 1 : 0).sum()).append("\n");
        summary.append("Total Sales: Rs. ").append(dailySales).append("\n");

        if (!dailyBills.isEmpty()) {
            BigDecimal avgBill = dailySales.divide(BigDecimal.valueOf(dailyBills.size()), 2, RoundingMode.HALF_UP);
            summary.append("Average Bill: Rs. ").append(avgBill).append("\n");
        }

        return summary.toString();
    }

    /**
     * Business method: Get customer purchase history summary
     */
    public String getCustomerPurchaseHistory(String customerAccountNumber) {
        List<Bill> customerBills = getBillsByCustomer(customerAccountNumber);
        Customer customer = customerService.findCustomerByAccountNumber(customerAccountNumber);

        if (customer == null) {
            return "Customer not found: " + customerAccountNumber;
        }

        BigDecimal totalPurchases = customerBills.stream()
                .filter(Bill::isPaid)
                .map(Bill::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        StringBuilder history = new StringBuilder();
        history.append("Purchase History for ").append(customer.getDisplayName()).append("\n");
        history.append("Account: ").append(customerAccountNumber).append("\n");
        history.append("=".repeat(50)).append("\n");
        history.append("Total Bills: ").append(customerBills.size()).append("\n");
        history.append("Total Purchases: Rs. ").append(totalPurchases).append("\n");

        if (!customerBills.isEmpty()) {
            history.append("Latest Bill: ").append(customerBills.get(0).getBillNumber()).append("\n");
            history.append("Latest Purchase Date: ").append(customerBills.get(0).getBillDate()).append("\n");
        }

        return history.toString();
    }

    /**
     * Business method: Apply business discount rules
     */
    public boolean applyBusinessDiscountRules(String billNumber) {
        Bill bill = findBillByNumber(billNumber);
        if (bill == null || !bill.canBeModified()) {
            return false;
        }

        BigDecimal subtotal = bill.getSubtotal();
        BigDecimal discountPercentage = BigDecimal.ZERO;

        // Business rules for automatic discounts
        if (subtotal.compareTo(new BigDecimal("10000")) >= 0) {
            discountPercentage = new BigDecimal("10"); // 10% for orders over Rs. 10,000
        } else if (subtotal.compareTo(new BigDecimal("5000")) >= 0) {
            discountPercentage = new BigDecimal("5"); // 5% for orders over Rs. 5,000
        } else if (subtotal.compareTo(new BigDecimal("2000")) >= 0) {
            discountPercentage = new BigDecimal("2"); // 2% for orders over Rs. 2,000
        }

        if (discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
            return applyPercentageDiscount(billNumber, discountPercentage.doubleValue());
        }

        return true; // No discount applied, but operation successful
    }

    /**
     * Business method: Validate bill for checkout
     */
    public boolean validateBillForCheckout(String billNumber) {
        Bill bill = findBillByNumber(billNumber);
        if (bill == null) {
            System.err.println("Bill not found for checkout validation");
            return false;
        }

        // Validate bill has items
        if (!bill.hasItems()) {
            System.err.println("Bill has no items");
            return false;
        }

        // Validate total amount
        if (!bill.hasValidTotal()) {
            System.err.println("Bill has invalid total amount");
            return false;
        }

        // Validate customer
        if (bill.getCustomer() == null || !customerService.isValidForBilling(bill.getCustomer())) {
            System.err.println("Customer is not valid for billing");
            return false;
        }

        // Validate stock availability
        if (!validateStockAvailability(bill.getBillItems())) {
            System.err.println("Stock not available for bill items");
            return false;
        }

        // Validate credit limit
        if (!canCustomerMakePurchase(bill.getCustomer(), bill.getTotalAmount())) {
            System.err.println("Purchase exceeds customer credit limit");
            return false;
        }

        return true;
    }

    @Override
    public int getPaidBillCount() {
        try {
            return billDAO.getPaidBillCount();
        } catch (Exception e) {
            System.err.println("Error getting paid bill count: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public BigDecimal getTotalRevenue() {
        try {
            return billDAO.getTotalRevenue();
        } catch (Exception e) {
            System.err.println("Error getting total revenue: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    @Override
    public BigDecimal getAverageBillAmount() {
        try {
            return billDAO.getAverageBillAmount();
        } catch (Exception e) {
            System.err.println("Error getting average bill amount: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }
}