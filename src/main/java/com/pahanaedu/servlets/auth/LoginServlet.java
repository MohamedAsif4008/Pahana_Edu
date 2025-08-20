package com.pahanaedu.servlets.auth;

import com.pahanaedu.models.User;
import com.pahanaedu.service.interfaces.UserService;
import com.pahanaedu.service.impl.UserServiceImpl;
import com.pahanaedu.servlets.common.BaseServlet;
import com.pahanaedu.util.ValidationUtils;
import com.pahanaedu.util.PasswordUtil;
import com.pahanaedu.util.PasswordMigrationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet for handling user authentication (login)
 * Manages user login process and session creation
 *
 * Design Patterns Used:
 * - MVC Pattern: Controller for authentication
 * - Command Pattern: Different actions based on request
 * - Session Pattern: User session management
 * - Security Pattern: Comprehensive security measures
 *
 * @author Pahana Edu Development Team
 * @version 1.0
 */
@WebServlet(name = "LoginServlet", urlPatterns = {
        "/login",
        "/auth/login",
        "/forgot-password",
        "/auth/forgot-password",
        "/password-reset",
        "/auth/password-reset"
})
public class LoginServlet extends BaseServlet {

    private UserService userService;

    // Track failed login attempts (in production, use Redis or database)
    private static final Map<String, Integer> failedAttempts = new HashMap<>();
    private static final Map<String, Long> lastAttemptTime = new HashMap<>();

    // Security constants
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCKOUT_TIME = 15 * 60 * 1000; // 15 minutes
    private static final long RATE_LIMIT_TIME = 1000; // 1 second between attempts

    @Override
    public void init() throws ServletException {
        super.init();
        this.userService = new UserServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Check if user is already logged in
            if (isUserLoggedIn(request)) {
                // Redirect to dashboard if already logged in
                redirectTo(response, request.getContextPath() + "/dashboard");
                return;
            }

            // Get action parameter
            String action = getParameter(request, PARAM_ACTION, "show");

            switch (action.toLowerCase()) {
                case "show":
                    showLoginPage(request, response);
                    break;
                case "forgot":
                    showForgotPasswordPage(request, response);
                    break;
                case "reset":
                    showPasswordResetPage(request, response);
                    break;
                default:
                    showLoginPage(request, response);
                    break;
            }

        } catch (Exception e) {
            handleException(request, response, e);
        }
    }

    /**
     * Handle POST requests - Process login
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Get action parameter
            String action = getParameter(request, PARAM_ACTION, "login");

            switch (action.toLowerCase()) {
                case "login":
                    processLogin(request, response);
                    break;
                case "forgot":
                    processForgotPassword(request, response);
                    break;
                case "reset":
                    processPasswordReset(request, response);
                    break;
                default:
                    processLogin(request, response);
                    break;
            }

        } catch (Exception e) {
            handleException(request, response, e);
        }
    }

    /**
     * Display login page
     */
    private void showLoginPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Generate CSRF token for security
        String csrfToken = generateCSRFToken(request);
        request.setAttribute("csrfToken", csrfToken);

        // Check for redirect URL (for post-login redirect)
        String redirectUrl = request.getParameter("redirect");
        if (ValidationUtils.isNotEmpty(redirectUrl)) {
            request.setAttribute("redirectUrl", redirectUrl);
        }

        // Check for logout success message
        String logout = request.getParameter("logout");
        if ("success".equals(logout)) {
            setSuccessMessage(request, "You have been successfully logged out.");
        } else if ("error".equals(logout)) {
            setErrorMessage(request, "There was an error during logout. Please try again.");
        }

        // Check for session timeout
        String timeout = request.getParameter("timeout");
        if ("true".equals(timeout)) {
            setInfoMessage(request, "Your session has expired. Please log in again.");
        }

        // Check for security alerts
        String security = request.getParameter("security");
        if ("violation".equals(security)) {
            setErrorMessage(request, "Security violation detected. Please log in again.");
        } else if ("true".equals(security)) {
            setErrorMessage(request, "For security reasons, you have been logged out. Please log in again.");
        }

        // Forward to login JSP
        forwardToJSP(request, response, "auth/login.jsp");
    }

    /**
     * Display forgot password page
     */
    private void showForgotPasswordPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Generate CSRF token
        String csrfToken = generateCSRFToken(request);
        request.setAttribute("csrfToken", csrfToken);

        // Forward to forgot password JSP
        forwardToJSP(request, response, "auth/forgot-password.jsp");
    }

    /**
     * Display password reset page
     */
    private void showPasswordResetPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String token = getSanitizedParameter(request, "token");
        if (!ValidationUtils.isNotEmpty(token)) {
            setErrorMessage(request, "Invalid or missing reset token.");
            showLoginPage(request, response);
            return;
        }

        // In production, validate the token against database
        // For now, just show the reset form
        request.setAttribute("resetToken", token);
        request.setAttribute("csrfToken", generateCSRFToken(request));

        forwardToJSP(request, response, "auth/password-reset.jsp");
    }

    /**
     * Process user login
     */
    private void processLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Validate CSRF token
        if (!isValidCSRFToken(request)) {
            setErrorMessage(request, "Invalid request. Please try again.");
            showLoginPage(request, response);
            return;
        }

        // Get client IP for security tracking
        String clientIP = getClientIP(request);

        // Check for rate limiting
        if (isRateLimited(request)) {
            setErrorMessage(request, "Too many login attempts. Please wait before trying again.");
            showLoginPage(request, response);
            return;
        }

        // Get login parameters
        String username = getSanitizedParameter(request, "username");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");
        boolean remember = "on".equals(rememberMe) || "true".equals(rememberMe);

        // Validate required fields
        if (!validateRequiredParams(request, "username", "password")) {
            setErrorMessage(request, "Username and password are required.");
            showLoginPage(request, response);
            return;
        }

        // Validate input format
        if (!ValidationUtils.isValidUsername(username)) {
            setErrorMessage(request, "Invalid username format.");
            recordFailedAttempt(clientIP);
            showLoginPage(request, response);
            return;
        }

        // Check if account is locked
        if (isAccountLocked(clientIP)) {
            setErrorMessage(request, "Account temporarily locked due to multiple failed attempts. Please try again later.");
            showLoginPage(request, response);
            return;
        }

        try {
            // Attempt authentication
            User user = userService.authenticateUser(username, password);

            if (user != null) {
                // Authentication successful
                clearFailedAttempts(clientIP);
                handleSuccessfulLogin(request, response, user, remember);
            } else {
                // Authentication failed
                recordFailedAttempt(clientIP);
                handleFailedLogin(request, response, username);
            }

        } catch (Exception e) {
            System.err.println("Login error for user " + username + ": " + e.getMessage());
            recordFailedAttempt(clientIP);
            setErrorMessage(request, "An error occurred during login. Please try again.");
            showLoginPage(request, response);
        }
    }

    /**
     * Handle successful login
     */
    private void handleSuccessfulLogin(HttpServletRequest request, HttpServletResponse response,
                                       User user, boolean rememberMe) throws IOException {

        // Set user in session
        setCurrentUser(request, user);

        // Set login time for session duration tracking
        request.getSession().setAttribute("loginTime", System.currentTimeMillis());

        // Store client IP for session validation
        request.getSession().setAttribute("clientIP", getClientIP(request));

        // Handle "Remember Me" functionality
        if (rememberMe) {
            // Set longer session timeout (7 days)
            request.getSession().setMaxInactiveInterval(7 * 24 * 60 * 60);

            // In production, you would also set a persistent cookie
            // with a secure token linked to the user
            setRememberMeCookie(response, user);
        }

        // Log successful login
        logAction(request, "LOGIN_SUCCESS", "User logged in successfully");

        // Determine redirect URL
        String redirectUrl = request.getParameter("redirectUrl");
        if (!ValidationUtils.isNotEmpty(redirectUrl)) {
            // Default redirect based on user role
            if (user.isAdmin()) {
                redirectUrl = request.getContextPath() + "/dashboard";
            } else {
                redirectUrl = request.getContextPath() + "/dashboard";
            }
        }

        // Redirect to appropriate page
        redirectTo(response, redirectUrl);
    }

    /**
     * Handle failed login
     */
    private void handleFailedLogin(HttpServletRequest request, HttpServletResponse response, String username)
            throws ServletException, IOException {

        // Log failed login attempt
        logAction(request, "LOGIN_FAILED", "Failed login attempt for username: " + username);

        // Set error message
        setErrorMessage(request, "Invalid username or password. Please try again.");

        // Show login page again
        showLoginPage(request, response);
    }

    /**
     * Process forgot password request
     */
    private void processForgotPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Validate CSRF token
        if (!isValidCSRFToken(request)) {
            setErrorMessage(request, "Invalid request. Please try again.");
            showForgotPasswordPage(request, response);
            return;
        }

        // Get username or email
        String usernameOrEmail = getSanitizedParameter(request, "usernameOrEmail");

        if (!ValidationUtils.isNotEmpty(usernameOrEmail)) {
            setErrorMessage(request, "Username or email is required.");
            showForgotPasswordPage(request, response);
            return;
        }

        try {
            // Find user by username or email
            User user = null;

            if (ValidationUtils.isValidEmail(usernameOrEmail)) {
                // Search by email (would need additional DAO method)
                setInfoMessage(request, "If an account with this email exists, password reset instructions will be sent.");
            } else if (ValidationUtils.isValidUsername(usernameOrEmail)) {
                // Search by username
                user = userService.findUserByUsername(usernameOrEmail);
                if (user != null) {
                    // Generate reset token (in production, store in database)
                    String resetToken = generatePasswordResetToken(user);

                    // In production, send email with reset link
                    // For demo, just log it
                    System.out.println("Password reset token for " + user.getUsername() + ": " + resetToken);

                    setInfoMessage(request, "Password reset instructions have been sent to your registered email.");
                } else {
                    setInfoMessage(request, "If an account with this username exists, password reset instructions will be sent.");
                }
            } else {
                setErrorMessage(request, "Please enter a valid username or email address.");
                showForgotPasswordPage(request, response);
                return;
            }

            // Log password reset request
            logAction(request, "PASSWORD_RESET_REQUEST", "Password reset requested for: " + usernameOrEmail);

            // Redirect back to login with info message
            request.getSession().setAttribute("infoMessage",
                    "If an account exists, password reset instructions have been sent to the registered email.");
            redirectTo(response, request.getContextPath() + "/login");

        } catch (Exception e) {
            System.err.println("Forgot password error: " + e.getMessage());
            setErrorMessage(request, "An error occurred. Please try again.");
            showForgotPasswordPage(request, response);
        }
    }

    /**
     * Process password reset
     */
    private void processPasswordReset(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isValidCSRFToken(request)) {
            setErrorMessage(request, "Invalid request.");
            showLoginPage(request, response);
            return;
        }

        String token = getSanitizedParameter(request, "token");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (!validateRequiredParams(request, "token", "newPassword", "confirmPassword")) {
            setErrorMessage(request, "All fields are required.");
            showPasswordResetPage(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            setErrorMessage(request, "Passwords do not match.");
            showPasswordResetPage(request, response);
            return;
        }

        // In production, validate token and update password
        // For demo, just show success
        setSuccessMessage(request, "Password has been reset successfully. Please log in with your new password.");
        logAction(request, "PASSWORD_RESET", "Password reset completed");

        redirectTo(response, request.getContextPath() + "/login");
    }

    /**
     * Check for brute force protection
     */
    private boolean isAccountLocked(String clientIP) {
        Integer attempts = failedAttempts.get(clientIP);
        if (attempts != null && attempts >= MAX_FAILED_ATTEMPTS) {
            Long lastAttempt = lastAttemptTime.get(clientIP);
            if (lastAttempt != null && System.currentTimeMillis() - lastAttempt < LOCKOUT_TIME) {
                return true;
            } else {
                // Lockout period expired, clear attempts
                clearFailedAttempts(clientIP);
            }
        }
        return false;
    }

    /**
     * Rate limiting for login attempts
     */
    private boolean isRateLimited(HttpServletRequest request) {
        String clientIP = getClientIP(request);
        Long lastAttempt = lastAttemptTime.get(clientIP);

        if (lastAttempt != null && System.currentTimeMillis() - lastAttempt < RATE_LIMIT_TIME) {
            return true;
        }

        lastAttemptTime.put(clientIP, System.currentTimeMillis());
        return false;
    }

    /**
     * Record failed login attempt
     */
    private void recordFailedAttempt(String clientIP) {
        failedAttempts.merge(clientIP, 1, Integer::sum);
        lastAttemptTime.put(clientIP, System.currentTimeMillis());

        System.out.println("SECURITY: Failed login attempt from IP: " + clientIP +
                " (Total attempts: " + failedAttempts.get(clientIP) + ")");
    }

    /**
     * Clear failed attempts after successful login
     */
    private void clearFailedAttempts(String clientIP) {
        failedAttempts.remove(clientIP);
        lastAttemptTime.remove(clientIP);
    }

    /**
     * Set Remember Me cookie
     */
    private void setRememberMeCookie(HttpServletResponse response, User user) {
        try {
            // Generate secure token (in production, store in database)
            String token = generateRememberMeToken(user);

            jakarta.servlet.http.Cookie rememberCookie = new jakarta.servlet.http.Cookie("rememberToken", token);
            rememberCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
            rememberCookie.setPath("/");
            rememberCookie.setHttpOnly(true);
            rememberCookie.setSecure(true); // Use HTTPS in production

            response.addCookie(rememberCookie);

        } catch (Exception e) {
            System.err.println("Error setting remember me cookie: " + e.getMessage());
        }
    }

    /**
     * Generate Remember Me token
     */
    private String generateRememberMeToken(User user) {
        // In production, use a cryptographically secure method
        return java.util.UUID.randomUUID().toString() + "_" + user.getUserId();
    }

    /**
     * Generate password reset token
     */
    private String generatePasswordResetToken(User user) {
        // In production, use a cryptographically secure method and store in database
        return java.util.UUID.randomUUID().toString() + "_" + System.currentTimeMillis();
    }

    /**
     * Validate session security
     */
    private boolean isValidSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        // Check session timeout
        long now = System.currentTimeMillis();
        Long lastAccess = (Long) session.getAttribute("lastAccess");

        if (lastAccess != null) {
            long sessionTimeout = session.getMaxInactiveInterval() * 1000L;
            if (now - lastAccess > sessionTimeout) {
                session.invalidate();
                return false;
            }
        }

        // Update last access time
        session.setAttribute("lastAccess", now);
        return true;
    }

    /**
     * Clean up expired failed attempts (call periodically)
     */
    public static void cleanupExpiredAttempts() {
        long now = System.currentTimeMillis();
        lastAttemptTime.entrySet().removeIf(entry -> now - entry.getValue() > LOCKOUT_TIME);

        // Remove corresponding failed attempts
        lastAttemptTime.keySet().forEach(ip -> {
            if (!lastAttemptTime.containsKey(ip)) {
                failedAttempts.remove(ip);
            }
        });
    }

    @Override
    public void destroy() {
        super.destroy();
        this.userService = null;

        // Clean up static maps
        failedAttempts.clear();
        lastAttemptTime.clear();
    }
}