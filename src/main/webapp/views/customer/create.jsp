<%--
  Created by IntelliJ IDEA.
  User: MOHAMED
  Date: 16/08/2025
  Time: 12:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Add Customer" />
<jsp:include page="../common/header.jsp" />

<!-- Page Content -->
<div class="col-md-10 main-content p-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="h3">
            <i class="bi bi-person-plus"></i> Add New Customer
        </h1>
        <div>
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
                    <form action="${pageContext.request.contextPath}/customers" method="post" id="customerForm">
                        <input type="hidden" name="action" value="create">

                        <div class="row">
                            <!-- Account Number -->
                            <div class="col-md-6 mb-3">
                                <label for="accountNumber" class="form-label">
                                    <i class="bi bi-hash"></i> Account Number *
                                </label>
                                <input type="text"
                                       class="form-control"
                                       id="accountNumber"
                                       name="accountNumber"
                                       value="${param.accountNumber}"
                                       placeholder="Enter unique account number"
                                       required>
                                <small class="form-text text-muted">
                                    Must be unique (e.g., ACC001, CUST001)
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
                                       value="${param.fullName}"
                                       placeholder="Enter customer's full name"
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
                                       value="${param.email}"
                                       placeholder="customer@example.com">
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
                                       value="${param.phoneNumber}"
                                       placeholder="0771234567"
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
                                          placeholder="Enter complete address"
                                          required>${param.address}</textarea>
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
                                       value="${param.city}"
                                       placeholder="Colombo">
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
                                       value="${param.postalCode}"
                                       placeholder="10100">
                            </div>

                            <!-- Units Consumed -->
                            <div class="col-md-6 mb-3">
                                <label for="unitsConsumed" class="form-label">
                                    <i class="bi bi-graph-up"></i> Initial Units Consumed
                                </label>
                                <input type="number"
                                       class="form-control"
                                       id="unitsConsumed"
                                       name="unitsConsumed"
                                       value="${param.unitsConsumed != null ? param.unitsConsumed : '0'}"
                                       min="0"
                                       step="1">
                                <small class="form-text text-muted">
                                    Number of units consumed (for billing calculations)
                                </small>
                            </div>

                            <!-- Status -->
                            <div class="col-md-6 mb-3">
                                <label for="active" class="form-label">
                                    <i class="bi bi-toggle-on"></i> Account Status
                                </label>
                                <select class="form-select" id="active" name="active">
                                    <option value="true" ${param.active != 'false' ? 'selected' : ''}>Active</option>
                                    <option value="false" ${param.active == 'false' ? 'selected' : ''}>Inactive</option>
                                </select>
                            </div>
                        </div>

                        <!-- Submit Buttons -->
                        <div class="row">
                            <div class="col-12">
                                <hr>
                                <div class="d-flex justify-content-between">
                                    <button type="button" class="btn btn-secondary" onclick="resetForm()">
                                        <i class="bi bi-arrow-clockwise"></i> Reset Form
                                    </button>
                                    <div>
                                        <button type="button" class="btn btn-outline-primary me-2" onclick="previewCustomer()">
                                            <i class="bi bi-eye"></i> Preview
                                        </button>
                                        <button type="submit" class="btn btn-primary">
                                            <i class="bi bi-check-circle"></i> Create Customer
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Help Panel -->
        <div class="col-md-4">
            <div class="card">
                <div class="card-header">
                    <h5 class="card-title mb-0">
                        <i class="bi bi-lightbulb"></i> Help & Tips
                    </h5>
                </div>
                <div class="card-body">
                    <h6 class="text-primary">Required Fields</h6>
                    <ul class="small">
                        <li>Account Number (unique)</li>
                        <li>Full Name</li>
                        <li>Phone Number</li>
                        <li>Address</li>
                    </ul>

                    <h6 class="text-primary mt-3">Account Number Tips</h6>
                    <ul class="small">
                        <li>Use format: ACC001, CUST001</li>
                        <li>Must be unique in system</li>
                        <li>Cannot be changed later</li>
                    </ul>

                    <h6 class="text-primary mt-3">Units Consumed</h6>
                    <p class="small">
                        This represents the number of units (books, items) the customer has purchased.
                        Used for billing calculations and consumption tracking.
                    </p>
                </div>
            </div>

            <!-- Sample Data -->
            <div class="card mt-3">
                <div class="card-header">
                    <h5 class="card-title mb-0">
                        <i class="bi bi-clipboard-data"></i> Sample Data
                    </h5>
                </div>
                <div class="card-body">
                    <button type="button" class="btn btn-sm btn-outline-info w-100" onclick="fillSampleData()">
                        <i class="bi bi-magic"></i> Fill Sample Data
                    </button>
                    <small class="text-muted d-block mt-2">
                        Click to populate form with sample customer data for testing
                    </small>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />

<script>
    // Form validation
    document.getElementById('customerForm').addEventListener('submit', function(e) {
        const accountNumber = document.getElementById('accountNumber').value.trim();
        const fullName = document.getElementById('fullName').value.trim();
        const phoneNumber = document.getElementById('phoneNumber').value.trim();
        const address = document.getElementById('address').value.trim();

        if (!accountNumber || !fullName || !phoneNumber || !address) {
            e.preventDefault();
            alert('Please fill in all required fields marked with *');
            return false;
        }

        // Account number validation
        if (accountNumber.length < 3) {
            e.preventDefault();
            alert('Account number must be at least 3 characters long');
            return false;
        }

        // Phone number validation (basic)
        if (phoneNumber.length < 10) {
            e.preventDefault();
            alert('Please enter a valid phone number');
            return false;
        }
    });

    // Reset form
    function resetForm() {
        if (confirm('Are you sure you want to reset the form? All entered data will be lost.')) {
            document.getElementById('customerForm').reset();
            document.getElementById('accountNumber').focus();
        }
    }

    // Preview customer data
    function previewCustomer() {
        const formData = new FormData(document.getElementById('customerForm'));
        let preview = 'Customer Preview:\n\n';

        preview += `Account Number: ${formData.get('accountNumber')}\n`;
        preview += `Full Name: ${formData.get('fullName')}\n`;
        preview += `Email: ${formData.get('email')}\n`;
        preview += `Phone: ${formData.get('phoneNumber')}\n`;
        preview += `Address: ${formData.get('address')}\n`;
        preview += `City: ${formData.get('city')}\n`;
        preview += `Postal Code: ${formData.get('postalCode')}\n`;
        preview += `Units Consumed: ${formData.get('unitsConsumed')}\n`;
        preview += `Status: ${formData.get('active') == 'true' ? 'Active' : 'Inactive'}`;

        alert(preview);
    }

    // Fill sample data for testing
    function fillSampleData() {
        const timestamp = Date.now().toString().slice(-4);

        document.getElementById('accountNumber').value = 'ACC' + timestamp;
        document.getElementById('fullName').value = 'John Doe';
        document.getElementById('email').value = 'john.doe@email.com';
        document.getElementById('phoneNumber').value = '0771234567';
        document.getElementById('address').value = '123 Main Street, Colombo 03';
        document.getElementById('city').value = 'Colombo';
        document.getElementById('postalCode').value = '00300';
        document.getElementById('unitsConsumed').value = '5';
        document.getElementById('active').value = 'true';
    }

    // Auto-generate account number suggestion
    document.getElementById('fullName').addEventListener('blur', function() {
        const accountNumberField = document.getElementById('accountNumber');
        if (!accountNumberField.value && this.value) {
            const name = this.value.trim().toUpperCase();
            const initials = name.split(' ').map(word => word.charAt(0)).join('');
            const timestamp = Date.now().toString().slice(-3);
            accountNumberField.value = 'ACC' + initials + timestamp;
        }
    });

    // Focus on first field when page loads
    window.addEventListener('load', function() {
        document.getElementById('accountNumber').focus();
    });
</script>