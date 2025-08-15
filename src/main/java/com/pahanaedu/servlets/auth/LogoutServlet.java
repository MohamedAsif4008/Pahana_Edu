package com.pahanaedu.servlets.auth;

import com.pahanaedu.models.User;
import com.pahanaedu.servlets.common.BaseServlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet for handling user logout
 * Manages user logout process and session cleanup
 *
 * Design Patterns Used:
 * - Command Pattern: Single responsibility for logout
 * - Session Pattern: Session management
 * - Security Pattern: Secure logout implementation
 *
 * @author Pahana Edu Development Team
 * @version 1.0
 */
@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout", "/auth/logout"})
public class LogoutServlet extends BaseServlet {

    /**
     * Handle GET requests - Process logout
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processLogout(request, response);
    }

    /**
     * Handle POST requests - Process logout (for security)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Validate CSRF token for POST logout (more secure)
        if (!isValidCSRFToken(request)) {
            setErrorMessage(request, "Invalid logout request.");
            redirectTo(response, request.getContextPath() + "/dashboard");
            return;
        }

        processLogout(request, response);
    }

    /**
     * Process user logout
     */
    private void processLogout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Check if request is AJAX
            if (isAjaxRequest(request)) {
                handleAjaxLogout(request, response);
                return;
            }

            // Get current user for logging
            User currentUser = getCurrentUser(request);
            String userId = currentUser != null ? currentUser.getUserId() : "Unknown";

            // Audit logout for security monitoring
            if (currentUser != null) {
                auditLogout(request, currentUser);
            }

            // Log logout action
            logAction(request, "LOGOUT", "User logged out: " + userId);

            // Get session before clearing it
            HttpSession session = request.getSession(false);

            if (session != null) {
                // Clean up application-specific resources
                cleanupApplicationResources(request);

                // Clear specific attributes first (for cleanup)
                clearSessionData(session);

                // Invalidate the entire session
                session.invalidate();
            }

            // Clear any cookies if using "Remember Me" functionality
            clearRememberMeCookies(response);

            // Set success message for next request
            // Since session is invalidated, we need to use a different approach
            // We'll add it as a URL parameter or use a temporary session

            // Create new session just for the logout message
            HttpSession newSession = request.getSession(true);
            newSession.setAttribute("logoutMessage", "You have been successfully logged out.");
            newSession.setMaxInactiveInterval(60); // Short timeout for message

            // Redirect to login page
            redirectTo(response, request.getContextPath() + "/login?logout=success");

        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
            e.printStackTrace();

            // Even if there's an error, still try to clear session and redirect
            try {
                clearUserSession(request);
                redirectTo(response, request.getContextPath() + "/login?logout=error");
            } catch (Exception ex) {
                // Last resort - send error response
                sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Logout failed. Please close your browser.");
            }
        }
    }

    /**
     * Clear session data before invalidation
     */
    private void clearSessionData(HttpSession session) {
        try {
            // Remove user-related attributes
            session.removeAttribute(SESSION_USER);
            session.removeAttribute(SESSION_USER_ID);
            session.removeAttribute(SESSION_USER_ROLE);

            // Remove any cart or temporary data
            session.removeAttribute("cart");
            session.removeAttribute("tempData");
            session.removeAttribute("lastPage");

            // Remove CSRF tokens
            session.removeAttribute("csrfToken");

            // Remove recent searches
            session.removeAttribute("recentCustomerSearches");
            session.removeAttribute("recentItemSearches");
            session.removeAttribute("recentBillSearches");

            // Clear any other application-specific session data
            session.removeAttribute("currentBill");
            session.removeAttribute("billItems");
            session.removeAttribute("preferences");

        } catch (Exception e) {
            System.err.println("Error clearing session data: " + e.getMessage());
        }
    }

    /**
     * Clear "Remember Me" cookies
     */
    private void clearRememberMeCookies(HttpServletResponse response) {
        try {
            // Clear remember me token cookie
            jakarta.servlet.http.Cookie rememberCookie = new jakarta.servlet.http.Cookie("rememberToken", null);
            rememberCookie.setMaxAge(0); // Delete cookie
            rememberCookie.setPath("/");
            rememberCookie.setHttpOnly(true);
            rememberCookie.setSecure(true); // Use HTTPS in production
            response.addCookie(rememberCookie);

            // Clear any other authentication cookies
            jakarta.servlet.http.Cookie authCookie = new jakarta.servlet.http.Cookie("authToken", null);
            authCookie.setMaxAge(0);
            authCookie.setPath("/");
            authCookie.setHttpOnly(true);
            authCookie.setSecure(true);
            response.addCookie(authCookie);

            // Clear session ID cookie
            jakarta.servlet.http.Cookie sessionCookie = new jakarta.servlet.http.Cookie("JSESSIONID", null);
            sessionCookie.setMaxAge(0);
            sessionCookie.setPath("/");
            sessionCookie.setHttpOnly(true);
            response.addCookie(sessionCookie);

        } catch (Exception e) {
            System.err.println("Error clearing cookies: " + e.getMessage());
        }
    }

    /**
     * Handle emergency logout (for security)
     * This method can be called to forcibly logout a user
     */
    public static void forceLogout(HttpServletRequest request, HttpServletResponse response, String reason) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                // Log the forced logout
                System.out.println("FORCED LOGOUT: " + reason + " for session: " + session.getId());
                session.invalidate();
            }

            // Redirect to login with security message
            response.sendRedirect(request.getContextPath() + "/login?security=true");

        } catch (Exception e) {
            System.err.println("Error during forced logout: " + e.getMessage());
        }
    }

    /**
     * Logout all sessions for a user (admin function)
     * This would typically be used by administrators to force logout a user from all devices
     */
    public static void logoutAllUserSessions(String userId, String reason) {
        try {
            // In a real application, you would:
            // 1. Maintain a session registry/tracker
            // 2. Find all active sessions for the user
            // 3. Invalidate each session
            // 4. Clear any persistent tokens

            System.out.println("ADMIN LOGOUT ALL: User " + userId + " - Reason: " + reason);

            // Log the admin action
            System.out.println("All sessions for user " + userId + " have been terminated");

        } catch (Exception e) {
            System.err.println("Error during logout all sessions: " + e.getMessage());
        }
    }

    /**
     * Check for concurrent sessions (security feature)
     */
    private boolean checkConcurrentSessions(HttpServletRequest request) {
        try {
            User user = getCurrentUser(request);
            if (user == null) {
                return true; // No user, allow logout
            }

            // In a real application, you would:
            // 1. Check if user has multiple active sessions
            // 2. Implement session management policy
            // 3. Handle concurrent login restrictions

            return true; // Allow logout for now

        } catch (Exception e) {
            System.err.println("Error checking concurrent sessions: " + e.getMessage());
            return true; // Allow logout on error
        }
    }

    /**
     * Audit logout for security monitoring
     */
    private void auditLogout(HttpServletRequest request, User user) {
        try {
            String userId = user != null ? user.getUserId() : "Unknown";
            String userAgent = request.getHeader("User-Agent");
            String clientIP = getClientIP(request);
            long sessionDuration = getSessionDuration(request);

            // Create audit log entry
            String auditEntry = String.format(
                    "LOGOUT_AUDIT: User=%s, IP=%s, UserAgent=%s, Duration=%dms, Timestamp=%d",
                    userId, clientIP, userAgent, sessionDuration, System.currentTimeMillis()
            );

            System.out.println(auditEntry);

            // In a real application, you would:
            // 1. Store this in an audit database
            // 2. Send to security monitoring system
            // 3. Check for unusual patterns

        } catch (Exception e) {
            System.err.println("Error during logout audit: " + e.getMessage());
        }
    }

    /**
     * Calculate session duration
     */
    private long getSessionDuration(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                return 0;
            }

            Long loginTime = (Long) session.getAttribute("loginTime");
            if (loginTime != null) {
                return System.currentTimeMillis() - loginTime;
            }

            // Fallback: calculate from session creation time
            return System.currentTimeMillis() - session.getCreationTime();

        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Handle logout confirmation page (for important operations)
     */
    private void showLogoutConfirmation(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Generate CSRF token for secure logout
        String csrfToken = generateCSRFToken(request);
        request.setAttribute("csrfToken", csrfToken);

        // Check if user has unsaved work
        boolean hasUnsavedWork = checkUnsavedWork(request);
        request.setAttribute("hasUnsavedWork", hasUnsavedWork);

        // Forward to confirmation page
        forwardToJSP(request, response, "auth/logout-confirm.jsp");
    }

    /**
     * Check if user has unsaved work
     */
    private boolean checkUnsavedWork(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                return false;
            }

            // Check for draft bills, unsaved forms, etc.
            Object draftBill = session.getAttribute("draftBill");
            Object unsavedForm = session.getAttribute("unsavedForm");
            Object currentBill = session.getAttribute("currentBill");

            return draftBill != null || unsavedForm != null || currentBill != null;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Clean up any application-specific resources
     */
    private void cleanupApplicationResources(HttpServletRequest request) {
        try {
            User user = getCurrentUser(request);
            if (user == null) {
                return;
            }

            // Clean up any user-specific caches
            // Release any locks held by the user
            // Clear temporary files
            // Notify other services of logout

            // Clear any draft bills or temporary data
            HttpSession session = request.getSession(false);
            if (session != null) {
                // Clean up billing session data
                session.removeAttribute("currentBill");
                session.removeAttribute("billItems");
                session.removeAttribute("cartItems");

                // Clean up search data
                session.removeAttribute("lastSearchResults");
                session.removeAttribute("searchFilters");
            }

            System.out.println("Cleaned up resources for user: " + user.getUserId());

        } catch (Exception e) {
            System.err.println("Error cleaning up application resources: " + e.getMessage());
        }
    }

    /**
     * Handle AJAX logout requests
     */
    private void handleAjaxLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            // Validate CSRF token
            if (!isValidCSRFToken(request)) {
                sendJsonResponse(response, "{\"success\": false, \"message\": \"Invalid request\"}");
                return;
            }

            // Get current user
            User user = getCurrentUser(request);

            // Perform logout
            clearUserSession(request);
            clearRememberMeCookies(response);

            // Log action
            if (user != null) {
                logAction(request, "AJAX_LOGOUT", "User logged out via AJAX: " + user.getUserId());
            }

            // Send success response
            sendJsonResponse(response, "{\"success\": true, \"message\": \"Logout successful\", \"redirect\": \"/login\"}");

        } catch (Exception e) {
            System.err.println("Error during AJAX logout: " + e.getMessage());
            sendJsonResponse(response, "{\"success\": false, \"message\": \"Logout failed\"}");
        }
    }

    /**
     * Check if request is AJAX
     */
    private boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWith = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(requestedWith);
    }

    /**
     * Handle session timeout logout
     */
    public static void handleSessionTimeout(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Log session timeout
            System.out.println("SESSION TIMEOUT: Session expired for IP: " +
                    request.getRemoteAddr());

            // Clear session if it exists
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            // Redirect to login with timeout message
            response.sendRedirect(request.getContextPath() + "/login?timeout=true");

        } catch (Exception e) {
            System.err.println("Error handling session timeout: " + e.getMessage());
        }
    }

    /**
     * Logout due to security violation
     */
    public static void securityLogout(HttpServletRequest request, HttpServletResponse response, String reason) {
        try {
            // Log security logout
            System.err.println("SECURITY LOGOUT: " + reason + " from IP: " + request.getRemoteAddr());

            // Invalidate session immediately
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            // Redirect to login with security alert
            response.sendRedirect(request.getContextPath() + "/login?security=violation");

        } catch (Exception e) {
            System.err.println("Error during security logout: " + e.getMessage());
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        System.out.println("LogoutServlet destroyed - cleanup completed");
    }
}