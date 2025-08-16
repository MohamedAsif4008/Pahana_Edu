<%--
  Created by IntelliJ IDEA.
  User: MOHAMED
  Date: 16/08/2025
  Time: 11:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Register" />

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle} - Pahana Edu Billing System</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet">

    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 2rem 0;
        }
        .register-container {
            background: white;
            border-radius: 15px;
            box-shadow: 0 15px 35px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        .register-header {
            background: linear-gradient(135deg, #28a745, #20c997);
            color: white;
            text-align: center;
            padding: 2rem;
        }
        .form-control:focus {
            border-color: #28a745;
            box-shadow: 0 0 0 0.2rem rgba(40, 167, 69, 0.25);
        }
        .btn-register {
            background: linear-gradient(135deg, #28a745, #20c997);
            border: none;
            padding: 0.75rem;
            font-weight: 500;
        }
        .btn-register:hover {
            background: linear-gradient(135deg, #20c997, #1e7e34);
        }
        .password-strength {
            height: 5px;
            margin-top: 5px;
            border-radius: 3px;
        }
    </style>
</head>
<body>

<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-8 col-lg-6">
            <div class="register-container">
                <!-- Header -->
                <div class="register-header">
                    <i class="bi bi-person-plus fs-1 mb-3"></i>
                    <h3 class="mb-0">Create Account</h3>
                    <p class="mb-0">Join Pahana Edu Billing System</p>
                </div>

                <!-- Registration Form -->
                <div class="p-4">
                    <!-- Display error messages -->
                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="bi bi-exclamation-triangle"></i> ${errorMessage}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <!-- Display success messages -->
                    <c:if test="${not empty successMessage}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="bi bi-check-circle"></i> ${successMessage}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/register" method="post" id="registerForm">
                        <div class="row">
                            <!-- Full Name -->
                            <div class="col-md-12 mb-3">
                                <label for="fullName" class="form-label">
                                    <i class="bi bi-person"></i> Full Name *
                                </label>
                                <input type="text"
                                       class="form-control"
                                       id="fullName"
                                       name="fullName"
                                       value="${param.fullName}"
                                       placeholder="Enter your full name"
                                       required>
                            </div>

                            <!-- Username -->
                            <div class="col-md-6 mb-3">
                                <label for="username" class="form-label">
                                    <i class="bi bi-at"></i> Username *
                                </label>
                                <input type="text"
                                       class="form-control"
                                       id="username"
                                       name="username"
                                       value="${param.username}"
                                       placeholder="Choose a username"
                                       required>
                                <small class="form-text text-muted">
                                    Username must be 3-20 characters, letters and numbers only
                                </small>
                            </div>

                            <!-- Email -->
                            <div class="col-md-6 mb-3">
                                <label for="email" class="form-label">
                                    <i class="bi bi-envelope"></i> Email *
                                </label>
                                <input type="email"
                                       class="form-control"
                                       id="email"
                                       name="email"
                                       value="${param.email}"
                                       placeholder="Enter your email"
                                       required>
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
                                       placeholder="Enter phone number">
                            </div>

                            <!-- Role Selection (Admin Only) -->
                            <div class="col-md-6 mb-3">
                                <label for="role" class="form-label">
                                    <i class="bi bi-shield"></i> Role *
                                </label>
                                <select class="form-select" id="role" name="role" required>
                                    <option value="">Select Role</option>
                                    <option value="STAFF" ${param.role == 'STAFF' ? 'selected' : ''}>Staff</option>
                                    <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                                        <option value="ADMIN" ${param.role == 'ADMIN' ? 'selected' : ''}>Admin</option>
                                    </c:if>
                                </select>
                            </div>

                            <!-- Password -->
                            <div class="col-md-6 mb-3">
                                <label for="password" class="form-label">
                                    <i class="bi bi-lock"></i> Password *
                                </label>
                                <div class="input-group">
                                    <input type="password"
                                           class="form-control"
                                           id="password"
                                           name="password"
                                           placeholder="Create a password"
                                           required>
                                    <button class="btn btn-outline-secondary"
                                            type="button"
                                            onclick="togglePassword('password', 'passwordToggleIcon')">
                                        <i class="bi bi-eye" id="passwordToggleIcon"></i>
                                    </button>
                                </div>
                                <div class="password-strength bg-secondary" id="passwordStrength"></div>
                                <small class="form-text text-muted">
                                    Password must be at least 8 characters with letters and numbers
                                </small>
                            </div>

                            <!-- Confirm Password -->
                            <div class="col-md-6 mb-3">
                                <label for="confirmPassword" class="form-label">
                                    <i class="bi bi-lock-fill"></i> Confirm Password *
                                </label>
                                <div class="input-group">
                                    <input type="password"
                                           class="form-control"
                                           id="confirmPassword"
                                           name="confirmPassword"
                                           placeholder="Confirm your password"
                                           required>
                                    <button class="btn btn-outline-secondary"
                                            type="button"
                                            onclick="togglePassword('confirmPassword', 'confirmPasswordToggleIcon')">
                                        <i class="bi bi-eye" id="confirmPasswordToggleIcon"></i>
                                    </button>
                                </div>
                                <div id="passwordMatch" class="form-text"></div>
                            </div>
                        </div>

                        <!-- Terms and Conditions -->
                        <div class="mb-3 form-check">
                            <input type="checkbox" class="form-check-input" id="terms" name="terms" required>
                            <label class="form-check-label" for="terms">
                                I agree to the <a href="#" class="text-decoration-none">Terms and Conditions</a> *
                            </label>
                        </div>

                        <!-- Register Button -->
                        <div class="d-grid mb-3">
                            <button type="submit" class="btn btn-success btn-register">
                                <i class="bi bi-person-plus"></i> Create Account
                            </button>
                        </div>

                        <!-- Login Link -->
                        <div class="text-center">
                            <small class="text-muted">
                                Already have an account?
                                <a href="${pageContext.request.contextPath}/login" class="text-decoration-none">
                                    Login here
                                </a>
                            </small>
                        </div>
                    </form>
                </div>

                <!-- Footer -->
                <div class="bg-light text-center py-3">
                    <small class="text-muted">
                        &copy; 2024 Pahana Edu. Advanced Programming Project.
                    </small>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<script>
    // Toggle password visibility
    function togglePassword(fieldId, iconId) {
        const passwordField = document.getElementById(fieldId);
        const toggleIcon = document.getElementById(iconId);

        if (passwordField.type === 'password') {
            passwordField.type = 'text';
            toggleIcon.className = 'bi bi-eye-slash';
        } else {
            passwordField.type = 'password';
            toggleIcon.className = 'bi bi-eye';
        }
    }

    // Password strength checker
    document.getElementById('password').addEventListener('input', function() {
        const password = this.value;
        const strengthBar = document.getElementById('passwordStrength');
        let strength = 0;

        if (password.length >= 8) strength++;
        if (/[a-z]/.test(password)) strength++;
        if (/[A-Z]/.test(password)) strength++;
        if (/[0-9]/.test(password)) strength++;
        if (/[^A-Za-z0-9]/.test(password)) strength++;

        switch(strength) {
            case 0:
            case 1:
                strengthBar.className = 'password-strength bg-danger';
                break;
            case 2:
                strengthBar.className = 'password-strength bg-warning';
                break;
            case 3:
                strengthBar.className = 'password-strength bg-info';
                break;
            case 4:
            case 5:
                strengthBar.className = 'password-strength bg-success';
                break;
        }
    });

    // Password confirmation checker
    document.getElementById('confirmPassword').addEventListener('input', function() {
        const password = document.getElementById('password').value;
        const confirmPassword = this.value;
        const matchDiv = document.getElementById('passwordMatch');

        if (confirmPassword === '') {
            matchDiv.textContent = '';
            matchDiv.className = 'form-text';
        } else if (password === confirmPassword) {
            matchDiv.textContent = 'Passwords match ✓';
            matchDiv.className = 'form-text text-success';
        } else {
            matchDiv.textContent = 'Passwords do not match ✗';
            matchDiv.className = 'form-text text-danger';
        }
    });

    // Form validation
    document.getElementById('registerForm').addEventListener('submit', function(e) {
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;
        const username = document.getElementById('username').value;
        const email = document.getElementById('email').value;
        const fullName = document.getElementById('fullName').value;
        const terms = document.getElementById('terms').checked;

        // Basic validation
        if (!fullName.trim() || !username.trim() || !email.trim() || !password || !confirmPassword) {
            e.preventDefault();
            alert('Please fill in all required fields.');
            return false;
        }

        // Username validation
        if (username.length < 3 || username.length > 20) {
            e.preventDefault();
            alert('Username must be between 3 and 20 characters.');
            return false;
        }

        if (!/^[a-zA-Z0-9]+$/.test(username)) {
            e.preventDefault();
            alert('Username can only contain letters and numbers.');
            return false;
        }

        // Password validation
        if (password.length < 8) {
            e.preventDefault();
            alert('Password must be at least 8 characters long.');
            return false;
        }

        if (password !== confirmPassword) {
            e.preventDefault();
            alert('Passwords do not match.');
            return false;
        }

        // Terms validation
        if (!terms) {
            e.preventDefault();
            alert('Please accept the terms and conditions.');
            return false;
        }
    });

    // Auto-hide alerts after 5 seconds
    setTimeout(function() {
        var alerts = document.querySelectorAll('.alert');
        alerts.forEach(function(alert) {
            var bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        });
    }, 5000);

    // Focus on first field when page loads
    window.addEventListener('load', function() {
        document.getElementById('fullName').focus();
    });
</script>

</body>
</html>