package com.pahanaedu.models;

import java.sql.Timestamp;

/**
 * Base User model class
 * Represents users in the Pahana Edu system (Admin and Staff)
 *
 * Design Patterns:
 * - Template Pattern: Base class for Admin and Staff
 * - Bean Pattern: Standard Java Bean with getters/setters
 *
 * @author Pahana Edu Development Team
 * @version 1.0
 */
public abstract class User {

    // Enum for user roles
    public enum Role {
        ADMIN, STAFF
    }

    // Private fields (encapsulation)
    private String userId;
    private String username;
    private String password;
    private Role role;
    private String fullName;
    private String email;
    private String phoneNumber;
    private boolean isActive;
    private Timestamp createdDate;
    private Timestamp lastLogin;

    // Default constructor
    public User() {
        this.isActive = true;
        this.createdDate = new Timestamp(System.currentTimeMillis());
    }

    // Constructor with essential fields
    public User(String userId, String username, String password, Role role, String fullName) {
        this();
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
    }

    // Abstract method to be implemented by subclasses
    public abstract String getDisplayRole();

    // Abstract method for role-specific permissions
    public abstract boolean hasPermission(String permission);

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    // Utility methods
    public boolean isAdmin() {
        return Role.ADMIN.equals(this.role);
    }

    public boolean isStaff() {
        return Role.STAFF.equals(this.role);
    }

    // Business logic methods
    public boolean canLogin() {
        return isActive && username != null && password != null;
    }

    public String getInitials() {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "??";
        }

        String[] names = fullName.trim().split("\\s+");
        StringBuilder initials = new StringBuilder();

        for (String name : names) {
            if (!name.isEmpty()) {
                initials.append(name.charAt(0));
                if (initials.length() >= 2) break; // Limit to 2 initials
            }
        }

        return initials.toString().toUpperCase();
    }

    // Override toString for debugging
    @Override
    public String toString() {
        return String.format("User{userId='%s', username='%s', role=%s, fullName='%s', isActive=%s}",
                userId, username, role, fullName, isActive);
    }

    // Override equals and hashCode for proper object comparison
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        User user = (User) obj;
        return userId != null ? userId.equals(user.userId) : user.userId == null;
    }

    @Override
    public int hashCode() {
        return userId != null ? userId.hashCode() : 0;
    }
}