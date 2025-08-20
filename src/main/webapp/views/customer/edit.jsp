<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Edit Customer" />
<jsp:include page="../common/header.jsp" />

<div class="container-fluid">
    <div class="row">
        <div class="col-12">
            <!-- Page Header -->
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 class="h3 mb-0">
                        <i class="bi bi-pencil-square"></i> Edit Customer
                    </h2>
                    <p class="text-muted">Update customer information</p>
                </div>
                <div>
                    <a href="${pageContext.request.contextPath}/customers?action=view&id=${customer.accountNumber}"
                       class="btn btn-info me-2">
                        <i class="bi bi-eye"></i> View Details
                    </a>
                    <a href="${pageContext.request.contextPath}/customers" class="btn btn-outline-secondary">
                        <i class="bi bi-arrow-left"></i> Back to List
                    </a>
                </div>
            </div>

            <!-- Error/Success Messages -->
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-triangle"></i> ${errorMessage}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <c:if test="${not empty successMessage}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="bi bi-check-circle"></i> ${successMessage}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <!-- Edit Customer Form -->
            <div class="row justify-content-center">
                <div class="col-xl-8">
                    <div class="card shadow-sm">
                        <div class="card-header bg-warning text-dark">
                            <h5 class="card-title mb-0">
                                <i class="bi bi-pencil-fill"></i> Update Customer Information
                            </h5>
                        </div>
                        <div class="card-body">
                            <form method="post" action="${pageContext.request.contextPath}/customers"
                                  onsubmit="return validateForm()" novalidate>

                                <input type="hidden" name="action" value="update">
                                <input type="hidden" name="accountNumber" value="${customer.accountNumber}">

                                <div class="row">
                                    <!-- Account Number (Read-only) -->
                                    <div class="col-md-6 mb-3">
                                        <label for="accountNumberDisplay" class="form-label">
                                            <i class="bi bi-hash"></i> Account Number
                                        </label>
                                        <input type="text"
                                               class="form-control-plaintext bg-light"
                                               id="accountNumberDisplay"
                                               value="${customer.accountNumber}"
                                               readonly>
                                        <small class="form-text text-muted">Account number cannot be changed</small>
                                    </div>

                                    <!-- Status -->
                                    <div class="col-md-6 mb-3">
                                        <label for="isActive" class="form-label">
                                            <i class="bi bi-toggle-on"></i> Status
                                        </label>
                                        <select class="form-select" id="isActive" name="isActive">
                                            <option value="true" ${customer.active ? 'selected' : ''}>Active</option>
                                            <option value="false" ${!customer.active ? 'selected' : ''}>Inactive</option>
                                        </select>
                                    </div>

                                    <!-- Customer Name -->
                                    <div class="col-md-12 mb-3">
                                        <label for="name" class="form-label">
                                            <i class="bi bi-person"></i> Full Name *
                                        </label>
                                        <input type="text"
                                               class="form-control"
                                               id="name"
                                               name="name"
                                               value="${customer.name}"
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
                                               value="${customer.email}"
                                               placeholder="customer@example.com">
                                    </div>

                                    <!-- Phone Number -->
                                    <div class="col-md-6 mb-3">
                                        <label for="phoneNumber" class="form-label">
                                            <i class="bi bi-telephone"></i> Phone Number
                                        </label>
                                        <input type="tel"
                                               class="form-control"
                                               id="phoneNumber"
                                               name="phoneNumber"
                                               value="${customer.phoneNumber}"
                                               placeholder="+94 77 123 4567">
                                    </div>

                                    <!-- Address -->
                                    <div class="col-12 mb-3">
                                        <label for="address" class="form-label">
                                            <i class="bi bi-geo-alt"></i> Address
                                        </label>
                                        <textarea class="form-control"
                                                  id="address"
                                                  name="address"
                                                  rows="3"
                                                  placeholder="Enter complete address">${customer.address}</textarea>
                                    </div>

                                    <!-- Credit Limit -->
                                    <div class="col-md-6 mb-3">
                                        <label for="creditLimit" class="form-label">
                                            <i class="bi bi-credit-card"></i> Credit Limit (Rs.)
                                        </label>
                                        <input type="number"
                                               class="form-control"
                                               id="creditLimit"
                                               name="creditLimit"
                                               value="${customer.creditLimit}"
                                               min="0"
                                               step="0.01"
                                               placeholder="0.00">
                                        <small class="form-text text-muted">
                                            Set to 0 for no credit limit
                                        </small>
                                    </div>

                                    <!-- Registration Date (Read-only) -->
                                    <div class="col-md-6 mb-3">
                                        <label class="form-label">
                                            <i class="bi bi-calendar"></i> Registration Date
                                        </label>
                                        <div class="form-control-plaintext bg-light">
                                            <c:if test="${not empty customer.registrationDate}">
                                                <fmt:formatDate value="${customer.registrationDate}" pattern="MMM dd, yyyy" />
                                            </c:if>
                                            <c:if test="${empty customer.registrationDate}">
                                                Not available
                                            </c:if>
                                        </div>
                                    </div>
                                </div>

                                <!-- Form Actions -->
                                <div class="row mt-4">
                                    <div class="col-12">
                                        <div class="d-flex justify-content-between">
                                            <button type="button" class="btn btn-warning" onclick="resetForm()">
                                                <i class="bi bi-arrow-clockwise"></i> Reset Changes
                                            </button>
                                            <div>
                                                <a href="${pageContext.request.contextPath}/customers"
                                                   class="btn btn-secondary me-2">
                                                    <i class="bi bi-x-circle"></i> Cancel
                                                </a>
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
            </div>
        </div>
    </div>
</div>

<!-- JavaScript for Form Validation -->
<script>
    // Store original values for reset functionality
    const originalValues = {
        name: '${customer.name}',
        email: '${customer.email}',
        phoneNumber: '${customer.phoneNumber}',
        address: '${customer.address}',
        creditLimit: '${customer.creditLimit}',
        isActive: '${customer.active}'
    };

    function validateForm() {
        let isValid = true;

        // Clear previous errors
        document.querySelectorAll('.is-invalid').forEach(element => {
            element.classList.remove('is-invalid');
        });

        // Validate Name
        const name = document.getElementById('name');
        if (!name.value.trim()) {
            showFieldError(name, 'Customer name is required');
            isValid = false;
        } else if (name.value.length < 2) {
            showFieldError(name, 'Name must be at least 2 characters');
            isValid = false;
        }

        // Validate Email (if provided)
        const email = document.getElementById('email');
        if (email.value.trim() && !isValidEmail(email.value)) {
            showFieldError(email, 'Please enter a valid email address');
            isValid = false;
        }

        // Validate Credit Limit (if provided)
        const creditLimit = document.getElementById('creditLimit');
        if (creditLimit.value && parseFloat(creditLimit.value) < 0) {
            showFieldError(creditLimit, 'Credit limit cannot be negative');
            isValid = false;
        }

        if (isValid) {
            return confirm('Are you sure you want to update this customer information?');
        }

        return isValid;
    }

    function showFieldError(field, message) {
        field.classList.add('is-invalid');

        // Remove existing error message
        const existingError = field.parentNode.querySelector('.invalid-feedback');
        if (existingError) {
            existingError.remove();
        }

        // Add new error message
        const errorDiv = document.createElement('div');
        errorDiv.className = 'invalid-feedback';
        errorDiv.textContent = message;
        field.parentNode.appendChild(errorDiv);
    }

    function isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    function resetForm() {
        if (confirm('Are you sure you want to reset all changes to original values?')) {
            document.getElementById('name').value = originalValues.name || '';
            document.getElementById('email').value = originalValues.email || '';
            document.getElementById('phoneNumber').value = originalValues.phoneNumber || '';
            document.getElementById('address').value = originalValues.address || '';
            document.getElementById('creditLimit').value = originalValues.creditLimit || '0';
            document.getElementById('isActive').value = originalValues.isActive;

            // Clear any validation errors
            document.querySelectorAll('.is-invalid').forEach(element => {
                element.classList.remove('is-invalid');
            });
            document.querySelectorAll('.invalid-feedback').forEach(element => {
                element.remove();
            });
        }
    }

    // Focus on name field when page loads
    document.addEventListener('DOMContentLoaded', function() {
        document.getElementById('name').focus();
    });
</script>

<jsp:include page="../common/footer.jsp" />