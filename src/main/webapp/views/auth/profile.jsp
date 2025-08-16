<%--
  Created by IntelliJ IDEA.
  User: MOHAMED
  Date: 16/08/2025
  Time: 11:45
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="My Profile" />
<jsp:include page="../common/header.jsp" />

<!-- Page Content -->
<div class="col-md-10 main-content p-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="h3">
            <i class="bi bi-person-circle"></i> My Profile
        </h1>
        <div>
            <button type="button" class="btn btn-primary" onclick="toggleEditMode()">
                <i class="bi bi-pencil"></i> Edit Profile
            </button>
        </div>
    </div>

    <!-- Display messages -->
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="bi bi-check-circle"></i> ${successMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="bi bi-exclamation-triangle"></i> ${errorMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <div class="row">
        <!-- Profile Information Card -->
        <div class="col-md-8">
            <div class="card">
                <div class="card-header">
                    <h5 class="card-title mb-0">
                        <i class="bi bi-info-circle"></i> Personal Information
                    </h5>
                </div>
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/profile" method="post" id="profileForm">
                        <input type="hidden" name="action" value="update">

                        <div class="row">
                            <!-- User ID (Read-only) -->
                            <div class="col-md-6 mb-3">
                                <label class="form-label">
                                    <i class="bi bi-hash"></i> User ID
                                </label>
                                <input type="text" class="form-control" value="${currentUser.userId}" readonly>
                            </div>

                            <!-- Username (Read-only) -->
                            <div class="col-md-6 mb-3">
                                <label class="form-label">
                                    <i class="bi bi-at"></i> Username
                                </label>
                                <input type="text" class="form-control" value="${currentUser.username}" readonly>
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
                                       value="${currentUser.fullName}"
                                       readonly>
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
                                       value="${currentUser.email}"
                                       readonly>
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
                                       value="${currentUser.phoneNumber}"
                                       readonly>
                            </div>

                            <!-- Role (Read-only) -->
                            <div class="col-md-6 mb-3">
                                <label class="form-label">
                                    <i class="bi bi-shield"></i> Role
                                </label>
                                <input type="text" class="form-control" value="${currentUser.displayRole}" readonly>
                            </div>

                            <!-- Created Date (Read-only) -->
                            <div class="col-md-6 mb-3">
                                <label class="form-label">
                                    <i class="bi bi-calendar-plus"></i> Account Created
                                </label>
                                <input type="text" class="form-control"
                                       value="<fmt:formatDate value='${currentUser.createdDate}' pattern='MMM dd, yyyy'/>"
                                       readonly>
                            </div>

                            <!-- Last Login (Read-only) -->
                            <div class="col-md-6 mb-3">
                                <label class="form-label">
                                    <i class="bi bi-clock"></i> Last Login
                                </label>
                                <input type="text" class="form-control"
                                       value="<fmt:formatDate value='${currentUser.lastLoginDate}' pattern='MMM dd, yyyy HH:mm'/>"
                                       readonly>
                            </div>
                        </div>

                        <!-- Update Button (Hidden by default) -->
                        <div class="text-end" id="updateButtonContainer" style="display: none;">
                            <button type="button" class="btn btn-secondary me-2" onclick="cancelEdit()">
                                <i class="bi bi-x-circle"></i> Cancel
                            </button>
                            <button type="submit" class="btn btn-primary">
                                <i class="bi bi-check-circle"></i> Update Profile
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Account Security Card -->
        <div class="col-md-4">
            <!-- Security Actions -->
            <div class="card mb-4">
                <div class="card-header">
                    <h5 class="card-title mb-0">
                        <i class="bi bi-shield-lock"></i> Account Security
                    </h5>
                </div>
                <div class="card-body">
                    <div class="d-grid gap-2">
                        <button type="button" class="btn btn-outline-warning" data-bs-toggle="modal" data-bs-target="#changePasswordModal">
                            <i class="bi bi-key"></i> Change Password
                        </button>
                        <button type="button" class="btn btn-outline-info" onclick="showSecurityQuestions()">
                            <i class="bi bi-question-circle"></i> Security Questions
                        </button>
                        <button type="button" class="btn btn-outline-danger" onclick="showAccountSettings()">
                            <i class="bi bi-gear"></i> Account Settings
                        </button>
                    </div>
                </div>
            </div>

            <!-- Account Status -->
            <div class="card">
                <div class="card-header">
                    <h5 class="card-title mb-0">
                        <i class="bi bi-info-square"></i> Account Status
                    </h5>
                </div>
                <div class="card-body">
                    <div class="mb-3">
                        <span class="badge bg-${currentUser.active ? 'success' : 'danger'} fs-6">
                            <i class="bi bi-${currentUser.active ? 'check' : 'x'}-circle"></i>
                            ${currentUser.active ? 'Active' : 'Inactive'}
                        </span>
                    </div>

                    <div class="small text-muted">
                        <div class="mb-1">
                            <i class="bi bi-person-badge"></i>
                            User since: <fmt:formatDate value='${currentUser.createdDate}' pattern='yyyy'/>
                        </div>
                        <div class="mb-1">
                            <i class="bi bi-activity"></i>
                            Status: ${currentUser.active ? 'Account in good standing' : 'Account suspended'}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Change Password Modal -->
<div class="modal fade" id="changePasswordModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="bi bi-key"></i> Change Password
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/profile" method="post" id="changePasswordForm">
                <input type="hidden" name="action" value="changePassword">

                <div class="modal-body">
                    <div class="mb-3">
                        <label for="currentPassword" class="form-label">Current Password *</label>
                        <input type="password" class="form-control" id="currentPassword" name="currentPassword" required>
                    </div>

                    <div class="mb-3">
                        <label for="newPassword" class="form-label">New Password *</label>
                        <div class="input-group">
                            <input type="password" class="form-control" id="newPassword" name="newPassword" required>
                            <button class="btn btn-outline-secondary" type="button" onclick="toggleModalPassword('newPassword', 'newPasswordIcon')">
                                <i class="bi bi-eye" id="newPasswordIcon"></i>
                            </button>
                        </div>
                        <div class="password-strength bg-secondary mt-2" id="modalPasswordStrength" style="height: 4px; border-radius: 2px;"></div>
                        <small class="form-text text-muted">Password must be at least 8 characters</small>
                    </div>

                    <div class="mb-3">
                        <label for="confirmPassword" class="form-label">Confirm New Password *</label>
                        <div class="input-group">
                            <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
                            <button class="btn btn-outline-secondary" type="button" onclick="toggleModalPassword('confirmPassword', 'confirmPasswordIcon')">
                                <i class="bi bi-eye" id="confirmPasswordIcon"></i>
                            </button>
                        </div>
                        <div id="modalPasswordMatch" class="form-text"></div>
                    </div>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">
                        <i class="bi bi-check-circle"></i> Change Password
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />

<script>
    // Edit mode toggle
    function toggleEditMode() {
        const readonlyFields = ['fullName', 'email', 'phoneNumber'];
        const updateContainer = document.getElementById('updateButtonContainer');

        readonlyFields.forEach(fieldId => {
            const field = document.getElementById(fieldId);
            field.readOnly = !field.readOnly;
            if (!field.readOnly) {
                field.focus();
            }
        });

        updateContainer.style.display = updateContainer.style.display === 'none' ? 'block' : 'none';
    }

    function cancelEdit() {
        // Reload page to restore original values
        window.location.reload();
    }

    // Password visibility toggle for modal
    function toggleModalPassword(fieldId, iconId) {
        const field = document.getElementById(fieldId);
        const icon = document.getElementById(iconId);

        if (field.type === 'password') {
            field.type = 'text';
            icon.className = 'bi bi-eye-slash';
        } else {
            field.type = 'password';
            icon.className = 'bi bi-eye';
        }
    }

    // Password strength for modal
    document.getElementById('newPassword').addEventListener('input', function() {
        const password = this.value;
        const strengthBar = document.getElementById('modalPasswordStrength');
        let strength = 0;

        if (password.length >= 8) strength++;
        if (/[a-z]/.test(password)) strength++;
        if (/[A-Z]/.test(password)) strength++;
        if (/[0-9]/.test(password)) strength++;
        if (/[^A-Za-z0-9]/.test(password)) strength++;

        strengthBar.style.width = (strength * 20) + '%';

        switch(strength) {
            case 0:
            case 1:
                strengthBar.className = 'password-strength bg-danger mt-2';
                break;
            case 2:
                strengthBar.className = 'password-strength bg-warning mt-2';
                break;
            case 3:
                strengthBar.className = 'password-strength bg-info mt-2';
                break;
            case 4:
            case 5:
                strengthBar.className = 'password-strength bg-success mt-2';
                break;
        }
    });

    // Password confirmation for modal
    document.getElementById('confirmPassword').addEventListener('input', function() {
        const newPassword = document.getElementById('newPassword').value;
        const confirmPassword = this.value;
        const matchDiv = document.getElementById('modalPasswordMatch');

        if (confirmPassword === '') {
            matchDiv.textContent = '';
            matchDiv.className = 'form-text';
        } else if (newPassword === confirmPassword) {
            matchDiv.textContent = 'Passwords match ✓';
            matchDiv.className = 'form-text text-success';
        } else {
            matchDiv.textContent = 'Passwords do not match ✗';
            matchDiv.className = 'form-text text-danger';
        }
    });

    // Form validation for password change
    document.getElementById('changePasswordForm').addEventListener('submit', function(e) {
        const currentPassword = document.getElementById('currentPassword').value;
        const newPassword = document.getElementById('newPassword').value;
        const confirmPassword = document.getElementById('confirmPassword').value;

        if (!currentPassword || !newPassword || !confirmPassword) {
            e.preventDefault();
            alert('Please fill in all password fields.');
            return false;
        }

        if (newPassword.length < 8) {
            e.preventDefault();
            alert('New password must be at least 8 characters long.');
            return false;
        }

        if (newPassword !== confirmPassword) {
            e.preventDefault();
            alert('New passwords do not match.');
            return false;
        }

        if (currentPassword === newPassword) {
            e.preventDefault();
            alert('New password must be different from current password.');
            return false;
        }
    });

    // Placeholder functions for additional features
    function showSecurityQuestions() {
        alert('Security questions feature would be implemented here.');
    }

    function showAccountSettings() {
        alert('Additional account settings would be implemented here.');
    }
</script>