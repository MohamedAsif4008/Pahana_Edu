<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Create New User" />
<jsp:include page="../common/header.jsp" />

<div class="container-fluid">
    <!-- Page Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="h3">
            <i class="bi bi-person-plus"></i> Create New User
        </h1>
        <a href="${pageContext.request.contextPath}/users" class="btn btn-outline-secondary">
            <i class="bi bi-arrow-left"></i> Back to Users
        </a>
    </div>

    <!-- Messages -->
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="bi bi-exclamation-triangle"></i> ${errorMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <!-- User Creation Form -->
    <div class="row justify-content-center">
        <div class="col-xl-8">
            <div class="card shadow-sm">
                <div class="card-header bg-primary text-white">
                    <h5 class="card-title mb-0">
                        <i class="bi bi-person-fill"></i> User Details
                    </h5>
                </div>
                <div class="card-body">
                    <form method="post" action="${pageContext.request.contextPath}/users"
                          onsubmit="return validateForm()" novalidate>

                        <input type="hidden" name="action" value="create">

                        <div class="row">
                            <!-- Username -->
                            <div class="col-md-6 mb-3">
                                <label for="username" class="form-label">
                                    <i class="bi bi-person"></i> Username *
                                </label>
                                <input type="text"
                                       class="form-control"
                                       id="username"
                                       name="username"
                                       value="${param.username}"
                                       placeholder="Enter unique username"
                                       required>
                                <small class="form-text text-muted">
                                    Username must be unique and 3-20 characters long
                                </small>
                            </div>

                            <!-- Password -->
                            <div class="col-md-6 mb-3">
                                <label for="password" class="form-label">
                                    <i class="bi bi-lock"></i> Password *
                                </label>
                                <input type="password"
                                       class="form-control"
                                       id="password"
                                       name="password"
                                       placeholder="Enter password"
                                       required>
                                <small class="form-text text-muted">
                                    Password should be at least 6 characters
                                </small>
                            </div>

                            <!-- Full Name -->
                            <div class="col-md-6 mb-3">
                                <label for="fullName" class="form-label">
                                    <i class="bi bi-person-badge"></i> Full Name *
                                </label>
                                <input type="text"
                                       class="form-control"
                                       id="fullName"
                                       name="fullName"
                                       value="${param.fullName}"
                                       placeholder="Enter full name"
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
                                       placeholder="user@example.com">
                                <small class="form-text text-muted">
                                    Optional - for password reset notifications
                                </small>
                            </div>

                            <!-- Role -->
                            <div class="col-md-6 mb-3">
                                <label for="role" class="form-label">
                                    <i class="bi bi-shield"></i> Role *
                                </label>
                                <select class="form-select" id="role" name="role" required>
                                    <option value="">Select Role</option>
                                    <option value="STAFF" ${param.role == 'STAFF' ? 'selected' : ''}>Staff</option>
                                    <option value="ADMIN" ${param.role == 'ADMIN' ? 'selected' : ''}>Admin</option>
                                </select>
                                <small class="form-text text-muted">
                                    Staff can manage customers, items, and bills. Admin can also manage users.
                                </small>
                            </div>

                            <!-- Status -->
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
                                        User is active
                                    </label>
                                </div>
                                <small class="form-text text-muted">
                                    Inactive users cannot log in to the system
                                </small>
                            </div>
                        </div>

                        <!-- Form Actions -->
                        <div class="row">
                            <div class="col-12">
                                <hr>
                                <div class="d-flex justify-content-end gap-2">
                                    <a href="${pageContext.request.contextPath}/users"
                                       class="btn btn-outline-secondary">
                                        <i class="bi bi-x-circle"></i> Cancel
                                    </a>
                                    <button type="reset" class="btn btn-outline-warning">
                                        <i class="bi bi-arrow-clockwise"></i> Reset
                                    </button>
                                    <button type="submit" class="btn btn-primary">
                                        <i class="bi bi-check-circle"></i> Create User
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

<!-- JavaScript for form validation -->
<script>
    function validateForm() {
        let isValid = true;

        // Clear previous errors
        document.querySelectorAll('.is-invalid').forEach(element => {
            element.classList.remove('is-invalid');
        });

        // Validate Username
        const username = document.getElementById('username');
        if (!username.value.trim()) {
            showFieldError(username, 'Username is required');
            isValid = false;
        } else if (username.value.length < 3 || username.value.length > 20) {
            showFieldError(username, 'Username must be 3-20 characters long');
            isValid = false;
        }

        // Validate Password
        const password = document.getElementById('password');
        if (!password.value.trim()) {
            showFieldError(password, 'Password is required');
            isValid = false;
        } else if (password.value.length < 6) {
            showFieldError(password, 'Password must be at least 6 characters');
            isValid = false;
        }

        // Validate Full Name
        const fullName = document.getElementById('fullName');
        if (!fullName.value.trim()) {
            showFieldError(fullName, 'Full name is required');
            isValid = false;
        } else if (fullName.value.length < 2) {
            showFieldError(fullName, 'Full name must be at least 2 characters');
            isValid = false;
        }

        // Validate Email (if provided)
        const email = document.getElementById('email');
        if (email.value.trim() && !isValidEmail(email.value)) {
            showFieldError(email, 'Please enter a valid email address');
            isValid = false;
        }

        // Validate Role
        const role = document.getElementById('role');
        if (!role.value) {
            showFieldError(role, 'Please select a role');
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
</script>

<jsp:include page="../common/footer.jsp" />