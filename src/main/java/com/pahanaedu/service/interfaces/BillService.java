package com.pahanaedu.service.interfaces;

import com.pahanaedu.models.Bill;
import com.pahanaedu.models.BillItem;
import com.pahanaedu.models.Customer;
import com.pahanaedu.models.Item;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

/**
 * Service interface for Bill management operations
 * Defines business logic operations for billing and invoice management
 *
 * Design Pattern: Service Layer Pattern
 * - Separates business logic from data access
 * - Provides transaction boundaries
 * - Encapsulates complex billing rules
 *
 * @author Pahana Edu Development Team
 * @version 1.0
 */
public interface BillService {

    /**
     * Create a new bill with validation and stock management
     *
     * @param bill Bill object to create
     * @return true if creation successful, false otherwise
     */
    boolean createBill(Bill bill);

    /**
     * Find bill by bill number
     *
     * @param billNumber Bill number to search for
     * @return Bill object if found, null otherwise
     */
    Bill findBillByNumber(String billNumber);

    /**
     * Get bills by customer account number
     *
     * @param customerAccountNumber Customer account number
     * @return List of bills for the customer
     */
    List<Bill> getBillsByCustomer(String customerAccountNumber);

    /**
     * Get all bills with pagination
     *
     * @param limit Number of records to return
     * @param offset Number of records to skip
     * @return List of bills
     */
    List<Bill> getAllBills(int limit, int offset);

    /**
     * Get bills by date range
     *
     * @param startDate Start date
     * @param endDate End date
     * @return List of bills in date range
     */
    List<Bill> getBillsByDateRange(Date startDate, Date endDate);

    /**
     * Update bill status
     *
     * @param billNumber Bill number
     * @param status New status
     * @return true if update successful, false otherwise
     */
    boolean updateBillStatus(String billNumber, Bill.Status status);

    /**
     * Cancel bill and restore stock
     *
     * @param billNumber Bill number to cancel
     * @return true if cancellation successful, false otherwise
     */
    boolean cancelBill(String billNumber);

    /**
     * Add item to bill
     *
     * @param billNumber Bill number
     * @param itemId Item ID to add
     * @param quantity Quantity to add
     * @return true if item added successfully, false otherwise
     */
    boolean addItemToBill(String billNumber, String itemId, int quantity);

    /**
     * Remove item from bill
     *
     * @param billNumber Bill number
     * @param itemId Item ID to remove
     * @return true if item removed successfully, false otherwise
     */
    boolean removeItemFromBill(String billNumber, String itemId);

    /**
     * Update item quantity in bill
     *
     * @param billNumber Bill number
     * @param itemId Item ID
     * @param newQuantity New quantity
     * @return true if quantity updated successfully, false otherwise
     */
    boolean updateItemQuantity(String billNumber, String itemId, int newQuantity);

    /**
     * Apply discount to bill
     *
     * @param billNumber Bill number
     * @param discountAmount Discount amount
     * @return true if discount applied successfully, false otherwise
     */
    boolean applyDiscount(String billNumber, BigDecimal discountAmount);

    /**
     * Apply percentage discount to bill
     *
     * @param billNumber Bill number
     * @param discountPercentage Discount percentage (0-100)
     * @return true if discount applied successfully, false otherwise
     */
    boolean applyPercentageDiscount(String billNumber, double discountPercentage);

    /**
     * Apply tax to bill
     *
     * @param billNumber Bill number
     * @param taxAmount Tax amount
     * @return true if tax applied successfully, false otherwise
     */
    boolean applyTax(String billNumber, BigDecimal taxAmount);

    /**
     * Apply percentage tax to bill
     *
     * @param billNumber Bill number
     * @param taxPercentage Tax percentage
     * @return true if tax applied successfully, false otherwise
     */
    boolean applyPercentageTax(String billNumber, double taxPercentage);

    /**
     * Validate bill data
     *
     * @param bill Bill object to validate
     * @return true if bill data is valid, false otherwise
     */
    boolean validateBill(Bill bill);

    /**
     * Validate bill item
     *
     * @param billItem Bill item to validate
     * @return true if bill item is valid, false otherwise
     */
    boolean validateBillItem(BillItem billItem);

    /**
     * Generate next bill number
     *
     * @return Next available bill number
     */
    String generateNextBillNumber();

    /**
     * Get total bill count
     *
     * @return Total number of bills
     */
    int getTotalBillCount();

    /**
     * Calculate bill totals and update amounts
     *
     * @param bill Bill to recalculate
     * @return Updated bill with recalculated amounts
     */
    Bill recalculateBill(Bill bill);

    /**
     * Check if customer can make purchase based on credit limit
     *
     * @param customer Customer making purchase
     * @param totalAmount Total purchase amount
     * @return true if customer can make purchase, false otherwise
     */
    boolean canCustomerMakePurchase(Customer customer, BigDecimal totalAmount);

    /**
     * Process payment for bill
     *
     * @param billNumber Bill number
     * @param paymentMethod Payment method
     * @return true if payment processed successfully, false otherwise
     */
    boolean processPayment(String billNumber, Bill.PaymentMethod paymentMethod);

    /**
     * Get bill summary for printing
     *
     * @param billNumber Bill number
     * @return Formatted bill summary string
     */
    String getBillSummary(String billNumber);

    /**
     * Get sales report for date range
     *
     * @param startDate Start date
     * @param endDate End date
     * @return Sales report summary
     */
    String getSalesReport(Date startDate, Date endDate);

    /**
     * Get top selling items
     *
     * @param limit Number of top items to return
     * @return List of top selling items with quantities
     */
    List<String> getTopSellingItems(int limit);

    /**
     * Calculate total sales for date range
     *
     * @param startDate Start date
     * @param endDate End date
     * @return Total sales amount
     */
    BigDecimal calculateTotalSales(Date startDate, Date endDate);

    /**
     * Get bills by payment method
     *
     * @param paymentMethod Payment method filter
     * @return List of bills with specified payment method
     */
    List<Bill> getBillsByPaymentMethod(Bill.PaymentMethod paymentMethod);

    /**
     * Get bills by status
     *
     * @param status Bill status filter
     * @return List of bills with specified status
     */
    List<Bill> getBillsByStatus(Bill.Status status);

    /**
     * Create bill from cart items
     *
     * @param customerAccountNumber Customer account number
     * @param cartItems List of items with quantities
     * @param paymentMethod Payment method
     * @param createdBy User who created the bill
     * @return Created bill if successful, null otherwise
     */
    Bill createBillFromCart(String customerAccountNumber, List<BillItem> cartItems,
                            Bill.PaymentMethod paymentMethod, String createdBy);

    /**
     * Validate stock availability for bill items
     *
     * @param billItems List of bill items to validate
     * @return true if all items have sufficient stock, false otherwise
     */
    boolean validateStockAvailability(List<BillItem> billItems);
}