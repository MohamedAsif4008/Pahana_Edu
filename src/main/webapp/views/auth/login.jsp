<%--
  Created by IntelliJ IDEA.
  User: MOHAMED
  Date: 16/08/2025
  Time: 11:30
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Login" />

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
            display: flex;
            align-items: center;
        }
        .login-container {
            background: white;
            border-radius: 15px;
            box-shadow: 0 15px 35px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        .login-header {
            background: linear-gradient(135deg, #0d6efd, #0056b3);
            color: white;
            text-align: center;
            padding: 2rem;
        }
        .form-control:focus {
            border-color: #0d6efd;
            box-shadow: 0 0 0 0.2rem rgba(13, 110, 253, 0.25);
        }
        .btn-login {
            background: linear-gradient(135deg, #0d6efd, #0056b3);
            border: none;
            padding: 0.75rem;
            font-weight: 500;
        }
        .btn-login:hover {
            background: linear-gradient(135deg, #0056b3, #004085);
        }
    </style>
</head>
<body>

<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-6 col-lg-4">
            <div class="login-container">
                <!-- Header -->
                <div class="login-header">
                    <i class="bi bi-shop fs-1 mb-3"></i>
                    <h3 class="mb-0">Pahana Edu</h3>
                    <p class="mb-0">Billing System Login</p>
                </div>

                <!-- Login Form -->
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

                    <!-- Display info messages -->
                    <c:if test="${not empty infoMessage}">
                        <div class="alert alert-info alert-dismissible fade show" role="alert">
                            <i class="bi bi-info-circle"></i> ${infoMessage}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <form action="/Pahana_Edu/login" method="post" id="loginForm">
                        <input type="hidden" name="csrfToken" value="${csrfToken}">



                        <!-- Username Field -->
                        <div class="mb-3">
                            <label for="username" class="form-label">
                                <i class="bi bi-person"></i> Username
                            </label>
                            <input type="text"
                                   class="form-control"
                                   id="username"
                                   name="username"
                                   value="${param.username}"
                                   placeholder="Enter your username"
                                   required>
                        </div>

                        <!-- Password Field -->
                        <div class="mb-3">
                            <label for="password" class="form-label">
                                <i class="bi bi-lock"></i> Password
                            </label>
                            <div class="input-group">
                                <input type="password"
                                       class="form-control"
                                       id="password"
                                       name="password"
                                       placeholder="Enter your password"
                                       required>
                                <button class="btn btn-outline-secondary"
                                        type="button"
                                        onclick="togglePassword()">
                                    <i class="bi bi-eye" id="passwordToggleIcon"></i>
                                </button>
                            </div>
                        </div>

                        <!-- Remember Me -->
                        <div class="mb-3 form-check">
                            <input type="checkbox" class="form-check-input" id="rememberMe" name="rememberMe">
                            <label class="form-check-label" for="rememberMe">
                                Remember me
                            </label>
                        </div>

                        <!-- Login Button -->
                        <div class="d-grid mb-3">
                            <button type="submit" class="btn btn-primary btn-login">
                                <i class="bi bi-box-arrow-in-right"></i> Login
                            </button>
                        </div>




                    </form>
                </div>

                <!-- Footer -->
                <div class="bg-light text-center py-3">
                    <small class="text-muted">
                        &copy; 2024 Pahana Edu.
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
    function togglePassword() {
        const passwordField = document.getElementById('password');
        const toggleIcon = document.getElementById('passwordToggleIcon');

        if (passwordField.type === 'password') {
            passwordField.type = 'text';
            toggleIcon.className = 'bi bi-eye-slash';
        } else {
            passwordField.type = 'password';
            toggleIcon.className = 'bi bi-eye';
        }
    }

    // Form validation
    document.getElementById('loginForm').addEventListener('submit', function(e) {
        const username = document.getElementById('username').value.trim();
        const password = document.getElementById('password').value.trim();

        if (username === '' || password === '') {
            e.preventDefault();
            alert('Please fill in all required fields.');
            return false;
        }

        if (username.length < 3) {
            e.preventDefault();
            alert('Username must be at least 3 characters long.');
            return false;
        }

        if (password.length < 6) {
            e.preventDefault();
            alert('Password must be at least 6 characters long.');
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

    // Focus on username field when page loads
    window.addEventListener('load', function() {
        document.getElementById('username').focus();
    });
</script>

</body>
</html>