package com.pahanaedu.models;

/**
 * Admin user model class
 * Represents administrators in the Pahana Edu system
 * Inherits from User and adds admin-specific functionality
 *
 * @author Pahana Edu Development Team
 * @version 1.0
 */
public class Admin extends User {

    // Admin-specific permissions
    private static final String[] ADMIN_PERMISSIONS = {
            "USER_MANAGEMENT",
            "CUSTOMER_MANAGEMENT",
            "ITEM_MANAGEMENT",
            "BILL_MANAGEMENT",
            "REPORTS_ACCESS",
            "SYSTEM_SETTINGS",
            "VIEW_ALL_DATA",
            "DELETE_RECORDS",
            "EXPORT_DATA"
    };

    // Default constructor
    public Admin() {
        super();
        setRole(Role.ADMIN);
    }

    // Constructor with essential fields
    public Admin(String userId, String username, String password, String fullName) {
        super(userId, username, password, Role.ADMIN, fullName);
    }

    @Override
    public String getDisplayRole() {
        return "Administrator";
    }

    @Override
    public boolean hasPermission(String permission) {
        // Admins have all permissions
        for (String adminPermission : ADMIN_PERMISSIONS) {
            if (adminPermission.equals(permission)) {
                return true;
            }
        }
        return false;
    }

    // Admin-specific methods
    public boolean canManageUsers() {
        return hasPermission("USER_MANAGEMENT");
    }

    public boolean canAccessReports() {
        return hasPermission("REPORTS_ACCESS");
    }

    public boolean canDeleteRecords() {
        return hasPermission("DELETE_RECORDS");
    }

    public boolean canExportData() {
        return hasPermission("EXPORT_DATA");
    }

    public boolean canManageSystemSettings() {
        return hasPermission("SYSTEM_SETTINGS");
    }

    // Get all admin permissions
    public String[] getAllPermissions() {
        return ADMIN_PERMISSIONS.clone();
    }

    // Check if admin has full access
    public boolean hasFullAccess() {
        return isActive() && isAdmin();
    }

    @Override
    public String toString() {
        return String.format("Admin{userId='%s', username='%s', fullName='%s', isActive=%s}",
                getUserId(), getUsername(), getFullName(), isActive());
    }
}