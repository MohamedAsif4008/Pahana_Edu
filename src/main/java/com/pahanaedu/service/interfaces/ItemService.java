package com.pahanaedu.service.interfaces;

import com.pahanaedu.models.Item;
import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for Item management operations
 * Defines business logic operations for inventory management
 *
 * Design Pattern: Service Layer Pattern
 * - Separates business logic from data access
 * - Provides transaction boundaries
 * - Encapsulates business rules
 *
 * @author Pahana Edu Development Team
 * @version 1.0
 */
public interface ItemService {

    /**
     * Create a new item with validation
     *
     * @param item Item object to create
     * @return true if creation successful, false otherwise
     */
    boolean createItem(Item item);

    /**
     * Update an existing item with validation
     *
     * @param item Item object with updated information
     * @return true if update successful, false otherwise
     */
    boolean updateItem(Item item);

    /**
     * Deactivate item (soft delete)
     *
     * @param itemId Item ID to deactivate
     * @return true if deactivation successful, false otherwise
     */
    boolean deactivateItem(String itemId);

    /**
     * Find item by item ID
     *
     * @param itemId Item ID to search for
     * @return Item object if found, null otherwise
     */
    Item findItemById(String itemId);

    /**
     * Search items by name (partial match)
     *
     * @param name Name to search for
     * @return List of matching items
     */
    List<Item> searchItemsByName(String name);

    /**
     * Get items by category
     *
     * @param category Category to filter by
     * @return List of items in the category
     */
    List<Item> getItemsByCategory(String category);

    /**
     * Get all active items
     *
     * @return List of all active items
     */
    List<Item> getAllActiveItems();

    /**
     * Get items that need reordering
     *
     * @return List of items with stock below reorder level
     */
    List<Item> getItemsNeedingReorder();

    /**
     * Get all distinct categories
     *
     * @return List of all distinct categories
     */
    List<String> getAllCategories();

    /**
     * Update item stock quantity
     *
     * @param itemId Item ID
     * @param newQuantity New stock quantity
     * @return true if update successful, false otherwise
     */
    boolean updateStock(String itemId, int newQuantity);

    /**
     * Add stock to existing quantity
     *
     * @param itemId Item ID
     * @param quantityToAdd Quantity to add
     * @return true if addition successful, false otherwise
     */
    boolean addStock(String itemId, int quantityToAdd);

    /**
     * Reduce stock quantity (for sales)
     *
     * @param itemId Item ID
     * @param quantityToReduce Quantity to reduce
     * @return true if reduction successful, false otherwise
     */
    boolean reduceStock(String itemId, int quantityToReduce);

    /**
     * Update item price with validation
     *
     * @param itemId Item ID
     * @param newPrice New price
     * @return true if update successful, false otherwise
     */
    boolean updatePrice(String itemId, BigDecimal newPrice);

    /**
     * Validate item data
     *
     * @param item Item object to validate
     * @return true if item data is valid, false otherwise
     */
    boolean validateItem(Item item);

    /**
     * Check if item ID is available
     *
     * @param itemId Item ID to check
     * @param excludeItemId Item ID to exclude from check (for updates)
     * @return true if item ID is available, false otherwise
     */
    boolean isItemIdAvailable(String itemId, String excludeItemId);

    /**
     * Generate next item ID
     *
     * @return Next available item ID
     */
    String generateNextItemId();

    /**
     * Get item count for reporting
     *
     * @return Total number of active items
     */
    int getActiveItemCount();

    /**
     * Check if item is available for sale
     *
     * @param itemId Item ID to check
     * @param requestedQuantity Requested quantity
     * @return true if item is available for the requested quantity
     */
    boolean isAvailableForSale(String itemId, int requestedQuantity);

    /**
     * Get low stock items (below reorder level)
     *
     * @return List of items with low stock
     */
    List<Item> getLowStockItems();

    /**
     * Get out of stock items
     *
     * @return List of items that are out of stock
     */
    List<Item> getOutOfStockItems();

    /**
     * Calculate total inventory value
     *
     * @return Total value of all items in inventory
     */
    BigDecimal calculateTotalInventoryValue();

    /**
     * Get items by price range
     *
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @return List of items within price range
     */
    List<Item> getItemsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Validate item for sale operations
     *
     * @param item Item to validate
     * @param quantity Quantity requested
     * @return true if item is valid for sale, false otherwise
     */
    boolean isValidForSale(Item item, int quantity);
}