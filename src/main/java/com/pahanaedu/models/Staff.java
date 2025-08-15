package com.pahanaedu.models;

/**
 * Staff user model class
 * Represents staff members in the Pahana Edu system
 * Inherits from User and adds staff-specific functionality
 *
 * @author Pahana Edu Development Team
 * @version 1.0
 */
public class Staff extends User {

    // Staff-specific permissions (limited compared to Admin)
    private static final String[] STAFF_PERMISSIONS = {
            "CUSTOMER_MANAGEMENT",
            "ITEM_MANAGEMENT",
            "BILL_MANAGEMENT",
            "VIEW_CUSTOMER_DATA",
            "VIEW_ITEM_DATA",
            "CREATE_BILLS",
            "EDIT_CUSTOMER_INFO"
    };

    // Default constructor
    public Staff() {
        super();
        setRole(Role.STAFF);
    }

    // Constructor with essential fields
    public Staff(String userId, String username, String password, String fullName) {
        super(userId, username, password, Role.STAFF, fullName);
    }

    @Override
    public String getDisplayRole() {
        return "Staff Member";
    }

    @Override
    public boolean hasPermission(String permission) {
        // Staff have limited permissions
        for (String staffPermission : STAFF_PERMISSIONS) {
            if (staffPermission.equals(permission)) {
                return true;
            }
        }
        return false;
    }

    // Staff-specific methods
    public boolean canManageCustomers() {
        return hasPermission("CUSTOMER_MANAGEMENT");
    }

    public boolean canManageItems() {
        return hasPermission("ITEM_MANAGEMENT");
    }

    public boolean canCreateBills() {
        return hasPermission("CREATE_BILLS");
    }

    public boolean canViewCustomerData() {
        return hasPermission("VIEW_CUSTOMER_DATA");
    }

    public boolean canEditCustomerInfo() {
        return hasPermission("EDIT_CUSTOMER_INFO");
    }

    // Staff cannot delete records (admin only)
    public boolean canDeleteRecords() {
        return false;
    }

    // Staff cannot access reports (admin only)
    public boolean canAccessReports() {
        return false;
    }

    // Staff cannot manage users (admin only)
    public boolean canManageUsers() {
        return false;
    }

    // Get all staff permissions
    public String[] getAllPermissions() {
        return STAFF_PERMISSIONS.clone();
    }

    // Check if staff can perform basic operations
    public boolean hasBasicAccess() {
        return isActive() && isStaff();
    }

    @Override
    public String toString() {
        return String.format("Staff{userId='%s', username='%s', fullName='%s', isActive=%s}",
                getUserId(), getUsername(), getFullName(), isActive());
    }
}