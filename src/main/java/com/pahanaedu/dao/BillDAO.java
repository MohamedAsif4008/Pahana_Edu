package com.pahanaedu.dao;

import com.pahanaedu.models.Bill;
import com.pahanaedu.models.BillItem;
import com.pahanaedu.models.Customer;
import com.pahanaedu.models.Item;
import com.pahanaedu.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Bill management
 * Handles all database operations related to bills and bill items
 *
 * Design Patterns Used:
 * - DAO Pattern: Encapsulates database access logic
 * - Transaction Pattern: Ensures data consistency for bill operations
 *
 * @author Pahana Edu Development Team
 * @version 1.0
 */
public class BillDAO {

    private final DatabaseConnection dbConnection;
    private final CustomerDAO customerDAO;
    private final ItemDAO itemDAO;

    /**
     * Constructor - initialize with database connection and related DAOs
     */
    public BillDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.customerDAO = new CustomerDAO();
        this.itemDAO = new ItemDAO();
    }

    /**
     * Create a new bill with its items (transaction)
     *
     * @param bill Bill object to create
     * @return true if creation successful, false otherwise
     */
    public boolean createBill(Bill bill) {
        System.out.println("=== BillDAO.createBill() called ===");
        System.out.println("  Bill Number: " + bill.getBillNumber());
        System.out.println("  Customer: " + bill.getCustomerAccountNumber());
        System.out.println("  Total Items: " + bill.getBillItems().size());
        
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
            System.out.println("  Transaction started");

            // Insert bill header
            String billSql = """
                INSERT INTO bills (bill_number, customer_account_number, total_amount, 
                                 payment_method, bill_date, created_by, status, notes) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

            try (PreparedStatement billStmt = conn.prepareStatement(billSql)) {
                billStmt.setString(1, bill.getBillNumber());
                billStmt.setString(2, bill.getCustomerAccountNumber());
                billStmt.setBigDecimal(3, bill.getTotalAmount());
                billStmt.setString(4, bill.getPaymentMethod().name());
                billStmt.setTimestamp(5, bill.getBillDate());
                billStmt.setString(6, bill.getCreatedBy());
                billStmt.setString(7, bill.getStatus().name());
                billStmt.setString(8, bill.getNotes());

                System.out.println("  Inserting bill header...");
                int billResult = billStmt.executeUpdate();
                System.out.println("  Bill header insert result: " + billResult);

                // Insert bill items
                String itemSql = """
                    INSERT INTO bill_items (bill_number, item_id, quantity, unit_price, line_total) 
                    VALUES (?, ?, ?, ?, ?)
                    """;

                System.out.println("  Processing " + bill.getBillItems().size() + " bill items...");
                for (BillItem billItem : bill.getBillItems()) {
                    System.out.println("    Processing item: " + billItem.getItemId() + " (qty: " + billItem.getQuantity() + ")");
                    
                    try (PreparedStatement itemStmt = conn.prepareStatement(itemSql)) {
                        itemStmt.setString(1, bill.getBillNumber());
                        itemStmt.setString(2, billItem.getItemId());
                        itemStmt.setInt(3, billItem.getQuantity());
                        itemStmt.setBigDecimal(4, billItem.getUnitPrice());
                        itemStmt.setBigDecimal(5, billItem.getLineTotal());

                        System.out.println("    Inserting bill item...");
                        int itemResult = itemStmt.executeUpdate();
                        System.out.println("    Bill item insert result: " + itemResult);

                        // Reduce stock for each item
                        System.out.println("    Reducing stock for item: " + billItem.getItemId());
                        if (!itemDAO.reduceStock(billItem.getItemId(), billItem.getQuantity(), conn)) {
                            System.err.println("    Failed to reduce stock for item: " + billItem.getItemId());
                            throw new SQLException("Failed to reduce stock for item: " + billItem.getItemId());
                        }
                        System.out.println("    Stock reduced successfully");
                    }
                }
            }

            System.out.println("  Committing transaction...");
            conn.commit(); // Commit transaction
            System.out.println("  Transaction committed successfully");
            return true;

        } catch (SQLException e) {
            System.err.println("Error creating bill: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            if (conn != null) {
                try {
                    System.out.println("  Rolling back transaction...");
                    conn.rollback(); // Rollback on error
                    System.out.println("  Transaction rolled back");
                } catch (SQLException rollbackEx) {
                    System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit
                    DatabaseConnection.closeConnection(conn);
                    System.out.println("  Connection closed");
                } catch (SQLException e) {
                    System.err.println("Error resetting connection: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Find bill by bill number
     *
     * @param billNumber Bill number to search for
     * @return Bill object if found, null otherwise
     */
    public Bill findByBillNumber(String billNumber) {
        String sql = """
            SELECT bill_number, customer_account_number, total_amount, payment_method, 
                   bill_date, created_by, status, notes 
            FROM bills 
            WHERE bill_number = ?
            """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, billNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Bill bill = createBillFromResultSet(rs);
                    // Load bill items
                    bill.setBillItems(getBillItems(billNumber));
                    // Load customer info
                    Customer customer = customerDAO.findByAccountNumber(bill.getCustomerAccountNumber());
                    bill.setCustomer(customer);
                    return bill;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding bill by number: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get bills by customer account number
     *
     * @param customerAccountNumber Customer account number
     * @return List of bills for the customer
     */
    public List<Bill> getBillsByCustomer(String customerAccountNumber) {
        List<Bill> bills = new ArrayList<>();
        String sql = """
            SELECT bill_number, customer_account_number, total_amount, payment_method, 
                   bill_date, created_by, status, notes 
            FROM bills 
            WHERE customer_account_number = ? 
            ORDER BY bill_date DESC
            """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customerAccountNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Bill bill = createBillFromResultSet(rs);
                    bill.setBillItems(getBillItems(bill.getBillNumber()));
                    bills.add(bill);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting bills by customer: " + e.getMessage());
            e.printStackTrace();
        }

        return bills;
    }

    /**
     * Get all bills with pagination
     *
     * @param limit Number of records to return
     * @param offset Number of records to skip
     * @return List of bills
     */
    public List<Bill> getAllBills(int limit, int offset) {
        List<Bill> bills = new ArrayList<>();
        String sql = """
            SELECT bill_number, customer_account_number, total_amount, payment_method, 
                   bill_date, created_by, status, notes 
            FROM bills 
            ORDER BY bill_date DESC 
            LIMIT ? OFFSET ?
            """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            stmt.setInt(2, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Bill bill = createBillFromResultSet(rs);
                    bill.setBillItems(getBillItems(bill.getBillNumber()));
                    bills.add(bill);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting all bills: " + e.getMessage());
            e.printStackTrace();
        }

        return bills;
    }

    /**
     * Update bill status
     *
     * @param billNumber Bill number
     * @param status New status
     * @return true if update successful, false otherwise
     */
    public boolean updateBillStatus(String billNumber, Bill.Status status) {
        String sql = "UPDATE bills SET status = ? WHERE bill_number = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.toString());
            stmt.setString(2, billNumber);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating bill status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cancel bill and restore stock
     *
     * @param billNumber Bill number to cancel
     * @return true if cancellation successful, false otherwise
     */
    public boolean cancelBill(String billNumber) {
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false);

            // Get bill items before cancellation
            List<BillItem> billItems = getBillItems(billNumber);

            // Update bill status to cancelled
            String sql = "UPDATE bills SET status = 'CANCELLED' WHERE bill_number = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, billNumber);
                stmt.executeUpdate();
            }

            // Restore stock for each item
            for (BillItem billItem : billItems) {
                Item item = itemDAO.findByItemId(billItem.getItemId());
                if (item != null) {
                    int newStock = item.getStockQuantity() + billItem.getQuantity();
                    if (!itemDAO.updateStock(billItem.getItemId(), newStock)) {
                        throw new SQLException("Failed to restore stock for item: " + billItem.getItemId());
                    }
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error cancelling bill: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Error rolling back cancellation: " + rollbackEx.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    DatabaseConnection.closeConnection(conn);
                } catch (SQLException e) {
                    System.err.println("Error resetting connection: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Get bill items for a specific bill
     *
     * @param billNumber Bill number
     * @return List of bill items
     */
    public List<BillItem> getBillItems(String billNumber) {
        List<BillItem> billItems = new ArrayList<>();
        String sql = """
            SELECT bill_item_id, bill_number, item_id, quantity, unit_price, line_total 
            FROM bill_items 
            WHERE bill_number = ? 
            ORDER BY bill_item_id
            """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, billNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BillItem billItem = new BillItem();
                    billItem.setBillItemId(rs.getInt("bill_item_id"));
                    billItem.setBillNumber(rs.getString("bill_number"));
                    billItem.setItemId(rs.getString("item_id"));
                    billItem.setQuantity(rs.getInt("quantity"));
                    billItem.setUnitPrice(rs.getBigDecimal("unit_price"));
                    billItem.setLineTotal(rs.getBigDecimal("line_total"));

                    // Load item details
                    Item item = itemDAO.findByItemId(billItem.getItemId());
                    billItem.setItem(item);

                    billItems.add(billItem);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting bill items: " + e.getMessage());
            e.printStackTrace();
        }

        return billItems;
    }

    /**
     * Generate next bill number
     *
     * @return Next available bill number
     */
    public String generateNextBillNumber() {
        String sql = "SELECT MAX(CAST(SUBSTRING(bill_number, 5) AS UNSIGNED)) as max_id FROM bills WHERE bill_number LIKE 'BILL%'";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int maxId = rs.getInt("max_id");
                return String.format("BILL%06d", maxId + 1);
            }

        } catch (SQLException e) {
            System.err.println("Error generating bill number: " + e.getMessage());
        }

        return "BILL000001"; // Default if no bills exist
    }

    /**
     * Get bills by date range
     *
     * @param startDate Start date
     * @param endDate End date
     * @return List of bills in date range
     */
    public List<Bill> getBillsByDateRange(Date startDate, Date endDate) {
        List<Bill> bills = new ArrayList<>();
        String sql = """
            SELECT bill_number, customer_account_number, total_amount, payment_method, 
                   bill_date, created_by, status, notes 
            FROM bills 
            WHERE DATE(bill_date) BETWEEN ? AND ? 
            ORDER BY bill_date DESC
            """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Bill bill = createBillFromResultSet(rs);
                    bill.setBillItems(getBillItems(bill.getBillNumber()));
                    bills.add(bill);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting bills by date range: " + e.getMessage());
            e.printStackTrace();
        }

        return bills;
    }

    /**
     * Get total bill count
     *
     * @return Total number of bills
     */
    public int getTotalBillCount() {
        String sql = "SELECT COUNT(*) FROM bills";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting total bill count: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Create Bill object from ResultSet
     *
     * @param rs ResultSet containing bill data
     * @return Bill object
     * @throws SQLException if database error occurs
     */
    private Bill createBillFromResultSet(ResultSet rs) throws SQLException {
        Bill bill = new Bill();

        bill.setBillNumber(rs.getString("bill_number"));
        bill.setCustomerAccountNumber(rs.getString("customer_account_number"));
        bill.setTotalAmount(rs.getBigDecimal("total_amount"));
        bill.setPaymentMethod(Bill.PaymentMethod.valueOf(rs.getString("payment_method")));
        bill.setBillDate(rs.getTimestamp("bill_date"));
        bill.setCreatedBy(rs.getString("created_by"));
        bill.setStatus(Bill.Status.valueOf(rs.getString("status")));
        bill.setNotes(rs.getString("notes"));

        return bill;
    }
}