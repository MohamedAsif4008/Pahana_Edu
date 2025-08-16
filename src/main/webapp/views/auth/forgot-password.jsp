<%--
  Created by IntelliJ IDEA.
  User: MOHAMED
  Date: 16/08/2025
  Time: 11:40
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Forgot Password" />

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
        .forgot-password-container {
            background: white;
            border-radius: 15px;
            box-shadow: 0 15px 35px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        .forgot-password-header {
            background: linear-gradient(135deg, #ffc107, #fd7e14);
            color: white;
            text-align: center;
            padding: 2rem;
        }
        .form-control:focus {
            border-color: #ffc107;
            box-shadow: 0 0 0 0.2rem rgba(255, 193, 7, 0.25);
        }
        .btn-reset {
            background: linear-gradient(135deg, #ffc107, #fd7e14);
            border: none;
            padding: 0.75rem;
            font-weight: 500;
            color: #000;
        }
        .btn-reset:hover {
            background: linear-gradient(135deg, #fd7e14, #dc3545);
            color: white;
        }
        .steps-container {
            background: #f8f9fa;
            border-radius: 10px;
            padding: 1.5rem;
            margin: 1rem 0;
        }
    </style>
</head>
<body>

<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-6 col-lg-5">
            <div class="forgot-password-container">
                <!-- Header -->
                <div class="forgot-password-header">
                    <i class="bi bi-key fs-1 mb-3"></i>
                    <h3 class="mb-0">Reset Password</h3>
                    <p class="mb-0">Don't worry, we'll help you get back in</p>
                </div>

                <!-- Reset Form -->
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

                    <!-- Instructions -->
                    <div class="steps-container">
                        <h6 class="text-muted mb-3">
                            <i class="bi bi-info-circle"></i> How it works:
                        </h6>
                        <ol class="text-muted small mb-0">
                            <li>Enter your username or email address</li>
                            <li>We'll verify your identity with security questions</li>
                            <li>Create a new password</li>
                            <li>Login with your new credentials</li>
                        </ol>
                    </div>

                    <form action="${pageContext.request.contextPath}/forgot-password" method="post" id="forgotPasswordForm">
                        <!-- Step 1: Username/Email Input -->
                        <c:if test="${empty step or step == 'identify'}">
                            <div class="mb-3">
                                <label for="identifier" class="form-label">
                                    <i class="bi bi-person"></i> Username or Email Address
                                </label>
                                <input type="text"
                                       class="form-control"
                                       id="identifier"
                                       name="identifier"
                                       value="${param.identifier}"
                                       placeholder="Enter your username or email"
                                       required>
                                <small class="form-text text-muted">
                                    We'll use this to verify your identity
                                </small>
                            </div>

                            <div class="d-grid mb-3">
                                <button type="submit" class="btn btn-warning btn-reset">
                                    <i class="bi bi-search"></i> Find My Account
                                </button>
                            </div>
                        </c:if>

                        <!-- Step 2: Security Questions -->
                        <c:if test="${step == 'verify'}">
                            <input type="hidden" name="step" value="verify">
                            <input type="hidden" name="identifier" value="${identifier}">

                            <div class="alert alert-info" role="alert">
                                <i class="bi bi-shield-check"></i>
                                Account found for: <strong>${maskedIdentifier}</strong>
                            </div>

                            <div class="mb-3">
                                <label for="securityAnswer" class="form-label">
                                    <i class="bi bi-question-circle"></i> Security Question
                                </label>
                                <div class="form-control-plaintext bg-light p-3 rounded">
                                        ${securityQuestion}
                                </div>
                                <input type="text"
                                       class="form-control mt-2"
                                       id="securityAnswer"
                                       name="securityAnswer"
                                       placeholder="Enter your answer"
                                       required>
                                <small class="form-text text-muted">
                                    Answer is case-insensitive
                                </small>
                            </div>

                            <div class="d-grid mb-3">
                                <button type="submit" class="btn btn-warning btn-reset">
                                    <i class="bi bi-shield-check"></i> Verify Answer
                                </button>
                            </div>
                        </c:if>

                        <!-- Step 3: New Password -->
                        <c:if test="${step == 'reset'}">
                            <input type="hidden" name="step" value="reset">
                            <input type="hidden" name="token" value="${resetToken}">

                            <div class="alert alert-success" role="alert">
                                <i class="bi bi-check-circle"></i>
                                Identity verified! Now create a new password.
                            </div>

                            <div class="mb-3">
                                <label for="newPassword" class="form-label">
                                    <i class="bi bi-lock"></i> New Password
                                </label>
                                <div class="input-group">
                                    <input type="password"
                                           class="form-control"
                                           id="newPassword"
                                           name="newPassword"
                                           placeholder="Enter new password"
                                           required>
                                    <button class="btn btn-outline-secondary"
                                            type="button"
                                            onclick="togglePassword('newPassword', 'newPasswordToggleIcon')">
                                        <i class="bi bi-eye" id="newPasswordToggleIcon"></i>
                                    </button>
                                </div>
                                <div class="password-strength bg-secondary" id="passwordStrength"></div>
                                <small class="form-text text-muted">
                                    Password must be at least 8 characters with letters and numbers
                                </small>
                            </div>

                            <div class="mb-3">
                                <label for="confirmNewPassword" class="form-label">
                                    <i class="bi bi-lock-fill"></i> Confirm New Password
                                </label>
                                <div class="input-group">
                                    <input type="password"
                                           class="form-control"
                                           id="confirmNewPassword"
                                           name="confirmNewPassword"
                                           placeholder="Confirm new password"
                                           required>
                                    <button class="btn btn-outline-secondary"
                                            type="button"
                                            onclick="togglePassword('confirmNewPassword', 'confirmPasswordToggleIcon')">
                                        <i class="bi bi-eye" id="confirmPasswordToggleIcon"></i>
                                    </button>
                                </div>
                                <div id="passwordMatch" class="form-text"></div>
                            </div>

                            <div class="d-grid mb-3">
                                <button type="submit" class="btn btn-warning btn-reset">
                                    <i class="bi bi-key"></i> Reset Password
                                </button>
                            </div>
                        </c:if>
                    </form>

                    <!-- Navigation Links -->
                    <div class="text-center">
                        <div class="d-flex justify-content-between">
                            <a href="${pageContext.request.contextPath}/login" class="btn btn-link text-decoration-none">
                                <i class="bi bi-arrow-left"></i> Back to Login
                            </a>
                            <c:if test="${step == 'verify' or step == 'reset'}">
                                <a href="${pageContext.request.contextPath}/forgot-password" class="btn btn-link text-decoration-none">
                                    <i class="bi bi-arrow-clockwise"></i> Start Over
                                </a>
                            </c:if>
                        </div>
                    </div>

                    <!-- Contact Support -->
                    <div class="mt-4 p-3 bg-light rounded">
                        <h6 class="text-muted mb-2">
                            <i class="bi bi-headset"></i> Need Help?
                        </h6>
                        <small class="text-muted">
                            If you're having trouble accessing your account, please contact the system administrator or IT support for assistance.
                        </small>
                    </div>
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

    // Password strength checker (only if new password field exists)
    const newPasswordField = document.getElementById('newPassword');
    if (newPasswordField) {
        newPasswordField.addEventListener('input', function() {
            const password = this.value;
            const strengthBar = document.getElementById('passwordStrength');
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
    }

    // Password confirmation checker (only if confirm password field exists)
    const confirmPasswordField = document.getElementById('confirmNewPassword');
    if (confirmPasswordField) {
        confirmPasswordField.addEventListener('input', function() {
            const password = document.getElementById('newPassword').value;
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
    }

    // Form validation
    document.getElementById('forgotPasswordForm').addEventListener('submit', function(e) {
        const step = '${step}';

        if (step === 'reset') {
            const newPassword = document.getElementById('newPassword').value;
            const confirmPassword = document.getElementById('confirmNewPassword').value;

            if (newPassword.length < 8) {
                e.preventDefault();
                alert('Password must be at least 8 characters long.');
                return false;
            }

            if (newPassword !== confirmPassword) {
                e.preventDefault();
                alert('Passwords do not match.');
                return false;
            }
        }
    });

    // Auto-hide alerts after 5 seconds
    setTimeout(function() {
        var alerts = document.querySelectorAll('.alert:not(.alert-info):not(.alert-success)');
        alerts.forEach(function(alert) {
            if (!alert.classList.contains('alert-info') && !alert.classList.contains('alert-success')) {
                var bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            }
        });
    }, 5000);

    // Focus on appropriate field when page loads
    window.addEventListener('load', function() {
        const step = '${step}';

        if (step === 'verify') {
            document.getElementById('securityAnswer').focus();
        } else if (step === 'reset') {
            document.getElementById('newPassword').focus();
        } else {
            document.getElementById('identifier').focus();
        }
    });
</script>

</body>
</html>