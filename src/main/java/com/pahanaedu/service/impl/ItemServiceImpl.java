package com.pahanaedu.service.impl;

import com.pahanaedu.dao.ItemDAO;
import com.pahanaedu.models.Item;
import com.pahanaedu.service.interfaces.ItemService;
import com.pahanaedu.util.ValidationUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for Item management operations
 * Implements business logic for inventory management
 *
 * Design Patterns Used:
 * - Service Layer Pattern: Encapsulates business logic
 * - Dependency Injection: Uses DAO for data access
 * - Strategy Pattern: Different validation strategies
 * - Business Rules Pattern: Encapsulates inventory rules
 *
 * @author Pahana Edu Development Team
 * @version 1.0
 */
public class ItemServiceImpl implements ItemService {

    private final ItemDAO itemDAO;

    // Business constants
    private static final BigDecimal MIN_PRICE = new BigDecimal("0.01");
    private static final BigDecimal MAX_PRICE = new BigDecimal("999999.99");
    private static final int MIN_STOCK = 0;
    private static final int MAX_STOCK = 999999;
    private static final int DEFAULT_REORDER_LEVEL = 10;

    /**
     * Constructor with dependency injection
     */
    public ItemServiceImpl() {
        this.itemDAO = new ItemDAO();
    }

    /**
     * Constructor for testing with DAO injection
     */
    public ItemServiceImpl(ItemDAO itemDAO) {
        this.itemDAO = itemDAO;
    }

    @Override
    public boolean createItem(Item item) {
        // Validate item data
        if (!validateItem(item)) {
            return false;
        }

        // Check if item ID already exists
        if (!isItemIdAvailable(item.getItemId(), null)) {
            System.err.println("Item ID already exists: " + item.getItemId());
            return false;
        }

        // Generate item ID if not provided
        if (item.getItemId() == null || item.getItemId().trim().isEmpty()) {
            item.setItemId(generateNextItemId());
        }

        // Set default values
        setDefaultValues(item);

        try {
            boolean created = itemDAO.createItem(item);
            if (created) {
                System.out.println("Item created successfully: " + item.getItemId());
            }
            return created;
        } catch (Exception e) {
            System.err.println("Error creating item: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateItem(Item item) {
        // Validate item data for update
        if (!validateItemForUpdate(item)) {
            return false;
        }

        // Verify item exists
        Item existingItem = findItemById(item.getItemId());
        if (existingItem == null) {
            System.err.println("Item not found: " + item.getItemId());
            return false;
        }

        try {
            boolean updated = itemDAO.updateItem(item);
            if (updated) {
                System.out.println("Item updated successfully: " + item.getItemId());
            }
            return updated;
        } catch (Exception e) {
            System.err.println("Error updating item: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deactivateItem(String itemId) {
        if (!ValidationUtils.isNotEmpty(itemId)) {
            System.err.println("Item ID is required");
            return false;
        }

        // Verify item exists and is active
        Item item = findItemById(itemId);
        if (item == null) {
            System.err.println("Item not found: " + itemId);
            return false;
        }

        if (!item.isActive()) {
            System.err.println("Item is already inactive: " + itemId);
            return false;
        }

        try {
            boolean deactivated = itemDAO.deactivateItem(itemId);
            if (deactivated) {
                System.out.println("Item deactivated successfully: " + itemId);
            }
            return deactivated;
        } catch (Exception e) {
            System.err.println("Error deactivating item: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Item findItemById(String itemId) {
        if (!ValidationUtils.isNotEmpty(itemId)) {
            return null;
        }

        try {
            return itemDAO.findByItemId(itemId);
        } catch (Exception e) {
            System.err.println("Error finding item by ID: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Item> searchItemsByName(String name) {
        if (!ValidationUtils.isNotEmpty(name)) {
            System.err.println("Search name is required");
            return List.of();
        }

        // Sanitize search input
        String sanitizedName = ValidationUtils.sanitizeInput(name);
        if (!ValidationUtils.isNotEmpty(sanitizedName)) {
            System.err.println("Invalid search name after sanitization");
            return List.of();
        }

        try {
            return itemDAO.searchByName(sanitizedName);
        } catch (Exception e) {
            System.err.println("Error searching items by name: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<Item> getItemsByCategory(String category) {
        if (!ValidationUtils.isValidCategory(category)) {
            System.err.println("Valid category is required");
            return List.of();
        }

        try {
            return itemDAO.getItemsByCategory(category);
        } catch (Exception e) {
            System.err.println("Error getting items by category: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<Item> getAllActiveItems() {
        try {
            return itemDAO.getAllActiveItems();
        } catch (Exception e) {
            System.err.println("Error getting all active items: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<Item> getItemsNeedingReorder() {
        try {
            return itemDAO.getItemsNeedingReorder();
        } catch (Exception e) {
            System.err.println("Error getting items needing reorder: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<String> getAllCategories() {
        try {
            return itemDAO.getAllCategories();
        } catch (Exception e) {
            System.err.println("Error getting all categories: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public boolean updateStock(String itemId, int newQuantity) {
        // Validate inputs
        if (!ValidationUtils.isNotEmpty(itemId)) {
            System.err.println("Item ID is required");
            return false;
        }

        if (!ValidationUtils.isInRange(newQuantity, MIN_STOCK, MAX_STOCK)) {
            System.err.println("Stock quantity must be between " + MIN_STOCK + " and " + MAX_STOCK);
            return false;
        }

        // Verify item exists
        Item item = findItemById(itemId);
        if (item == null) {
            System.err.println("Item not found: " + itemId);
            return false;
        }

        try {
            boolean updated = itemDAO.updateStock(itemId, newQuantity);
            if (updated) {
                System.out.println("Stock updated for item " + itemId + ": " + newQuantity);
            }
            return updated;
        } catch (Exception e) {
            System.err.println("Error updating stock: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean addStock(String itemId, int quantityToAdd) {
        if (!ValidationUtils.isPositive(quantityToAdd)) {
            System.err.println("Quantity to add must be positive");
            return false;
        }

        Item item = findItemById(itemId);
        if (item == null) {
            System.err.println("Item not found: " + itemId);
            return false;
        }

        int newQuantity = item.getStockQuantity() + quantityToAdd;

        // Check for overflow
        if (newQuantity > MAX_STOCK) {
            System.err.println("Adding stock would exceed maximum stock limit");
            return false;
        }

        return updateStock(itemId, newQuantity);
    }

    @Override
    public boolean reduceStock(String itemId, int quantityToReduce) {
        if (!ValidationUtils.isPositive(quantityToReduce)) {
            System.err.println("Quantity to reduce must be positive");
            return false;
        }

        Item item = findItemById(itemId);
        if (item == null) {
            System.err.println("Item not found: " + itemId);
            return false;
        }

        if (!isAvailableForSale(itemId, quantityToReduce)) {
            System.err.println("Insufficient stock for item: " + itemId);
            return false;
        }

        try {
            boolean reduced = itemDAO.reduceStock(itemId, quantityToReduce);
            if (reduced) {
                System.out.println("Stock reduced for item " + itemId + ": -" + quantityToReduce);
            }
            return reduced;
        } catch (Exception e) {
            System.err.println("Error reducing stock: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updatePrice(String itemId, BigDecimal newPrice) {
        // Validate inputs
        if (!ValidationUtils.isNotEmpty(itemId)) {
            System.err.println("Item ID is required");
            return false;
        }

        if (!ValidationUtils.isInRange(newPrice, MIN_PRICE, MAX_PRICE)) {
            System.err.println("Price must be between " + MIN_PRICE + " and " + MAX_PRICE);
            return false;
        }

        // Verify item exists
        Item item = findItemById(itemId);
        if (item == null) {
            System.err.println("Item not found: " + itemId);
            return false;
        }

        item.setPrice(newPrice);
        return updateItem(item);
    }

    @Override
    public boolean validateItem(Item item) {
        if (item == null) {
            System.err.println("Item object is required");
            return false;
        }

        // Validate item ID format if provided
        if (item.getItemId() != null && !item.getItemId().trim().isEmpty()) {
            if (!ValidationUtils.isValidItemId(item.getItemId())) {
                System.err.println("Invalid item ID format: " + item.getItemId());
                return false;
            }
        }

        // Validate item name
        if (!ValidationUtils.isValidName(item.getName())) {
            System.err.println("Item name is required and must be valid");
            return false;
        }

        // Validate category
        if (!ValidationUtils.isValidCategory(item.getCategory())) {
            System.err.println("Valid category is required");
            return false;
        }

        // Validate price
        if (!ValidationUtils.isInRange(item.getPrice(), MIN_PRICE, MAX_PRICE)) {
            System.err.println("Price must be between " + MIN_PRICE + " and " + MAX_PRICE);
            return false;
        }

        // Validate stock quantity
        if (!ValidationUtils.isInRange(item.getStockQuantity(), MIN_STOCK, MAX_STOCK)) {
            System.err.println("Stock quantity must be between " + MIN_STOCK + " and " + MAX_STOCK);
            return false;
        }

        // Validate reorder level
        if (!ValidationUtils.isInRange(item.getReorderLevel(), 0, item.getStockQuantity())) {
            System.err.println("Reorder level must be between 0 and current stock quantity");
            return false;
        }

        // Validate description if provided
        if (!ValidationUtils.isValidDescription(item.getDescription())) {
            System.err.println("Invalid description format");
            return false;
        }

        return true;
    }

    /**
     * Validate item data for updates (item ID is required)
     */
    private boolean validateItemForUpdate(Item item) {
        if (item == null) {
            System.err.println("Item object is required");
            return false;
        }

        // Validate item ID is required for updates
        if (!ValidationUtils.isValidItemId(item.getItemId())) {
            System.err.println("Valid item ID is required for updates");
            return false;
        }

        // Use existing validation for other fields
        return validateItem(item);
    }

    /**
     * Set default values for new items
     */
    private void setDefaultValues(Item item) {
        if (item.getReorderLevel() <= 0) {
            item.setReorderLevel(DEFAULT_REORDER_LEVEL);
        }

        if (item.getStockQuantity() < 0) {
            item.setStockQuantity(0);
        }
    }

    @Override
    public boolean isItemIdAvailable(String itemId, String excludeItemId) {
        if (!ValidationUtils.isNotEmpty(itemId)) {
            return false;
        }

        try {
            return !itemDAO.isItemIdExists(itemId, excludeItemId);
        } catch (Exception e) {
            System.err.println("Error checking item ID availability: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String generateNextItemId() {
        try {
            return itemDAO.generateNextItemId();
        } catch (Exception e) {
            System.err.println("Error generating item ID: " + e.getMessage());
            return "ITEM001"; // Fallback default
        }
    }

    @Override
    public int getActiveItemCount() {
        try {
            return itemDAO.getActiveItemCount();
        } catch (Exception e) {
            System.err.println("Error getting active item count: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public boolean isAvailableForSale(String itemId, int requestedQuantity) {
        if (!ValidationUtils.isPositive(requestedQuantity)) {
            return false;
        }

        Item item = findItemById(itemId);
        if (item == null) {
            return false;
        }

        return isValidForSale(item, requestedQuantity);
    }

    @Override
    public List<Item> getLowStockItems() {
        return getItemsNeedingReorder();
    }

    @Override
    public List<Item> getOutOfStockItems() {
        try {
            return getAllActiveItems().stream()
                    .filter(item -> item.getStockQuantity() == 0)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting out of stock items: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public BigDecimal calculateTotalInventoryValue() {
        try {
            List<Item> allItems = getAllActiveItems();
            return allItems.stream()
                    .map(Item::getStockValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } catch (Exception e) {
            System.err.println("Error calculating total inventory value: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    @Override
    public List<Item> getItemsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        if (!ValidationUtils.isNonNegative(minPrice) || !ValidationUtils.isPositive(maxPrice)) {
            System.err.println("Invalid price range");
            return List.of();
        }

        if (minPrice.compareTo(maxPrice) > 0) {
            System.err.println("Minimum price cannot be greater than maximum price");
            return List.of();
        }

        try {
            return getAllActiveItems().stream()
                    .filter(item -> ValidationUtils.isInRange(item.getPrice(), minPrice, maxPrice))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting items by price range: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public boolean isValidForSale(Item item, int quantity) {
        if (item == null || !ValidationUtils.isPositive(quantity)) {
            return false;
        }

        // Check if item is active
        if (!item.isActive()) {
            return false;
        }

        // Check if item has sufficient stock
        if (!item.canSell(quantity)) {
            return false;
        }

        // Check if price is valid
        if (!item.hasValidPrice()) {
            return false;
        }

        return true;
    }

    /**
     * Business method: Get inventory summary for reporting
     *
     * @return Inventory summary string
     */
    public String getInventorySummary() {
        try {
            int totalItems = getActiveItemCount();
            int lowStockCount = getLowStockItems().size();
            int outOfStockCount = getOutOfStockItems().size();
            BigDecimal totalValue = calculateTotalInventoryValue();

            StringBuilder summary = new StringBuilder();
            summary.append("Inventory Summary:\n");
            summary.append("Total Active Items: ").append(totalItems).append("\n");
            summary.append("Low Stock Items: ").append(lowStockCount).append("\n");
            summary.append("Out of Stock Items: ").append(outOfStockCount).append("\n");
            summary.append("Total Inventory Value: Rs. ").append(totalValue).append("\n");

            return summary.toString();
        } catch (Exception e) {
            System.err.println("Error generating inventory summary: " + e.getMessage());
            return "Error generating inventory summary";
        }
    }

    /**
     * Business method: Check if item needs restocking
     *
     * @param itemId Item ID to check
     * @return true if item needs restocking
     */
    public boolean needsRestocking(String itemId) {
        Item item = findItemById(itemId);
        if (item == null) {
            return false;
        }

        return item.needsReorder();
    }

    /**
     * Business method: Get suggested reorder quantity
     *
     * @param itemId Item ID
     * @return Suggested reorder quantity
     */
    public int getSuggestedReorderQuantity(String itemId) {
        Item item = findItemById(itemId);
        if (item == null) {
            return 0;
        }

        // Business rule: Reorder to 3 times the reorder level
        int targetQuantity = item.getReorderLevel() * 3;
        return Math.max(0, targetQuantity - item.getStockQuantity());
    }
}