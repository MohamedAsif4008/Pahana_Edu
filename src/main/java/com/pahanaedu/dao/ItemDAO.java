package com.pahanaedu.dao;

import com.pahanaedu.models.Item;
import com.pahanaedu.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Item management
 * Handles all database operations related to inventory items
 *
 * Design Patterns Used:
 * - DAO Pattern: Encapsulates database access logic
 * - Singleton Pattern: Uses DatabaseConnection singleton
 *
 * @author Pahana Edu Development Team
 * @version 1.0
 */
public class ItemDAO {

    private final DatabaseConnection dbConnection;

    /**
     * Constructor - initialize with database connection
     */
    public ItemDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Create a new item
     *
     * @param item Item object to create
     * @return true if creation successful, false otherwise
     */
    public boolean createItem(Item item) {
        String sql = """
            INSERT INTO items (item_id, name, category, price, stock_quantity, reorder_level, description, is_active) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getItemId());
            stmt.setString(2, item.getName());
            stmt.setString(3, item.getCategory());
            stmt.setBigDecimal(4, item.getPrice());
            stmt.setInt(5, item.getStockQuantity());
            stmt.setInt(6, item.getReorderLevel());
            stmt.setString(7, item.getDescription());
            stmt.setBoolean(8, item.isActive());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error creating item: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Find item by item ID
     *
     * @param itemId Item ID to search for
     * @return Item object if found, null otherwise
     */
    public Item findByItemId(String itemId) {
        String sql = """
            SELECT item_id, name, category, price, stock_quantity, reorder_level, description, 
                   is_active, created_date, updated_date 
            FROM items 
            WHERE item_id = ?
            """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, itemId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createItemFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding item by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Search items by name (partial match)
     *
     * @param name Name to search for
     * @return List of matching items
     */
    public List<Item> searchByName(String name) {
        List<Item> items = new ArrayList<>();
        String sql = """
            SELECT item_id, name, category, price, stock_quantity, reorder_level, description, 
                   is_active, created_date, updated_date 
            FROM items 
            WHERE name LIKE ? AND is_active = TRUE 
            ORDER BY name
            """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + name + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(createItemFromResultSet(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error searching items by name: " + e.getMessage());
            e.printStackTrace();
        }

        return items;
    }

    /**
     * Get items by category
     *
     * @param category Category to filter by
     * @return List of items in the category
     */
    public List<Item> getItemsByCategory(String category) {
        List<Item> items = new ArrayList<>();
        String sql = """
            SELECT item_id, name, category, price, stock_quantity, reorder_level, description, 
                   is_active, created_date, updated_date 
            FROM items 
            WHERE category = ? AND is_active = TRUE 
            ORDER BY name
            """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(createItemFromResultSet(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting items by category: " + e.getMessage());
            e.printStackTrace();
        }

        return items;
    }

    /**
     * Update an existing item
     *
     * @param item Item object with updated information
     * @return true if update successful, false otherwise
     */
    public boolean updateItem(Item item) {
        String sql = """
            UPDATE items 
            SET name = ?, category = ?, price = ?, stock_quantity = ?, reorder_level = ?, 
                description = ?, is_active = ?, updated_date = CURRENT_TIMESTAMP
            WHERE item_id = ?
            """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getName());
            stmt.setString(2, item.getCategory());
            stmt.setBigDecimal(3, item.getPrice());
            stmt.setInt(4, item.getStockQuantity());
            stmt.setInt(5, item.getReorderLevel());
            stmt.setString(6, item.getDescription());
            stmt.setBoolean(7, item.isActive());
            stmt.setString(8, item.getItemId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating item: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update item stock quantity
     *
     * @param itemId Item ID
     * @param newQuantity New stock quantity
     * @return true if update successful, false otherwise
     */
    public boolean updateStock(String itemId, int newQuantity) {
        String sql = "UPDATE items SET stock_quantity = ?, updated_date = CURRENT_TIMESTAMP WHERE item_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newQuantity);
            stmt.setString(2, itemId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating stock: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Reduce stock quantity for sales
     *
     * @param itemId Item ID
     * @param quantity Quantity to reduce
     * @return true if reduction successful, false otherwise
     */
    public boolean reduceStock(String itemId, int quantity) {
        String sql = """
            UPDATE items 
            SET stock_quantity = stock_quantity - ?, updated_date = CURRENT_TIMESTAMP 
            WHERE item_id = ? AND stock_quantity >= ?
            """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantity);
            stmt.setString(2, itemId);
            stmt.setInt(3, quantity);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error reducing stock: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deactivate item (soft delete)
     *
     * @param itemId Item ID to deactivate
     * @return true if deactivation successful, false otherwise
     */
    public boolean deactivateItem(String itemId) {
        String sql = "UPDATE items SET is_active = FALSE, updated_date = CURRENT_TIMESTAMP WHERE item_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, itemId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deactivating item: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all active items
     *
     * @return List of all active items
     */
    public List<Item> getAllActiveItems() {
        List<Item> items = new ArrayList<>();
        String sql = """
            SELECT item_id, name, category, price, stock_quantity, reorder_level, description, 
                   is_active, created_date, updated_date 
            FROM items 
            WHERE is_active = TRUE 
            ORDER BY name
            """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                items.add(createItemFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting all active items: " + e.getMessage());
            e.printStackTrace();
        }

        return items;
    }

    /**
     * Get items that need reordering
     *
     * @return List of items with stock below reorder level
     */
    public List<Item> getItemsNeedingReorder() {
        List<Item> items = new ArrayList<>();
        String sql = """
            SELECT item_id, name, category, price, stock_quantity, reorder_level, description, 
                   is_active, created_date, updated_date 
            FROM items 
            WHERE stock_quantity <= reorder_level AND is_active = TRUE 
            ORDER BY stock_quantity ASC
            """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                items.add(createItemFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting items needing reorder: " + e.getMessage());
            e.printStackTrace();
        }

        return items;
    }

    /**
     * Get distinct categories
     *
     * @return List of all distinct categories
     */
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM items WHERE is_active = TRUE ORDER BY category";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categories.add(rs.getString("category"));
            }

        } catch (SQLException e) {
            System.err.println("Error getting categories: " + e.getMessage());
            e.printStackTrace();
        }

        return categories;
    }

    /**
     * Check if item ID already exists
     *
     * @param itemId Item ID to check
     * @param excludeItemId Item ID to exclude from check (for updates)
     * @return true if item ID exists, false otherwise
     */
    public boolean isItemIdExists(String itemId, String excludeItemId) {
        String sql = "SELECT COUNT(*) FROM items WHERE item_id = ? AND item_id != ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, itemId);
            stmt.setString(2, excludeItemId != null ? excludeItemId : "");

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error checking item ID existence: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Generate next item ID
     *
     * @return Next available item ID
     */
    public String generateNextItemId() {
        String sql = "SELECT MAX(CAST(SUBSTRING(item_id, 5) AS UNSIGNED)) as max_id FROM items WHERE item_id LIKE 'ITEM%'";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int maxId = rs.getInt("max_id");
                return String.format("ITEM%03d", maxId + 1);
            }

        } catch (SQLException e) {
            System.err.println("Error generating item ID: " + e.getMessage());
        }

        return "ITEM001"; // Default if no items exist
    }

    /**
     * Get item count for reporting
     *
     * @return Total number of active items
     */
    public int getActiveItemCount() {
        String sql = "SELECT COUNT(*) FROM items WHERE is_active = TRUE";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting item count: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Create Item object from ResultSet
     *
     * @param rs ResultSet containing item data
     * @return Item object
     * @throws SQLException if database error occurs
     */
    private Item createItemFromResultSet(ResultSet rs) throws SQLException {
        Item item = new Item();

        item.setItemId(rs.getString("item_id"));
        item.setName(rs.getString("name"));
        item.setCategory(rs.getString("category"));
        item.setPrice(rs.getBigDecimal("price"));
        item.setStockQuantity(rs.getInt("stock_quantity"));
        item.setReorderLevel(rs.getInt("reorder_level"));
        item.setDescription(rs.getString("description"));
        item.setActive(rs.getBoolean("is_active"));
        item.setCreatedDate(rs.getTimestamp("created_date"));
        item.setUpdatedDate(rs.getTimestamp("updated_date"));

        return item;
    }
}