package com.pahanaedu.models;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Bill model class
 * Represents customer bills/invoices in the Pahana Edu billing system
 *
 * @author Pahana Edu Development Team
 * @version 1.0
 */
public class Bill {

    // Enums for bill-related data
    public enum PaymentMethod {
        CASH, CARD
    }

    public enum Status {
        PENDING, PAID, CANCELLED
    }

    // Private fields
    private String billNumber;
    private String customerAccountNumber;
    private BigDecimal totalAmount;
    private PaymentMethod paymentMethod;
    private Timestamp billDate;
    private String createdBy;
    private Status status;
    private String notes;

    // Related objects
    private Customer customer;
    private User createdByUser;
    private List<BillItem> billItems;

    // Calculated fields
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;

    // Default constructor
    public Bill() {
        this.totalAmount = BigDecimal.ZERO;
        this.subtotal = BigDecimal.ZERO;
        this.taxAmount = BigDecimal.ZERO;
        this.discountAmount = BigDecimal.ZERO;
        this.billDate = new Timestamp(System.currentTimeMillis());
        this.status = Status.PAID;
        this.billItems = new ArrayList<>();
    }

    // Constructor with essential fields
    public Bill(String billNumber, String customerAccountNumber, String createdBy) {
        this();
        this.billNumber = billNumber;
        this.customerAccountNumber = customerAccountNumber;
        this.createdBy = createdBy;
    }

    // Full constructor
    public Bill(String billNumber, String customerAccountNumber, PaymentMethod paymentMethod, String createdBy) {
        this(billNumber, customerAccountNumber, createdBy);
        this.paymentMethod = paymentMethod;
    }

    // Getters and Setters
    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public String getCustomerAccountNumber() {
        return customerAccountNumber;
    }

    public void setCustomerAccountNumber(String customerAccountNumber) {
        this.customerAccountNumber = customerAccountNumber;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Timestamp getBillDate() {
        return billDate;
    }

    public void setBillDate(Timestamp billDate) {
        this.billDate = billDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public User getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(User createdByUser) {
        this.createdByUser = createdByUser;
    }

    public List<BillItem> getBillItems() {
        return billItems;
    }

    public void setBillItems(List<BillItem> billItems) {
        this.billItems = billItems != null ? billItems : new ArrayList<>();
        recalculateAmounts();
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    // Business logic methods
    public void addBillItem(BillItem billItem) {
        if (billItem != null) {
            billItems.add(billItem);
            billItem.setBillNumber(this.billNumber);
            recalculateAmounts();
        }
    }

    public void removeBillItem(BillItem billItem) {
        if (billItems.remove(billItem)) {
            recalculateAmounts();
        }
    }

    public void removeBillItem(int index) {
        if (index >= 0 && index < billItems.size()) {
            billItems.remove(index);
            recalculateAmounts();
        }
    }

    public void clearBillItems() {
        billItems.clear();
        recalculateAmounts();
    }

    // Calculate amounts based on bill items
    public void recalculateAmounts() {
        subtotal = BigDecimal.ZERO;

        for (BillItem item : billItems) {
            if (item.getLineTotal() != null) {
                subtotal = subtotal.add(item.getLineTotal());
            }
        }

        // Calculate total (subtotal + tax - discount)
        totalAmount = subtotal.add(taxAmount).subtract(discountAmount);

        // Ensure total is not negative
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            totalAmount = BigDecimal.ZERO;
        }
    }

    // Apply discount
    public void applyDiscount(BigDecimal discount) {
        if (discount != null && discount.compareTo(BigDecimal.ZERO) >= 0) {
            this.discountAmount = discount;
            recalculateAmounts();
        }
    }

    // Apply tax
    public void applyTax(BigDecimal tax) {
        if (tax != null && tax.compareTo(BigDecimal.ZERO) >= 0) {
            this.taxAmount = tax;
            recalculateAmounts();
        }
    }

    // Apply percentage discount
    public void applyPercentageDiscount(double percentage) {
        if (percentage >= 0 && percentage <= 100) {
            BigDecimal discountPercent = BigDecimal.valueOf(percentage / 100);
            BigDecimal discount = subtotal.multiply(discountPercent);
            applyDiscount(discount);
        }
    }

    // Apply percentage tax
    public void applyPercentageTax(double percentage) {
        if (percentage >= 0) {
            BigDecimal taxPercent = BigDecimal.valueOf(percentage / 100);
            BigDecimal tax = subtotal.multiply(taxPercent);
            applyTax(tax);
        }
    }

    // Status checks
    public boolean isPaid() {
        return Status.PAID.equals(status);
    }

    public boolean isPending() {
        return Status.PENDING.equals(status);
    }

    public boolean isCancelled() {
        return Status.CANCELLED.equals(status);
    }

    public boolean canBeModified() {
        return Status.PENDING.equals(status);
    }

    public boolean canBeCancelled() {
        return Status.PENDING.equals(status) || Status.PAID.equals(status);
    }

    // Payment method checks
    public boolean isCashPayment() {
        return PaymentMethod.CASH.equals(paymentMethod);
    }

    public boolean isCardPayment() {
        return PaymentMethod.CARD.equals(paymentMethod);
    }

    // Get bill summary
    public int getTotalItems() {
        return billItems.size();
    }

    public int getTotalQuantity() {
        return billItems.stream().mapToInt(BillItem::getQuantity).sum();
    }

    public String getCustomerName() {
        return customer != null ? customer.getName() : "Unknown Customer";
    }

    public String getCreatedByName() {
        return createdByUser != null ? createdByUser.getFullName() : "Unknown User";
    }

    // Formatting methods
    public String getFormattedTotal() {
        return String.format("Rs. %.2f", totalAmount);
    }

    public String getFormattedSubtotal() {
        return String.format("Rs. %.2f", subtotal);
    }

    public String getFormattedTax() {
        return String.format("Rs. %.2f", taxAmount);
    }

    public String getFormattedDiscount() {
        return String.format("Rs. %.2f", discountAmount);
    }

    public String getStatusDisplay() {
        return status != null ? status.toString() : "UNKNOWN";
    }

    public String getPaymentMethodDisplay() {
        return paymentMethod != null ? paymentMethod.toString() : "UNKNOWN";
    }

    // Validation methods
    public boolean isValidForSaving() {
        return billNumber != null && !billNumber.trim().isEmpty() &&
                customerAccountNumber != null && !customerAccountNumber.trim().isEmpty() &&
                createdBy != null && !createdBy.trim().isEmpty() &&
                !billItems.isEmpty() &&
                totalAmount != null && totalAmount.compareTo(BigDecimal.ZERO) >= 0;
    }

    public boolean hasItems() {
        return !billItems.isEmpty();
    }

    public boolean hasValidTotal() {
        return totalAmount != null && totalAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    // Override toString for debugging
    @Override
    public String toString() {
        return String.format("Bill{billNumber='%s', customer='%s', total=%s, status=%s, items=%d}",
                billNumber, customerAccountNumber, totalAmount, status, billItems.size());
    }

    // Override equals and hashCode
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Bill bill = (Bill) obj;
        return billNumber != null ? billNumber.equals(bill.billNumber) : bill.billNumber == null;
    }

    @Override
    public int hashCode() {
        return billNumber != null ? billNumber.hashCode() : 0;
    }
}