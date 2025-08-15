package com.pahanaedu.models;

import java.math.BigDecimal;

/**
 * BillItem model class
 * Represents individual line items in a bill/invoice
 * Links Bills with Items and stores quantity/pricing information
 *
 * @author Pahana Edu Development Team
 * @version 1.0
 */
public class BillItem {

    // Private fields
    private int billItemId;
    private String billNumber;
    private String itemId;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;

    // Related objects
    private Item item;

    // Default constructor
    public BillItem() {
        this.quantity = 0;
        this.unitPrice = BigDecimal.ZERO;
        this.lineTotal = BigDecimal.ZERO;
    }

    // Constructor with essential fields
    public BillItem(String billNumber, String itemId, int quantity, BigDecimal unitPrice) {
        this();
        this.billNumber = billNumber;
        this.itemId = itemId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        calculateLineTotal();
    }

    // Constructor with Item object
    public BillItem(String billNumber, Item item, int quantity) {
        this();
        this.billNumber = billNumber;
        this.item = item;
        if (item != null) {
            this.itemId = item.getItemId();
            this.unitPrice = item.getPrice();
        }
        this.quantity = quantity;
        calculateLineTotal();
    }

    // Getters and Setters
    public int getBillItemId() {
        return billItemId;
    }

    public void setBillItemId(int billItemId) {
        this.billItemId = billItemId;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        calculateLineTotal();
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateLineTotal();
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
        if (item != null) {
            this.itemId = item.getItemId();
            // Only update unit price if not already set
            if (this.unitPrice == null || this.unitPrice.compareTo(BigDecimal.ZERO) == 0) {
                this.unitPrice = item.getPrice();
                calculateLineTotal();
            }
        }
    }

    // Business logic methods

    /**
     * Calculate line total based on quantity and unit price
     */
    public void calculateLineTotal() {
        if (unitPrice != null && quantity > 0) {
            lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        } else {
            lineTotal = BigDecimal.ZERO;
        }
    }

    /**
     * Update quantity and recalculate total
     */
    public void updateQuantity(int newQuantity) {
        if (newQuantity >= 0) {
            this.quantity = newQuantity;
            calculateLineTotal();
        }
    }

    /**
     * Update unit price and recalculate total
     */
    public void updateUnitPrice(BigDecimal newUnitPrice) {
        if (newUnitPrice != null && newUnitPrice.compareTo(BigDecimal.ZERO) >= 0) {
            this.unitPrice = newUnitPrice;
            calculateLineTotal();
        }
    }

    /**
     * Apply discount to this line item
     */
    public void applyDiscount(BigDecimal discountAmount) {
        if (discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) >= 0) {
            lineTotal = lineTotal.subtract(discountAmount);
            if (lineTotal.compareTo(BigDecimal.ZERO) < 0) {
                lineTotal = BigDecimal.ZERO;
            }
        }
    }

    /**
     * Apply percentage discount to this line item
     */
    public void applyPercentageDiscount(double percentage) {
        if (percentage >= 0 && percentage <= 100) {
            BigDecimal originalTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
            BigDecimal discountPercent = BigDecimal.valueOf(percentage / 100);
            BigDecimal discountAmount = originalTotal.multiply(discountPercent);
            lineTotal = originalTotal.subtract(discountAmount);
        }
    }

    // Information methods
    public String getItemName() {
        return item != null ? item.getName() : "Unknown Item";
    }

    public String getItemCategory() {
        return item != null ? item.getCategoryDisplay() : "Unknown";
    }

    public boolean isItemAvailable() {
        return item != null && item.isAvailable();
    }

    public boolean canFulfillQuantity() {
        return item != null && item.canSell(quantity);
    }

    // Formatting methods
    public String getFormattedUnitPrice() {
        return String.format("Rs. %.2f", unitPrice);
    }

    public String getFormattedLineTotal() {
        return String.format("Rs. %.2f", lineTotal);
    }

    public String getQuantityDisplay() {
        return String.valueOf(quantity);
    }

    // Validation methods
    public boolean isValid() {
        return billNumber != null && !billNumber.trim().isEmpty() &&
                itemId != null && !itemId.trim().isEmpty() &&
                quantity > 0 &&
                unitPrice != null && unitPrice.compareTo(BigDecimal.ZERO) > 0 &&
                lineTotal != null && lineTotal.compareTo(BigDecimal.ZERO) >= 0;
    }

    public boolean hasValidQuantity() {
        return quantity > 0;
    }

    public boolean hasValidPrice() {
        return unitPrice != null && unitPrice.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasValidTotal() {
        return lineTotal != null && lineTotal.compareTo(BigDecimal.ZERO) >= 0;
    }

    // Comparison methods
    public boolean isSameItem(String otherItemId) {
        return itemId != null && itemId.equals(otherItemId);
    }

    public boolean isSameItem(Item otherItem) {
        return otherItem != null && isSameItem(otherItem.getItemId());
    }

    // Clone method for creating copies
    public BillItem clone() {
        BillItem clone = new BillItem();
        clone.setBillNumber(this.billNumber);
        clone.setItemId(this.itemId);
        clone.setQuantity(this.quantity);
        clone.setUnitPrice(this.unitPrice);
        clone.setLineTotal(this.lineTotal);
        clone.setItem(this.item);
        return clone;
    }

    // Static factory method
    public static BillItem createFromItem(String billNumber, Item item, int quantity) {
        if (item == null || quantity <= 0) {
            return null;
        }
        return new BillItem(billNumber, item, quantity);
    }

    // Override toString for debugging
    @Override
    public String toString() {
        return String.format("BillItem{billNumber='%s', itemId='%s', quantity=%d, unitPrice=%s, lineTotal=%s}",
                billNumber, itemId, quantity, unitPrice, lineTotal);
    }

    // Override equals and hashCode
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        BillItem billItem = (BillItem) obj;

        if (billItemId != 0 && billItem.billItemId != 0) {
            return billItemId == billItem.billItemId;
        }

        // If no ID, compare by bill number and item ID
        return billNumber != null && billNumber.equals(billItem.billNumber) &&
                itemId != null && itemId.equals(billItem.itemId);
    }

    @Override
    public int hashCode() {
        if (billItemId != 0) {
            return billItemId;
        }

        int result = billNumber != null ? billNumber.hashCode() : 0;
        result = 31 * result + (itemId != null ? itemId.hashCode() : 0);
        return result;
    }
}