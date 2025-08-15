package com.pahanaedu.servlets.common;

import com.pahanaedu.models.User;
import com.pahanaedu.util.ValidationUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Base servlet class providing common functionality for all servlets
 * Implements Template Method pattern and common utilities
 *
 * Design Patterns Used:
 * - Template Method Pattern: Common servlet workflow
 * - Utility Pattern: Common helper methods
 * - Session Management Pattern: User session handling
 *
 * @author Pahana Edu Development Team
 * @version 1.0
 */
public abstract class BaseServlet extends HttpServlet {

    // Session attribute names
    protected static final String SESSION_USER = "currentUser";
    protected static final String SESSION_USER_ID = "userId";
    protected static final String SESSION_USER_ROLE = "userRole";

    // Request attribute names
    protected static final String ATTR_ERROR_MESSAGE = "errorMessage";
    protected static final String ATTR_SUCCESS_MESSAGE = "successMessage";
    protected static final String ATTR_INFO_MESSAGE = "infoMessage";

    // Common parameters
    protected static final String PARAM_ACTION = "action";
    protected static final String PARAM_ID = "id";
    protected static final String PARAM_PAGE = "page";
    protected static final String PARAM_SIZE = "size";

    // Default pagination values
    protected static final int DEFAULT_PAGE_SIZE = 20;
    protected static final int MAX_PAGE_SIZE = 100;

    /**
     * Get current logged-in user from session
     *
     * @param request HTTP request
     * @return Current user or null if not logged in
     */
    protected User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (User) session.getAttribute(SESSION_USER);
        }
        return null;
    }

    /**
     * Check if user is logged in
     *
     * @param request HTTP request
     * @return true if user is logged in, false otherwise
     */
    protected boolean isUserLoggedIn(HttpServletRequest request) {
        return getCurrentUser(request) != null;
    }

    /**
     * Check if current user has admin role
     *
     * @param request HTTP request
     * @return true if user is admin, false otherwise
     */
    protected boolean isAdmin(HttpServletRequest request) {
        User user = getCurrentUser(request);
        return user != null && user.isAdmin();
    }

    /**
     * Check if current user has staff role
     *
     * @param request HTTP request
     * @return true if user is staff, false otherwise
     */
    protected boolean isStaff(HttpServletRequest request) {
        User user = getCurrentUser(request);
        return user != null && user.isStaff();
    }

    /**
     * Set current user in session
     *
     * @param request HTTP request
     * @param user User to set in session
     */
    protected void setCurrentUser(HttpServletRequest request, User user) {
        HttpSession session = request.getSession(true);
        session.setAttribute(SESSION_USER, user);
        session.setAttribute(SESSION_USER_ID, user.getUserId());
        session.setAttribute(SESSION_USER_ROLE, user.getRole().toString());

        // Set session timeout (30 minutes)
        session.setMaxInactiveInterval(1800);
    }

    /**
     * Clear user session (logout)
     *
     * @param request HTTP request
     */
    protected void clearUserSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(SESSION_USER);
            session.removeAttribute(SESSION_USER_ID);
            session.removeAttribute(SESSION_USER_ROLE);
            session.invalidate();
        }
    }

    /**
     * Set error message in request
     *
     * @param request HTTP request
     * @param message Error message
     */
    protected void setErrorMessage(HttpServletRequest request, String message) {
        request.setAttribute(ATTR_ERROR_MESSAGE, message);
    }

    /**
     * Set success message in request
     *
     * @param request HTTP request
     * @param message Success message
     */
    protected void setSuccessMessage(HttpServletRequest request, String message) {
        request.setAttribute(ATTR_SUCCESS_MESSAGE, message);
    }

    /**
     * Set info message in request
     *
     * @param request HTTP request
     * @param message Info message
     */
    protected void setInfoMessage(HttpServletRequest request, String message) {
        request.setAttribute(ATTR_INFO_MESSAGE, message);
    }

    /**
     * Get request parameter with default value
     *
     * @param request HTTP request
     * @param paramName Parameter name
     * @param defaultValue Default value if parameter is null or empty
     * @return Parameter value or default value
     */
    protected String getParameter(HttpServletRequest request, String paramName, String defaultValue) {
        String value = request.getParameter(paramName);
        return ValidationUtils.isNotEmpty(value) ? value.trim() : defaultValue;
    }

    /**
     * Get integer parameter with default value
     *
     * @param request HTTP request
     * @param paramName Parameter name
     * @param defaultValue Default value if parameter is invalid
     * @return Parameter value as integer or default value
     */
    protected int getIntParameter(HttpServletRequest request, String paramName, int defaultValue) {
        try {
            String value = request.getParameter(paramName);
            return ValidationUtils.isNotEmpty(value) ? Integer.parseInt(value.trim()) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Get pagination parameters
     *
     * @param request HTTP request
     * @return int array with [page, size, offset]
     */
    protected int[] getPaginationParams(HttpServletRequest request) {
        int page = Math.max(1, getIntParameter(request, PARAM_PAGE, 1));
        int size = Math.min(MAX_PAGE_SIZE, Math.max(1, getIntParameter(request, PARAM_SIZE, DEFAULT_PAGE_SIZE)));
        int offset = (page - 1) * size;

        return new int[]{page, size, offset};
    }

    /**
     * Forward request to JSP page
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param jspPage JSP page path
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
     */
    protected void forwardToJSP(HttpServletRequest request, HttpServletResponse response, String jspPage)
            throws ServletException, IOException {
        request.getRequestDispatcher("/views/" + jspPage).forward(request, response);
    }

    /**
     * Redirect to URL
     *
     * @param response HTTP response
     * @param url URL to redirect to
     * @throws IOException if I/O error occurs
     */
    protected void redirectTo(HttpServletResponse response, String url) throws IOException {
        response.sendRedirect(url);
    }

    /**
     * Send JSON response
     *
     * @param response HTTP response
     * @param jsonContent JSON content
     * @throws IOException if I/O error occurs
     */
    protected void sendJsonResponse(HttpServletResponse response, String jsonContent) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            out.print(jsonContent);
            out.flush();
        }
    }

    /**
     * Send error response
     *
     * @param response HTTP response
     * @param statusCode HTTP status code
     * @param message Error message
     * @throws IOException if I/O error occurs
     */
    protected void sendErrorResponse(HttpServletResponse response, int statusCode, String message)
            throws IOException {
        response.setStatus(statusCode);
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            out.print(message);
            out.flush();
        }
    }

    /**
     * Validate required parameters
     *
     * @param request HTTP request
     * @param paramNames Required parameter names
     * @return true if all parameters are present and not empty
     */
    protected boolean validateRequiredParams(HttpServletRequest request, String... paramNames) {
        for (String paramName : paramNames) {
            String value = request.getParameter(paramName);
            if (!ValidationUtils.isNotEmpty(value)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sanitize input parameter
     *
     * @param request HTTP request
     * @param paramName Parameter name
     * @return Sanitized parameter value
     */
    protected String getSanitizedParameter(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName);
        return value != null ? ValidationUtils.sanitizeInput(value) : null;
    }

    /**
     * Log servlet action for auditing
     *
     * @param request HTTP request
     * @param action Action performed
     * @param details Action details
     */
    protected void logAction(HttpServletRequest request, String action, String details) {
        User user = getCurrentUser(request);
        String userId = user != null ? user.getUserId() : "Anonymous";
        String clientIP = getClientIP(request);

        System.out.println(String.format("[AUDIT] User: %s, IP: %s, Action: %s, Details: %s",
                userId, clientIP, action, details));
    }

    /**
     * Get client IP address
     *
     * @param request HTTP request
     * @return Client IP address
     */
    protected String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (ValidationUtils.isNotEmpty(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIP = request.getHeader("X-Real-IP");
        if (ValidationUtils.isNotEmpty(xRealIP)) {
            return xRealIP;
        }

        return request.getRemoteAddr();
    }

    /**
     * Check CSRF token (basic implementation)
     *
     * @param request HTTP request
     * @return true if CSRF token is valid
     */
    protected boolean isValidCSRFToken(HttpServletRequest request) {
        String sessionToken = (String) request.getSession().getAttribute("csrfToken");
        String requestToken = request.getParameter("csrfToken");

        return sessionToken != null && sessionToken.equals(requestToken);
    }

    /**
     * Generate CSRF token for session
     *
     * @param request HTTP request
     * @return Generated CSRF token
     */
    protected String generateCSRFToken(HttpServletRequest request) {
        String token = java.util.UUID.randomUUID().toString();
        request.getSession().setAttribute("csrfToken", token);
        return token;
    }

    /**
     * Handle common servlet exceptions
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param e Exception that occurred
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
     */
    protected void handleException(HttpServletRequest request, HttpServletResponse response, Exception e)
            throws ServletException, IOException {
        // Log the exception
        System.err.println("Servlet Exception: " + e.getMessage());
        e.printStackTrace();

        // Set error message
        setErrorMessage(request, "An error occurred while processing your request. Please try again.");

        // Forward to error page
        forwardToJSP(request, response, "common/error.jsp");
    }

    /**
     * Initialize servlet - can be overridden by subclasses
     */
    @Override
    public void init() throws ServletException {
        super.init();
        System.out.println(this.getClass().getSimpleName() + " initialized successfully");
    }

    /**
     * Destroy servlet - can be overridden by subclasses
     */
    @Override
    public void destroy() {
        System.out.println(this.getClass().getSimpleName() + " destroyed");
        super.destroy();
    }
}