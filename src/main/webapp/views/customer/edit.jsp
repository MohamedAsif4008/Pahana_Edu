<%--
  Created by IntelliJ IDEA.
  User: MOHAMED
  Date: 16/08/2025
  Time: 12:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Edit Customer" />
<jsp:include page="../common/header.jsp" />

<!-- Page Content -->
<div class="col-md-10 main-content p-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="h3">
            <i class="bi bi-pencil-square"></i> Edit Customer
        </h1>
        <div>
            <a href="${pageContext.request.contextPath}/customers?action=view&id=${customer.accountNumber}" class="btn btn-info me-2">
                <i class="bi bi-eye"></i> View Details
            </a>
            <a href="${pageContext.request.contextPath}/customers" class="btn btn-secondary">
                <i class="bi bi-arrow-left"></i> Back to List
            </a>
        </div>
    </div>

    <div class="row">
        <div class="col-md-8">
            <div class="card">
                <div class="card-header">
                    <h5 class="card-title mb-0">
                        <i class="bi bi-info-circle"></i> Customer Information
                    </h5>
                </div>
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/customers" method="post" id="editCustomerForm">
                        <input type="hidden" name="action" value="update">
                        <input type="hidden" name="id" value="${customer.accountNumber}">

                        <div class="row">
                            <!-- Account Number (Read-only) -->
                            <div class="col-md-6 mb-3">
                                <label for="accountNumber" class="form-label">
                                    <i class="bi bi-hash"></i> Account Number
                                </label>
                                <input type="text"
                                       class="form-control"
                                       id="accountNumber"
                                       value="${customer.accountNumber}"
                                       readonly>
                                <small class="form-text text-muted">
                                    Account number cannot be changed
                                </small>
                            </div>

                            <!-- Full Name -->
                            <div class="col-md-6 mb-3">
                                <label for="fullName" class="form-label">
                                    <i class="bi bi-person"></i> Full Name *
                                </label>
                                <input type="text"
                                       class="form-control"
                                       id="fullName"
                                       name="fullName"
                                       value="${customer.fullName}"
                                       required>
                            </div>

                            <!-- Email -->
                            <div class="col-md-6 mb-3">
                                <label for="email" class="form-label">
                                    <i class="bi bi-envelope"></i> Email Address
                                </label>
                                <input type="email"
                                       class="form-control"
                                       id="email"
                                       name="email"
                                       value="${customer.email}">
                            </div>

                            <!-- Phone Number -->
                            <div class="col-md-6 mb-3">
                                <label for="phoneNumber" class="form-label">
                                    <i class="bi bi-telephone"></i> Phone Number *
                                </label>
                                <input type="tel"
                                       class="form-control"
                                       id="phoneNumber"
                                       name="phoneNumber"
                                       value="${customer.phoneNumber}"
                                       required>
                            </div>

                            <!-- Address -->
                            <div class="col-md-12 mb-3">
                                <label for="address" class="form-label">
                                    <i class="bi bi-geo-alt"></i> Address *
                                </label>
                                <textarea class="form-control"
                                          id="address"
                                          name="address"
                                          rows="3"
                                          required>${customer.address}</textarea>
                            </div>

                            <!-- City -->
                            <div class="col-md-6 mb-3">
                                <label for="city" class="form-label">
                                    <i class="bi bi-building"></i> City
                                </label>
                                <input type="text"
                                       class="form-control"
                                       id="city"
                                       name="city"
                                       value="${customer.city}">
                            </div>

                            <!-- Postal Code -->
                            <div class="col-md-6 mb-3">
                                <label for="postalCode" class="form-label">
                                    <i class="bi bi-mailbox"></i> Postal Code
                                </label>
                                <input type="text"
                                       class="form-control"
                                       id="postalCode"
                                       name="postalCode"
                                       value="${customer.postalCode}">
                            </div>

                            <!-- Units Consumed -->
                            <div class="col-md-6 mb-3">
                                <label for="unitsConsumed" class="form-label">
                                    <i class="bi bi-graph-up"></i> Units Consumed
                                </label>
                                <input type="number"
                                       class="form-control"
                                       id="unitsConsumed"
                                       name="unitsConsumed"
                                       value="${customer.unitsConsumed}"
                                       min="0"
                                       step="1">
                                <small class="form-text text-muted">
                                    Current number of units consumed
                                </small>
                            </div>

                            <!-- Status -->
                            <div class="col-md-6 mb-3">
                                <label for="active" class="form-label">
                                    <i class="bi bi-toggle-on"></i> Account Status
                                </label>
                                <select class="form-select" id="active" name="active">
                                    <option value="true" ${customer.active ? 'selected' : ''}>Active</option>
                                    <option value="false" ${!customer.active ? 'selected' : ''}>Inactive</option>
                                </select>
                            </div>
                        </div>

                        <!-- Submit Buttons -->
                        <div class="row">
                            <div class="col-12">
                                <hr>
                                <div class="d-flex justify-content-between">
                                    <button type="button" class="btn btn-warning" onclick="resetToOriginal()">
                                        <i class="bi bi-arrow-clockwise"></i> Reset Changes
                                    </button>
                                    <div>
                                        <button type="button" class="btn btn-outline-primary me-2" onclick="previewChanges()">
                                            <i class="bi bi-eye"></i> Preview Changes
                                        </button>
                                        <button type="submit" class="btn btn-success">
                                            <i class="bi bi-check-circle"></i> Update Customer
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Customer Info Panel -->
        <div class="col-md-4">
            <div class="card">
                <div class="card-header">
                    <h5 class="card-title mb-0">
                        <i class="bi bi-info-square"></i> Customer Summary
                    </h5>
                </div>
                <div class="card-body">
                    <div class="text-center mb-3">
                        <i class="bi bi-person-circle text-primary" style="font-size: 3rem;"></i>
                        <h6 class="mt-2">${customer.fullName}</h6>
                        <span class="badge bg-${customer.active ? 'success' : 'danger'}">
                            ${customer.active ? 'Active' : 'Inactive'}
                        </span>
                    </div>

                    <table class="table table-sm">
                        <tr>
                            <td><strong>Account:</strong></td>
                            <td>${customer.accountNumber}</td>
                        </tr>
                        <tr>
                            <td><strong>Phone:</strong></td>
                            <td>${customer.phoneNumber}</td>
                        </tr>
                        <tr>
                            <td><strong>Email:</strong></td>
                            <td><small>${customer.email}</small></td>
                        </tr>
                        <tr>
                            <td><strong>Units:</strong></td>
                            <td>${customer.unitsConsumed}</td>
                        </tr>
                        <c:if test="${not empty customer.createdDate}">
                            <tr>
                                <td><strong>Joined:</strong></td>
                                <td><small><fmt:formatDate value="${customer.createdDate}" pattern="MMM yyyy"/></small></td>
                            </tr>
                        </c:if>
                    </table>
                </div>
            </div>

            <!-- Quick Actions -->
            <div class="card mt-3">
                <div class="card-header">
                    <h5 class="card-title mb-0">
                        <i class="bi bi-lightning"></i> Quick Actions
                    </h5>
                </div>
                <div class="card-body">
                    <div class="d-grid gap-2">
                        <a href="${pageContext.request.contextPath}/bills?action=create&customerId=${customer.accountNumber}"
                           class="btn btn-primary btn-sm">
                            <i class="bi bi-receipt"></i> Create Bill
                        </a>
                        <a href="${pageContext.request.contextPath}/bills?customer=${customer.accountNumber}"
                           class="btn btn-info btn-sm">
                            <i class="bi bi-list"></i> View Bills
                        </a>
                        <button class="btn btn-warning btn-sm" onclick="sendNotification()">
                            <i class="bi bi-envelope"></i> Send Notification
                        </button>
                    </div>
                </div>
            </div>

            <!-- Edit History (if available) -->
            <c:if test="${not empty customer.lastModifiedDate}">
                <div class="card mt-3">
                    <div class="card-header">
                        <h5 class="card-title mb-0">
                            <i class="bi bi-clock-history"></i> Last Updated
                        </h5>
                    </div>
                    <div class="card-body">
                        <p class="small mb-1">
                            <strong>Date:</strong> <fmt:formatDate value="${customer.lastModifiedDate}" pattern="MMM dd, yyyy HH:mm"/>
                        </p>
                        <c:if test="${not empty customer.lastModifiedBy}">
                            <p class="small mb-0">
                                <strong>By:</strong> ${customer.lastModifiedBy}
                            </p>
                        </c:if>
                    </div>
                </div>
            </c:if>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />

<script>
    // Store original values for reset functionality
    const originalValues = {
        fullName: '${customer.fullName}',
        email: '${customer.email}',
        phoneNumber: '${customer.phoneNumber}',
        address: '${customer.address}',
        city: '${customer.city}',
        postalCode: '${customer.postalCode}',
        unitsConsumed: '${customer.unitsConsumed}',
        active: '${customer.active}'
    };

    // Form validation
    document.getElementById('editCustomerForm').addEventListener('submit', function(e) {
        const fullName = document.getElementById('fullName').value.trim();
        const phoneNumber = document.getElementById('phoneNumber').value.trim();
        const address = document.getElementById('address').value.trim();

        if (!fullName || !phoneNumber || !address) {
            e.preventDefault();
            alert('Please fill in all required fields marked with *');
            return false;
        }

        // Phone number validation
        if (phoneNumber.length < 10) {
            e.preventDefault();
            alert('Please enter a valid phone number');
            return false;
        }

        // Confirm update
        if (!confirm('Are you sure you want to update this customer information?')) {
            e.preventDefault();
            return false;
        }
    });

    // Reset to original values
    function resetToOriginal() {
        if (confirm('Are you sure you want to reset all changes?')) {
            document.getElementById('fullName').value = originalValues.fullName;
            document.getElementById('email').value = originalValues.email;
            document.getElementById('phoneNumber').value = originalValues.phoneNumber;
            document.getElementById('address').value = originalValues.address;
            document.getElementById('city').value = originalValues.city;
            document.getElementById('postalCode').value = originalValues.postalCode;
            document.getElementById('unitsConsumed').value = originalValues.unitsConsumed;
            document.getElementById('active').value = originalValues.active;
        }
    }

    // Preview changes
    function previewChanges() {
        const changes = [];

        if (document.getElementById('fullName').value !== originalValues.fullName) {
            changes.push(`Name: "${originalValues.fullName}" → "${document.getElementById('fullName').value}"`);
        }
        if (document.getElementById('email').value !== originalValues.email) {
            changes.push(`Email: "${originalValues.email}" → "${document.getElementById('email').value}"`);
        }
        if (document.getElementById('phoneNumber').value !== originalValues.phoneNumber) {
            changes.push(`Phone: "${originalValues.phoneNumber}" → "${document.getElementById('phoneNumber').value}"`);
        }
        if (document.getElementById('address').value !== originalValues.address) {
            changes.push(`Address: Changed`);
        }
        if (document.getElementById('city').value !== originalValues.city) {
            changes.push(`City: "${originalValues.city}" → "${document.getElementById('city').value}"`);
        }
        if (document.getElementById('postalCode').value !== originalValues.postalCode) {
            changes.push(`Postal: "${originalValues.postalCode}" → "${document.getElementById('postalCode').value}"`);
        }
        if (document.getElementById('unitsConsumed').value !== originalValues.unitsConsumed) {
            changes.push(`Units: ${originalValues.unitsConsumed} → ${document.getElementById('unitsConsumed').value}`);
        }
        if (document.getElementById('active').value !== originalValues.active) {
            const oldStatus = originalValues.active === 'true' ? 'Active' : 'Inactive';
            const newStatus = document.getElementById('active').value === 'true' ? 'Active' : 'Inactive';
            changes.push(`Status: ${oldStatus} → ${newStatus}`);
        }

        if (changes.length === 0) {
            alert('No changes detected.');
        } else {
            const preview = 'Changes to be saved:\n\n' + changes.join('\n');
            alert(preview);
        }
    }

    // Send notification placeholder
    function sendNotification() {
        alert('Notification feature would send email/SMS to customer about account updates.');
    }

    // Focus on first editable field when page loads
    window.addEventListener('load', function() {
        document.getElementById('fullName').focus();
    });

    // Mark form as dirty when changes are made
    let formDirty = false;
    const formInputs = document.querySelectorAll('#editCustomerForm input, #editCustomerForm textarea, #editCustomerForm select');
    formInputs.forEach(input => {
        input.addEventListener('change', function() {
            formDirty = true;
        });
    });

    // Warn user about unsaved changes
    window.addEventListener('beforeunload', function(e) {
        if (formDirty) {
            e.preventDefault();
            e.returnValue = 'You have unsaved changes. Are you sure you want to leave?';
            return e.returnValue;
        }
    });

    // Reset dirty flag when form is submitted
    document.getElementById('editCustomerForm').addEventListener('submit', function() {
        formDirty = false;
    });
</script>