<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Edit User" />
<jsp:include page="../common/header.jsp" />

<div class="container-fluid">
    <!-- Page Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="h3">
            <i class="bi bi-pencil-square"></i> Edit User
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

    <!-- User Edit Form -->
    <div class="row justify-content-center">
        <div class="col-xl-8">
            <div class="card shadow-sm">
                <div class="card-header bg-warning text-white">
                    <h5 class="card-title mb-0">
                        <i class="bi bi-person-fill"></i> Edit User: ${user.username}
                    </h5>
                </div>
                <div class="card-body">
                    <form method="post" action="${pageContext.request.contextPath}/users"
                          onsubmit="return validateForm()" novalidate>

                        <input type="hidden" name="action" value="update">
                        <input type="hidden" name="id" value="${user.userId}">

                        <div class="row">
                            <!-- Username (Read-only) -->
                            <div class="col-md-6 mb-3">
                                <label for="username" class="form-label">
                                    <i class="bi bi-person"></i> Username
                                </label>
                                <input type="text"
                                       class="form-control"
                                       id="username"
                                       value="${user.username}"
                                       readonly>
                                <small class="form-text text-muted">
                                    Username cannot be changed
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
                                       value="${user.fullName}"
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
                                       value="${user.email}"
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
                                    <option value="STAFF" ${user.role == 'STAFF' ? 'selected' : ''}>Staff</option>
                                    <option value="ADMIN" ${user.role == 'ADMIN' ? 'selected' : ''}>Admin</option>
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
                                           ${user.active ? 'checked' : ''}>
                                    <label class="form-check-label" for="isActive">
                                        User is active
                                    </label>
                                </div>
                                <small class="form-text text-muted">
                                    Inactive users cannot log in to the system
                                </small>
                            </div>

                            <!-- Created Date (Read-only) -->
                            <div class="col-md-6 mb-3">
                                <label class="form-label">
                                    <i class="bi bi-calendar"></i> Created Date
                                </label>
                                <input type="text"
                                       class="form-control"
                                       value="<fmt:formatDate value='${user.createdDate}' pattern='dd/MM/yyyy HH:mm'/>"
                                       readonly>
                            </div>
                        </div>

                        <!-- Password Reset Section -->
                        <div class="row">
                            <div class="col-12">
                                <hr>
                                <h6 class="text-muted">
                                    <i class="bi bi-lock"></i> Password Management
                                </h6>
                                <div class="alert alert-info">
                                    <i class="bi bi-info-circle"></i>
                                    <strong>Note:</strong> To reset the user's password, you can use the password reset functionality 
                                    or ask the user to use the "Forgot Password" feature on the login page.
                                </div>
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
                                    <button type="submit" class="btn btn-warning">
                                        <i class="bi bi-check-circle"></i> Update User
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