<%--
  Created by IntelliJ IDEA.
  User: MOHAMED
  Date: 16/08/2025
  Time: 11:50
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Logout" />

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
        .logout-container {
            background: white;
            border-radius: 15px;
            box-shadow: 0 15px 35px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        .logout-header {
            background: linear-gradient(135deg, #dc3545, #c82333);
            color: white;
            text-align: center;
            padding: 2rem;
        }
        .logout-success {
            background: linear-gradient(135deg, #28a745, #20c997);
        }
        .pulse {
            animation: pulse 2s infinite;
        }
        @keyframes pulse {
            0% { transform: scale(1); }
            50% { transform: scale(1.1); }
            100% { transform: scale(1); }
        }
        .countdown {
            font-size: 1.5rem;
            font-weight: bold;
            color: #007bff;
        }
    </style>
</head>
<body>

<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-6 col-lg-5">
            <div class="logout-container">
                <!-- Check if logout was successful -->
                <c:choose>
                    <c:when test="${param.success == 'true' or empty sessionScope.currentUser}">
                        <!-- Logout Success -->
                        <div class="logout-header logout-success">
                            <i class="bi bi-check-circle fs-1 mb-3 pulse"></i>
                            <h3 class="mb-0">Logout Successful</h3>
                            <p class="mb-0">You have been safely logged out</p>
                        </div>

                        <div class="p-4 text-center">
                            <div class="mb-4">
                                <h5 class="text-success">
                                    <i class="bi bi-shield-check"></i> Session Ended
                                </h5>
                                <p class="text-muted">
                                    Your session has been securely terminated. All data has been cleared from this device.
                                </p>
                            </div>

                            <!-- Security Tips -->
                            <div class="alert alert-info" role="alert">
                                <h6 class="alert-heading">
                                    <i class="bi bi-lightbulb"></i> Security Tips
                                </h6>
                                <ul class="text-start small mb-0">
                                    <li>Close all browser windows if on a shared computer</li>
                                    <li>Clear browser cache and cookies for extra security</li>
                                    <li>Never leave your account logged in on public computers</li>
                                </ul>
                            </div>

                            <!-- Redirect countdown -->
                            <div class="mb-4">
                                <p class="text-muted">
                                    Redirecting to login page in <span class="countdown" id="countdown">10</span> seconds...
                                </p>
                                <div class="progress" style="height: 6px;">
                                    <div class="progress-bar bg-primary" id="progressBar" style="width: 100%"></div>
                                </div>
                            </div>

                            <!-- Action Buttons -->
                            <div class="d-grid gap-2">
                                <a href="${pageContext.request.contextPath}/login" class="btn btn-primary">
                                    <i class="bi bi-box-arrow-in-right"></i> Login Again
                                </a>
                                <a href="${pageContext.request.contextPath}/" class="btn btn-outline-secondary">
                                    <i class="bi bi-house"></i> Go to Home
                                </a>
                            </div>
                        </div>
                    </c:when>

                    <c:otherwise>
                        <!-- Logout Confirmation -->
                        <div class="logout-header">
                            <i class="bi bi-box-arrow-right fs-1 mb-3"></i>
                            <h3 class="mb-0">Confirm Logout</h3>
                            <p class="mb-0">Are you sure you want to sign out?</p>
                        </div>

                        <div class="p-4">
                            <div class="alert alert-warning" role="alert">
                                <i class="bi bi-exclamation-triangle"></i>
                                <strong>Warning:</strong> You are about to end your current session.
                            </div>

                            <!-- User Info -->
                            <div class="mb-4 p-3 bg-light rounded">
                                <div class="d-flex align-items-center">
                                    <i class="bi bi-person-circle fs-2 text-muted me-3"></i>
                                    <div>
                                        <h6 class="mb-1">${sessionScope.currentUser.fullName}</h6>
                                        <small class="text-muted">
                                            Role: ${sessionScope.currentUser.displayRole}
                                        </small>
                                    </div>
                                </div>
                            </div>

                            <!-- Logout Form -->
                            <form action="${pageContext.request.contextPath}/logout" method="post" id="logoutForm">
                                <div class="d-grid gap-2">
                                    <button type="submit" class="btn btn-danger">
                                        <i class="bi bi-box-arrow-right"></i> Yes, Logout
                                    </button>
                                    <button type="button" class="btn btn-secondary" onclick="goBack()">
                                        <i class="bi bi-arrow-left"></i> Cancel
                                    </button>
                                </div>
                            </form>

                            <!-- Additional Options -->
                            <div class="mt-4 text-center">
                                <small class="text-muted">
                                    <a href="${pageContext.request.contextPath}/profile" class="text-decoration-none">
                                        Update Profile
                                    </a> |
                                    <a href="${pageContext.request.contextPath}/help" class="text-decoration-none">
                                        Help
                                    </a>
                                </small>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>

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
    // Redirect countdown functionality (only if logout was successful)
    const isLogoutSuccessful = ${param.success == 'true' or empty sessionScope.currentUser};

    if (isLogoutSuccessful) {
        let countdown = 10;
        const countdownElement = document.getElementById('countdown');
        const progressBar = document.getElementById('progressBar');

        const countdownTimer = setInterval(function() {
            countdown--;
            countdownElement.textContent = countdown;

            // Update progress bar
            const progressPercentage = (countdown / 10) * 100;
            progressBar.style.width = progressPercentage + '%';

            if (countdown <= 0) {
                clearInterval(countdownTimer);
                window.location.href = '${pageContext.request.contextPath}/login';
            }
        }, 1000);

        // Allow user to click anywhere to redirect immediately
        document.addEventListener('click', function() {
            clearInterval(countdownTimer);
            window.location.href = '${pageContext.request.contextPath}/login';
        });
    }

    // Go back function for logout confirmation
    function goBack() {
        // Try to go back in history, fallback to dashboard
        if (document.referrer && document.referrer !== window.location.href) {
            window.history.back();
        } else {
            window.location.href = '${pageContext.request.contextPath}/dashboard';
        }
    }

    // Logout form submission with confirmation
    const logoutForm = document.getElementById('logoutForm');
    if (logoutForm) {
        logoutForm.addEventListener('submit', function(e) {
            // Show a brief loading state
            const submitButton = this.querySelector('button[type="submit"]');
            const originalText = submitButton.innerHTML;

            submitButton.innerHTML = '<i class="bi bi-hourglass-split"></i> Logging out...';
            submitButton.disabled = true;

            // Allow form to submit normally
            setTimeout(function() {
                submitButton.innerHTML = originalText;
                submitButton.disabled = false;
            }, 2000);
        });
    }

    // Keyboard shortcuts
    document.addEventListener('keydown', function(e) {
        // ESC key to cancel logout (if on confirmation page)
        if (e.key === 'Escape' && !isLogoutSuccessful) {
            goBack();
        }

        // Enter key to confirm logout (if on confirmation page)
        if (e.key === 'Enter' && !isLogoutSuccessful && logoutForm) {
            logoutForm.submit();
        }
    });

    // Clear any sensitive data from memory/storage
    if (isLogoutSuccessful) {
        // Clear session storage
        if (typeof(Storage) !== "undefined") {
            sessionStorage.clear();
        }

        // Clear any cached form data
        const forms = document.querySelectorAll('form');
        forms.forEach(function(form) {
            form.reset();
        });
    }
</script>

</body>
</html>