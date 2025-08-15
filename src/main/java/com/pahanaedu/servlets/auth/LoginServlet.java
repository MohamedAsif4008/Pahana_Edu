package com.pahanaedu.servlets.auth;

import com.pahanaedu.models.User;
import com.pahanaedu.service.interfaces.UserService;
import com.pahanaedu.service.impl.UserServiceImpl;
import com.pahanaedu.util.ValidationUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet for handling user authentication (login)
 * Manages user login process and session creation
 *
 * Design Patterns Used:
 * - MVC Pattern: Controller for authentication
 * - Command Pattern: Different actions based on request
 * - Session Pattern: User session management
 *
 * @author Pahana Edu Development Team
 * @version 1.0
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login", "/auth/login"})
public class LoginServlet extends BaseServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.userService = new UserServiceImpl();
    }

    /**
     * Handle GET requests - Display login page
     */
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
            showLoginPage(request, response);
            return;
        }

        try {
            // Attempt authentication
            User user = userService.authenticateUser(username, password);

            if (user != null) {
                // Authentication successful
                handleSuccessfulLogin(request, response, user, remember);
            } else {
                // Authentication failed
                handleFailedLogin(request, response, username);
            }

        } catch (Exception e) {
            System.err.println("Login error for user " + username + ": " + e.getMessage());
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

        // Handle "Remember Me" functionality
        if (rememberMe) {
            // Set longer session timeout (7 days)
            request.getSession().setMaxInactiveInterval(7 * 24 * 60 * 60);
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

            // In a real application, you would:
            // 1. Generate a secure reset token
            // 2. Store it in database with expiration
            // 3. Send email with reset link
            // For this demo, we just show a message

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
     * Check for brute force protection (basic implementation)
     */
    private boolean isAccountLocked(String username) {
        // In a real application, you would check:
        // 1. Number of failed attempts
        // 2. Time window for attempts
        // 3. Account lockout status
        // This is a placeholder for the concept
        return false;
    }

    /**
     * Rate limiting for login attempts
     */
    private boolean isRateLimited(HttpServletRequest request) {
        // In a real application, you would implement:
        // 1. IP-based rate limiting
        // 2. Session-based attempt tracking
        // 3. Progressive delays
        // This is a placeholder for the concept
        return false;
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

    @Override
    public void destroy() {
        super.destroy();
        this.userService = null;
    }
}