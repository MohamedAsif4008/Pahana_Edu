-- Pahana Edu Database Schema
-- MySQL Database Setup Script
-- Run this script in your Navicat connected to pahana_edu database

USE pahana_edu;

-- Drop existing tables if they exist (for clean setup)
DROP TABLE IF EXISTS bill_items;
DROP TABLE IF EXISTS bills;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS users;

-- ================================================
-- Users Table (Admin and Staff)
-- ================================================
CREATE TABLE users (
                       user_id VARCHAR(20) PRIMARY KEY COMMENT 'Unique user identifier',
                       username VARCHAR(50) UNIQUE NOT NULL COMMENT 'Login username',
                       password VARCHAR(500) NOT NULL COMMENT 'Hashed password with salt',
                       role ENUM('ADMIN', 'STAFF') NOT NULL COMMENT 'User role for access control',
                       full_name VARCHAR(100) NOT NULL COMMENT 'Full name of user',
                       email VARCHAR(100) COMMENT 'Email address',
                       phone_number VARCHAR(20) COMMENT 'Contact phone number',
                       is_active BOOLEAN DEFAULT TRUE COMMENT 'Account status',
                       created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Account creation date',
                       last_login TIMESTAMP NULL COMMENT 'Last login timestamp',

    -- Indexes for performance
                       INDEX idx_username (username),
                       INDEX idx_role (role),
                       INDEX idx_active (is_active)
) ENGINE=InnoDB COMMENT='System users (Admin and Staff)';

-- ================================================
-- Customers Table
-- ================================================
CREATE TABLE customers (
                           account_number VARCHAR(20) PRIMARY KEY COMMENT 'Unique customer account number',
                           name VARCHAR(100) NOT NULL COMMENT 'Customer full name',
                           address TEXT COMMENT 'Customer address',
                           phone_number VARCHAR(20) COMMENT 'Contact phone number',
                           email VARCHAR(100) COMMENT 'Email address',
                           credit_limit DECIMAL(10,2) DEFAULT 0.00 COMMENT 'Credit limit amount',
                           is_active BOOLEAN DEFAULT TRUE COMMENT 'Account status',
                           registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Registration date',
                           updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update date',

    -- Indexes for performance
                           INDEX idx_customer_name (name),
                           INDEX idx_customer_phone (phone_number),
                           INDEX idx_customer_active (is_active)
) ENGINE=InnoDB COMMENT='Customer information';

-- ================================================
-- Items Table (Books and Products)
-- ================================================
CREATE TABLE items (
                       item_id VARCHAR(20) PRIMARY KEY COMMENT 'Unique item identifier',
                       name VARCHAR(100) NOT NULL COMMENT 'Item name',
                       category VARCHAR(50) COMMENT 'Item category',
                       price DECIMAL(10,2) NOT NULL COMMENT 'Unit price',
                       stock_quantity INT DEFAULT 0 COMMENT 'Available stock quantity',
                       reorder_level INT DEFAULT 10 COMMENT 'Minimum stock level for reordering',
                       description TEXT COMMENT 'Item description',
                       is_active BOOLEAN DEFAULT TRUE COMMENT 'Item status',
                       created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Item creation date',
                       updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update date',

    -- Indexes for performance
                       INDEX idx_item_name (name),
                       INDEX idx_item_category (category),
                       INDEX idx_stock_quantity (stock_quantity),
                       INDEX idx_item_active (is_active)
) ENGINE=InnoDB COMMENT='Product/book inventory';

-- ================================================
-- Bills Table
-- ================================================
CREATE TABLE bills (
                       bill_number VARCHAR(20) PRIMARY KEY COMMENT 'Unique bill identifier',
                       customer_account_number VARCHAR(20) NOT NULL COMMENT 'Customer account reference',
                       total_amount DECIMAL(10,2) NOT NULL COMMENT 'Total bill amount',
                       payment_method ENUM('CASH', 'CARD') NOT NULL COMMENT 'Payment method used',
                       bill_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Bill generation date',
                       created_by VARCHAR(20) NOT NULL COMMENT 'User who created the bill',
                       status ENUM('PENDING', 'PAID', 'CANCELLED') DEFAULT 'PAID' COMMENT 'Bill status',
                       notes TEXT COMMENT 'Additional notes',

    -- Foreign key constraints
                       FOREIGN KEY (customer_account_number) REFERENCES customers(account_number)
                           ON UPDATE CASCADE ON DELETE RESTRICT,
                       FOREIGN KEY (created_by) REFERENCES users(user_id)
                           ON UPDATE CASCADE ON DELETE RESTRICT,

    -- Indexes for performance
                       INDEX idx_bill_date (bill_date),
                       INDEX idx_bill_customer (customer_account_number),
                       INDEX idx_bill_created_by (created_by),
                       INDEX idx_bill_status (status)
) ENGINE=InnoDB COMMENT='Customer bills/invoices';

-- ================================================
-- Bill Items Table (Bill Line Items)
-- ================================================
CREATE TABLE bill_items (
                            bill_item_id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Unique bill item identifier',
                            bill_number VARCHAR(20) NOT NULL COMMENT 'Bill reference',
                            item_id VARCHAR(20) NOT NULL COMMENT 'Item reference',
                            quantity INT NOT NULL COMMENT 'Quantity purchased',
                            unit_price DECIMAL(10,2) NOT NULL COMMENT 'Unit price at time of sale',
                            line_total DECIMAL(10,2) NOT NULL COMMENT 'Line total (quantity * unit_price)',

    -- Foreign key constraints
                            FOREIGN KEY (bill_number) REFERENCES bills(bill_number)
                                ON UPDATE CASCADE ON DELETE CASCADE,
                            FOREIGN KEY (item_id) REFERENCES items(item_id)
                                ON UPDATE CASCADE ON DELETE RESTRICT,

    -- Indexes for performance
                            INDEX idx_bill_items_bill (bill_number),
                            INDEX idx_bill_items_item (item_id)
) ENGINE=InnoDB COMMENT='Individual items in each bill';

-- ================================================
-- Insert Initial Test Data
-- ================================================

-- Insert test users (passwords will be updated after running password hash)
INSERT INTO users (user_id, username, password, role, full_name, email) VALUES
                                                                            ('USR001', 'admin', 'temp_password_to_be_updated', 'ADMIN', 'System Administrator', 'admin@pahanaedu.com'),
                                                                            ('USR002', 'staff1', 'temp_password_to_be_updated', 'STAFF', 'John Staff', 'john.staff@pahanaedu.com'),
                                                                            ('USR003', 'staff2', 'temp_password_to_be_updated', 'STAFF', 'Jane Staff', 'jane.staff@pahanaedu.com');

-- Insert test customers
INSERT INTO customers (account_number, name, address, phone_number, email) VALUES
                                                                               ('ACC00001', 'John Doe', '123 Main Street, Colombo 01', '+94771234567', 'john.doe@email.com'),
                                                                               ('ACC00002', 'Jane Smith', '456 Park Avenue, Colombo 02', '+94779876543', 'jane.smith@email.com'),
                                                                               ('ACC00003', 'Mike Johnson', '789 Lake Road, Kandy', '+94711122334', 'mike.johnson@email.com'),
                                                                               ('ACC00004', 'Sarah Wilson', '321 Hill Street, Galle', '+94765555666', 'sarah.wilson@email.com'),
                                                                               ('ACC00005', 'David Brown', '654 Beach Road, Negombo', '+94777888999', 'david.brown@email.com');

-- Insert test items
INSERT INTO items (item_id, name, category, price, stock_quantity, description) VALUES
                                                                                    ('ITM001', 'Introduction to Java Programming', 'Programming', 2500.00, 50, 'Comprehensive guide to Java programming for beginners'),
                                                                                    ('ITM002', 'Database Design Fundamentals', 'Database', 3000.00, 30, 'Complete guide to database design and normalization'),
                                                                                    ('ITM003', 'Web Development with HTML/CSS', 'Web Development', 2200.00, 40, 'Modern web development techniques and best practices'),
                                                                                    ('ITM004', 'Advanced Java Concepts', 'Programming', 3500.00, 25, 'Advanced Java programming concepts and design patterns'),
                                                                                    ('ITM005', 'Software Engineering Principles', 'Engineering', 4000.00, 20, 'Software engineering methodologies and project management'),
                                                                                    ('ITM006', 'Mobile App Development', 'Mobile', 3200.00, 35, 'Cross-platform mobile application development'),
                                                                                    ('ITM007', 'Data Structures and Algorithms', 'Programming', 2800.00, 45, 'Essential data structures and algorithm concepts'),
                                                                                    ('ITM008', 'System Analysis and Design', 'Analysis', 3300.00, 28, 'System analysis methodologies and design patterns');

-- Note: You'll need to update the user passwords after implementing the PasswordUtil class
-- The passwords above are temporary and should be replaced with properly hashed versions