package com.pahanaedu.models;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Item model class
 * Represents books/products in the Pahana Edu inventory system
 *
 * @author Pahana Edu Development Team
 * @version 1.0
 */
public class Item {

    // Private fields
    private String itemId;
    private String name;
    private String category;
    private BigDecimal price;
    private int stockQuantity;
    private int reorderLevel;
    private String description;
    private boolean isActive;
    private Timestamp createdDate;
    private Timestamp updatedDate;

    // Default constructor
    public Item() {
        this.price = BigDecimal.ZERO;
        this.stockQuantity = 0;
        this.reorderLevel = 10;
        this.isActive = true;
        this.createdDate = new Timestamp(System.currentTimeMillis());
        this.updatedDate = new Timestamp(System.currentTimeMillis());
    }

    // Constructor with essential fields
    public Item(String itemId, String name, BigDecimal price) {
        this();
        this.itemId = itemId;
        this.name = name;
        this.price = price;
    }

    // Full constructor
    public Item(String itemId, String name, String category, BigDecimal price, int stockQuantity) {
        this(itemId, name, price);
        this.category = category;
        this.stockQuantity = stockQuantity;
    }

    // Getters and Setters
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public Timestamp getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Timestamp updatedDate) {
        this.updatedDate = updatedDate;
    }

    // Business logic methods
    public boolean isAvailable() {
        return isActive && stockQuantity > 0;
    }

    public boolean isInStock() {
        return stockQuantity > 0;
    }

    public boolean needsReorder() {
        return stockQuantity <= reorderLevel;
    }

    public boolean isOutOfStock() {
        return stockQuantity <= 0;
    }

    public boolean canSell(int quantity) {
        return isAvailable() && stockQuantity >= quantity;
    }

    // Calculate total value of stock
    public BigDecimal getStockValue() {
        return price.multiply(BigDecimal.valueOf(stockQuantity));
    }

    // Calculate line total for given quantity
    public BigDecimal calculateLineTotal(int quantity) {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    // Reduce stock quantity (for sales)
    public boolean reduceStock(int quantity) {
        if (canSell(quantity)) {
            this.stockQuantity -= quantity;
            this.updatedDate = new Timestamp(System.currentTimeMillis());
            return true;
        }
        return false;
    }

    // Increase stock quantity (for restocking)
    public void addStock(int quantity) {
        if (quantity > 0) {
            this.stockQuantity += quantity;
            this.updatedDate = new Timestamp(System.currentTimeMillis());
        }
    }

    // Get stock status description
    public String getStockStatus() {
        if (isOutOfStock()) {
            return "Out of Stock";
        } else if (needsReorder()) {
            return "Low Stock";
        } else {
            return "In Stock";
        }
    }

    public String getDisplayName() {
        return name != null ? name : "Unknown Item";
    }

    public String getCategoryDisplay() {
        return category != null ? category : "Uncategorized";
    }

    // Get formatted price
    public String getFormattedPrice() {
        return String.format("Rs. %.2f", price);
    }

    // Validation methods
    public boolean isValidForSale() {
        return itemId != null && !itemId.trim().isEmpty() &&
                name != null && !name.trim().isEmpty() &&
                price != null && price.compareTo(BigDecimal.ZERO) > 0 &&
                isActive;
    }

    public boolean hasValidPrice() {
        return price != null && price.compareTo(BigDecimal.ZERO) > 0;
    }

    // Override toString for debugging
    @Override
    public String toString() {
        return String.format("Item{itemId='%s', name='%s', category='%s', price=%s, stock=%d, isActive=%s}",
                itemId, name, category, price, stockQuantity, isActive);
    }

    // Override equals and hashCode
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Item item = (Item) obj;
        return itemId != null ? itemId.equals(item.itemId) : item.itemId == null;
    }

    @Override
    public int hashCode() {
        return itemId != null ? itemId.hashCode() : 0;
    }
}