package com.pahanaedu.servlets.auth;

import com.pahanaedu.models.User;
import com.pahanaedu.util.ValidationUtils;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Authentication filter to protect secured resources
 * Ensures only authenticated users can access protected pages
 *
 * Design Patterns Used:
 * - Filter Pattern: Intercepts requests for security
 * - Chain of Responsibility: Filter chain processing
 * - Strategy Pattern: Different security strategies
 *
 * @author Pahana Edu Development Team
 * @version 1.0
 */
@WebFilter(filterName = "AuthenticationFilter",
        urlPatterns = {"/*"},
        dispatcherTypes = {DispatcherType.REQUEST, DispatcherType.FORWARD})
public class AuthenticationFilter implements Filter {

    // Public URLs that don't require authentication
    private static final Set<String> PUBLIC_URLS = new HashSet<>(Arrays.asList(
            "/login",
            "/auth/login",
            "/logout",
            "/auth/logout",
            "/",
            "/index.jsp",
            "/css/",
            "/js/",
            "/images/",
            "/favicon.ico",
            "/error"
    ));

    // Admin-only URLs
    private static final Set<String> ADMIN_URLS = new HashSet<>(Arrays.asList(
            "/admin/",
            "/users",
            "/reports/admin",
            "/settings"
    ));

    // API URLs that require special handling
    private static final Set<String> API_URLS = new HashSet<>(Arrays.asList(
            "/api/"
    ));

    private FilterConfig filterConfig;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        System.out.println("AuthenticationFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            // Get request URI
            String requestURI = httpRequest.getRequestURI();
            String contextPath = httpRequest.getContextPath();
            String path = requestURI.substring(contextPath.length());

            // Log request for monitoring
            logRequest(httpRequest, path);

            // Check if this is a public URL
            if (isPublicUrl(path)) {
                chain.doFilter(request, response);
                return;
            }

            // Check authentication
            User currentUser = getCurrentUser(httpRequest);
            if (currentUser == null) {
                handleUnauthenticatedRequest(httpRequest, httpResponse, path);
                return;
            }

            // Validate session security
            if (!isValidSession(httpRequest)) {
                handleInvalidSession(httpRequest, httpResponse);
                return;
            }

            // Check authorization for protected resources
            if (!isAuthorized(currentUser, path)) {
                handleUnauthorizedRequest(httpRequest, httpResponse, path);
                return;
            }

            // Check for admin-only resources
            if (isAdminUrl(path) && !currentUser.isAdmin()) {
                handleAdminOnlyRequest(httpRequest, httpResponse, path);
                return;
            }

            // Update session activity
            updateSessionActivity(httpRequest);

            // Set security headers
            setSecurityHeaders(httpResponse);

            // Continue with the request
            chain.doFilter(request, response);

        } catch (Exception e) {
            System.err.println("Authentication filter error: " + e.getMessage());
            e.printStackTrace();
            handleFilterException(httpRequest, httpResponse, e);
        }
    }

    /**
     * Check if URL is public (doesn't require authentication)
     */
    private boolean isPublicUrl(String path) {
        // Direct match
        if (PUBLIC_URLS.contains(path)) {
            return true;
        }

        // Check for prefix matches (for static resources)
        for (String publicUrl : PUBLIC_URLS) {
            if (publicUrl.endsWith("/") && path.startsWith(publicUrl)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if URL requires admin privileges
     */
    private boolean isAdminUrl(String path) {
        for (String adminUrl : ADMIN_URLS) {
            if (path.equals(adminUrl) || path.startsWith(adminUrl)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if URL is an API endpoint
     */
    private boolean isApiUrl(String path) {
        for (String apiUrl : API_URLS) {
            if (path.startsWith(apiUrl)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get current user from session
     */
    private User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (User) session.getAttribute("currentUser");
        }
        return null;
    }

    /**
     * Validate session security
     */
    private boolean isValidSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        try {
            // Check session timeout
            long now = System.currentTimeMillis();
            Long lastAccess = (Long) session.getAttribute("lastAccess");

            if (lastAccess != null) {
                long inactiveTime = now - lastAccess;
                long maxInactive = session.getMaxInactiveInterval() * 1000L;

                if (inactiveTime > maxInactive) {
                    session.invalidate();
                    return false;
                }
            }

            // Check for session hijacking (basic)
            String storedIP = (String) session.getAttribute("clientIP");
            String currentIP = getClientIP(request);

            if (storedIP != null && !storedIP.equals(currentIP)) {
                // IP changed - possible session hijacking
                System.err.println("SECURITY ALERT: IP change detected for session " + session.getId());
                session.invalidate();
                return false;
            }

            // Store/update client IP
            if (storedIP == null) {
                session.setAttribute("clientIP", currentIP);
            }

            return true;

        } catch (Exception e) {
            System.err.println("Session validation error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check user authorization for specific resource
     */
    private boolean isAuthorized(User user, String path) {
        if (user == null) {
            return false;
        }

        // Check if user is active
        if (!user.isActive()) {
            return false;
        }

        // Basic role-based authorization
        if (user.isAdmin()) {
            return true; // Admins can access everything
        }

        // Staff permissions
        if (user.isStaff()) {
            // Staff can access most resources except admin-only
            return !isAdminUrl(path);
        }

        return false;
    }

    /**
     * Handle unauthenticated requests
     */
    private void handleUnauthenticatedRequest(HttpServletRequest request, HttpServletResponse response, String path)
            throws IOException, ServletException {

        // Log unauthorized access attempt
        System.out.println("SECURITY: Unauthenticated access attempt to: " + path + " from IP: " + getClientIP(request));

        if (isApiUrl(path)) {
            // Return JSON error for API requests
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Authentication required\", \"code\": 401}");
        } else if (isAjaxRequest(request)) {
            // Return JSON error for AJAX requests
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Session expired\", \"redirect\": \"/login\"}");
        } else {
            // Redirect to login page with return URL
            String loginUrl = request.getContextPath() + "/login";
            if (!"/".equals(path) && !path.isEmpty()) {
                loginUrl += "?redirect=" + java.net.URLEncoder.encode(path, "UTF-8");
            }
            response.sendRedirect(loginUrl);
        }
    }

    /**
     * Handle invalid session
     */
    private void handleInvalidSession(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        System.out.println("SECURITY: Invalid session detected from IP: " + getClientIP(request));

        if (isApiUrl(request.getRequestURI()) || isAjaxRequest(request)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid session\", \"code\": 401}");
        } else {
            response.sendRedirect(request.getContextPath() + "/login?session=invalid");
        }
    }

    /**
     * Handle unauthorized requests (user logged in but lacks permission)
     */
    private void handleUnauthorizedRequest(HttpServletRequest request, HttpServletResponse response, String path)
            throws IOException {

        User user = getCurrentUser(request);
        String userId = user != null ? user.getUserId() : "Unknown";

        System.out.println("SECURITY: Unauthorized access attempt by user " + userId + " to: " + path);

        if (isApiUrl(path) || isAjaxRequest(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Access denied\", \"code\": 403}");
        } else {
            response.sendRedirect(request.getContextPath() + "/dashboard?error=access_denied");
        }
    }

    /**
     * Handle admin-only requests from non-admin users
     */
    private void handleAdminOnlyRequest(HttpServletRequest request, HttpServletResponse response, String path)
            throws IOException {

        User user = getCurrentUser(request);
        System.out.println("SECURITY: Non-admin user " + user.getUserId() + " attempted to access admin resource: " + path);

        if (isApiUrl(path) || isAjaxRequest(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Admin access required\", \"code\": 403}");
        } else {
            response.sendRedirect(request.getContextPath() + "/dashboard?error=admin_required");
        }
    }

    /**
     * Update session activity timestamp
     */
    private void updateSessionActivity(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setAttribute("lastAccess", System.currentTimeMillis());

            // Update last activity for monitoring
            session.setAttribute("lastActivity", new java.util.Date());
        }
    }

    /**
     * Set security headers
     */
    private void setSecurityHeaders(HttpServletResponse response) {
        // Prevent clickjacking
        response.setHeader("X-Frame-Options", "DENY");

        // Prevent MIME sniffing
        response.setHeader("X-Content-Type-Options", "nosniff");

        // XSS protection
        response.setHeader("X-XSS-Protection", "1; mode=block");

        // HTTPS redirect (in production)
        response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");

        // Cache control for sensitive pages
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
    }

    /**
     * Get client IP address
     */
    private String getClientIP(HttpServletRequest request) {
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
     * Check if request is AJAX
     */
    private boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWith = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(requestedWith);
    }

    /**
     * Log request for monitoring
     */
    private void logRequest(HttpServletRequest request, String path) {
        // Only log non-static resources
        if (!path.startsWith("/css/") && !path.startsWith("/js/") && !path.startsWith("/images/")) {
            String method = request.getMethod();
            String ip = getClientIP(request);
            String userAgent = request.getHeader("User-Agent");

            // Basic request logging
            System.out.printf("REQUEST: %s %s from %s [%s]%n", method, path, ip,
                    userAgent != null ? userAgent.substring(0, Math.min(50, userAgent.length())) : "Unknown");
        }
    }

    /**
     * Handle filter exceptions
     */
    private void handleFilterException(HttpServletRequest request, HttpServletResponse response, Exception e)
            throws IOException {

        System.err.println("Filter exception: " + e.getMessage());

        if (isApiUrl(request.getRequestURI()) || isAjaxRequest(request)) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Internal server error\", \"code\": 500}");
        } else {
            response.sendRedirect(request.getContextPath() + "/error?code=500");
        }
    }

    @Override
    public void destroy() {
        System.out.println("AuthenticationFilter destroyed");
        this.filterConfig = null;
    }
}