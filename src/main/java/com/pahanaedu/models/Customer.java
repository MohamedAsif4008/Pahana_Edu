package com.pahanaedu.models;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Customer model class
 * Represents customers in the Pahana Edu billing system
 *
 * @author Pahana Edu Development Team
 * @version 1.0
 */
public class Customer {

    // Private fields
    private String accountNumber;
    private String name;
    private String address;
    private String phoneNumber;
    private String email;
    private BigDecimal creditLimit;
    private boolean isActive;
    private Timestamp registrationDate;
    private Timestamp updatedDate;

    // Default constructor
    public Customer() {
        this.creditLimit = BigDecimal.ZERO;
        this.isActive = true;
        this.registrationDate = new Timestamp(System.currentTimeMillis());
        this.updatedDate = new Timestamp(System.currentTimeMillis());
    }

    // Constructor with essential fields
    public Customer(String accountNumber, String name) {
        this();
        this.accountNumber = accountNumber;
        this.name = name;
    }

    // Full constructor
    public Customer(String accountNumber, String name, String address, String phoneNumber, String email) {
        this(accountNumber, name);
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    // Getters and Setters
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Timestamp getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Timestamp registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Timestamp getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Timestamp updatedDate) {
        this.updatedDate = updatedDate;
    }

    // Business logic methods
    public boolean hasValidContact() {
        return (phoneNumber != null && !phoneNumber.trim().isEmpty()) ||
                (email != null && !email.trim().isEmpty());
    }

    public boolean hasEmailContact() {
        return email != null && !email.trim().isEmpty() && email.contains("@");
    }

    public boolean hasPhoneContact() {
        return phoneNumber != null && !phoneNumber.trim().isEmpty();
    }

    public String getInitials() {
        if (name == null || name.trim().isEmpty()) {
            return "??";
        }

        String[] names = name.trim().split("\\s+");
        StringBuilder initials = new StringBuilder();

        for (String n : names) {
            if (!n.isEmpty()) {
                initials.append(n.charAt(0));
                if (initials.length() >= 2) break;
            }
        }

        return initials.toString().toUpperCase();
    }

    public String getDisplayName() {
        return name != null ? name : "Unknown Customer";
    }

    public String getContactInfo() {
        StringBuilder contact = new StringBuilder();

        if (hasPhoneContact()) {
            contact.append("Phone: ").append(phoneNumber);
        }

        if (hasEmailContact()) {
            if (contact.length() > 0) contact.append(" | ");
            contact.append("Email: ").append(email);
        }

        return contact.length() > 0 ? contact.toString() : "No contact info";
    }

    // Validation methods
    public boolean isValidForBilling() {
        return accountNumber != null && !accountNumber.trim().isEmpty() &&
                name != null && !name.trim().isEmpty() &&
                isActive;
    }

    public boolean hasCreditLimit() {
        return creditLimit != null && creditLimit.compareTo(BigDecimal.ZERO) > 0;
    }

    // Override toString for debugging
    @Override
    public String toString() {
        return String.format("Customer{accountNumber='%s', name='%s', phone='%s', email='%s', isActive=%s}",
                accountNumber, name, phoneNumber, email, isActive);
    }

    // Override equals and hashCode
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Customer customer = (Customer) obj;
        return accountNumber != null ? accountNumber.equals(customer.accountNumber) : customer.accountNumber == null;
    }

    @Override
    public int hashCode() {
        return accountNumber != null ? accountNumber.hashCode() : 0;
    }
}