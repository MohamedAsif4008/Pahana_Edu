<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Create Customer" />
<jsp:include page="../common/header.jsp" />

<div class="container-fluid">
    <div class="row">
        <div class="col-12">
            <!-- Page Header -->
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 class="h3 mb-0">
                        <i class="bi bi-person-plus"></i> Create New Customer
                    </h2>
                    <p class="text-muted">Add a new customer to the system</p>
                </div>
                <a href="${pageContext.request.contextPath}/customers" class="btn btn-outline-secondary">
                    <i class="bi bi-arrow-left"></i> Back to Customers
                </a>
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

            <!-- Customer Creation Form -->
            <div class="row justify-content-center">
                <div class="col-xl-8">
                    <div class="card shadow-sm">
                        <div class="card-header bg-primary text-white">
                            <h5 class="card-title mb-0">
                                <i class="bi bi-person-fill"></i> Customer Details
                            </h5>
                        </div>
                        <div class="card-body">
                            <form method="post" action="${pageContext.request.contextPath}/customers"
                                  onsubmit="return validateForm()" novalidate>

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

                                    <!-- Name (Fixed from fullName) -->
                                    <div class="col-md-6 mb-3">
                                        <label for="name" class="form-label">
                                            <i class="bi bi-person"></i> Full Name *
                                        </label>
                                        <input type="text"
                                               class="form-control"
                                               id="name"
                                               name="name"
                                               value="${param.name}"
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
                                            <i class="bi bi-telephone"></i> Phone Number
                                        </label>
                                        <input type="tel"
                                               class="form-control"
                                               id="phoneNumber"
                                               name="phoneNumber"
                                               value="${param.phoneNumber}"
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
                                                  placeholder="Enter complete address">${param.address}</textarea>
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
                                               value="${param.creditLimit}"
                                               min="0"
                                               step="0.01"
                                               placeholder="0.00">
                                        <small class="form-text text-muted">
                                            Leave blank or 0 for no credit limit
                                        </small>
                                    </div>

                                    <!-- Active Status -->
                                    <div class="col-md-6 mb-3">
                                        <label class="form-label">
                                            <i class="bi bi-toggle-on"></i> Status
                                        </label>
                                        <div class="form-check">
                                            <input type="checkbox"
                                                   class="form-check-input"
                                                   id="isActive"
                                                   name="isActive"
                                                   value="true"
                                            ${empty param.isActive || param.isActive == 'true' ? 'checked' : ''}>
                                            <label class="form-check-label" for="isActive">
                                                Customer is active
                                            </label>
                                        </div>
                                    </div>
                                </div>

                                <!-- Form Actions -->
                                <div class="row mt-4">
                                    <div class="col-12">
                                        <div class="d-flex justify-content-end gap-2">
                                            <a href="${pageContext.request.contextPath}/customers"
                                               class="btn btn-secondary">
                                                <i class="bi bi-x-circle"></i> Cancel
                                            </a>
                                            <button type="reset" class="btn btn-outline-warning">
                                                <i class="bi bi-arrow-clockwise"></i> Reset
                                            </button>
                                            <button type="submit" class="btn btn-primary">
                                                <i class="bi bi-check-circle"></i> Create Customer
                                            </button>
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
    function validateForm() {
        let isValid = true;

        // Clear previous errors
        document.querySelectorAll('.is-invalid').forEach(element => {
            element.classList.remove('is-invalid');
        });

        // Validate Account Number
        const accountNumber = document.getElementById('accountNumber');
        if (!accountNumber.value.trim()) {
            showFieldError(accountNumber, 'Account number is required');
            isValid = false;
        } else if (accountNumber.value.length < 3) {
            showFieldError(accountNumber, 'Account number must be at least 3 characters');
            isValid = false;
        }

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

    // Auto-generate account number if empty
    document.getElementById('name').addEventListener('blur', function() {
        const accountNumber = document.getElementById('accountNumber');
        if (!accountNumber.value.trim() && this.value.trim()) {
            const names = this.value.trim().split(' ');
            const initials = names.map(name => name.charAt(0).toUpperCase()).join('');
            const timestamp = Date.now().toString().slice(-4);
            accountNumber.value = 'ACC' + initials + timestamp;
        }
    });
</script>

<jsp:include page="../common/footer.jsp" />