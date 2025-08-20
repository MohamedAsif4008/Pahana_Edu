package com.pahanaedu.servlets.user;

import com.pahanaedu.models.User;
import com.pahanaedu.service.interfaces.UserService;
import com.pahanaedu.service.impl.UserServiceImpl;
import com.pahanaedu.servlets.common.BaseServlet;
import com.pahanaedu.util.ValidationUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Servlet for User Management (Admin only)
 * Handles CRUD operations for staff users
 */
@WebServlet(name = "UserManagementServlet", urlPatterns = {"/users", "/user-management"})
public class UserManagementServlet extends BaseServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.userService = new UserServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check authentication
        if (!isUserLoggedIn(request)) {
            redirectTo(response, request.getContextPath() + "/login");
            return;
        }

        // Check if user is admin
        User currentUser = getCurrentUser(request);
        if (currentUser == null || !currentUser.isAdmin()) {
            setErrorMessage(request, "Access denied. Admin privileges required.");
            redirectTo(response, request.getContextPath() + "/dashboard");
            return;
        }

        String action = getParameter(request, PARAM_ACTION, "list");

        try {
            switch (action.toLowerCase()) {
                case "list":
                    showUserList(request, response);
                    break;
                case "create":
                    showCreateUserForm(request, response);
                    break;
                case "edit":
                    showEditUserForm(request, response);
                    break;
                case "view":
                    showUserDetails(request, response);
                    break;
                default:
                    showUserList(request, response);
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error in UserManagementServlet doGet: " + e.getMessage());
            e.printStackTrace();
            handleException(request, response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check authentication
        if (!isUserLoggedIn(request)) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }

        // Check if user is admin
        User currentUser = getCurrentUser(request);
        if (currentUser == null || !currentUser.isAdmin()) {
            setErrorMessage(request, "Access denied. Admin privileges required.");
            redirectTo(response, request.getContextPath() + "/dashboard");
            return;
        }

        String action = getParameter(request, PARAM_ACTION, "create");

        try {
            switch (action.toLowerCase()) {
                case "create":
                    createUser(request, response);
                    break;
                case "update":
                    updateUser(request, response);
                    break;
                case "delete":
                    deleteUser(request, response);
                    break;
                default:
                    setErrorMessage(request, "Invalid action specified");
                    showUserList(request, response);
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error in UserManagementServlet doPost: " + e.getMessage());
            e.printStackTrace();
            handleException(request, response, e);
        }
    }

    /**
     * Show user list
     */
    private void showUserList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Use getAllActiveUsers() instead of getAllUsers()
            List<User> users = userService.getAllActiveUsers();
            request.setAttribute("users", users);
            forwardToJSP(request, response, "user/list.jsp");
        } catch (Exception e) {
            System.err.println("Error showing user list: " + e.getMessage());
            setErrorMessage(request, "Error loading user list");
            forwardToJSP(request, response, "user/list.jsp");
        }
    }

    /**
     * Show create user form
     */
    private void showCreateUserForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        forwardToJSP(request, response, "user/create.jsp");
    }

    /**
     * Show edit user form
     */
    private void showEditUserForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userId = getSanitizedParameter(request, PARAM_ID);
        if (!ValidationUtils.isNotEmpty(userId)) {
            setErrorMessage(request, "User ID is required");
            showUserList(request, response);
            return;
        }

        try {
            User user = userService.findUserById(userId);
            if (user == null) {
                setErrorMessage(request, "User not found");
                showUserList(request, response);
                return;
            }

            request.setAttribute("user", user);
            forwardToJSP(request, response, "user/edit.jsp");
        } catch (Exception e) {
            System.err.println("Error showing edit form: " + e.getMessage());
            setErrorMessage(request, "Error loading user for editing");
            showUserList(request, response);
        }
    }

    /**
     * Show user details
     */
    private void showUserDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userId = getSanitizedParameter(request, PARAM_ID);
        if (!ValidationUtils.isNotEmpty(userId)) {
            setErrorMessage(request, "User ID is required");
            showUserList(request, response);
            return;
        }

        try {
            User user = userService.findUserById(userId);
            if (user == null) {
                setErrorMessage(request, "User not found");
                showUserList(request, response);
                return;
            }

            request.setAttribute("user", user);
            forwardToJSP(request, response, "user/view.jsp");
        } catch (Exception e) {
            System.err.println("Error showing user details: " + e.getMessage());
            setErrorMessage(request, "Error loading user details");
            showUserList(request, response);
        }
    }

    /**
     * Create new user
     */
    private void createUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String username = getSanitizedParameter(request, "username");
            String password = getSanitizedParameter(request, "password");
            String fullName = getSanitizedParameter(request, "fullName");
            String email = getSanitizedParameter(request, "email");
            String roleStr = getSanitizedParameter(request, "role");

            // Basic validation
            if (!ValidationUtils.isNotEmpty(username) || !ValidationUtils.isNotEmpty(password) || 
                !ValidationUtils.isNotEmpty(fullName)) {
                setErrorMessage(request, "Username, password, and full name are required");
                showCreateUserForm(request, response);
                return;
            }

            // Check if username already exists
            if (userService.findUserByUsername(username) != null) {
                setErrorMessage(request, "Username already exists. Please choose a different username.");
                showCreateUserForm(request, response);
                return;
            }

            // Create user object - we need to create a concrete implementation
            // Since User is abstract, we'll need to create a concrete user class
            // For now, let's create a simple user implementation
            User user = createConcreteUser();
            user.setUsername(username);
            user.setPassword(password); // Service will hash this
            user.setFullName(fullName);
            user.setEmail(email);
            
            // Set role using enum
            if ("ADMIN".equals(roleStr)) {
                user.setRole(User.Role.ADMIN);
            } else {
                user.setRole(User.Role.STAFF); // Default to STAFF
            }

            boolean success = userService.createUser(user);

            if (success) {
                setSuccessMessage(request, "User created successfully!");
                logAction(request, "CREATE_USER", "Username: " + username);
                response.sendRedirect(request.getContextPath() + "/users");
            } else {
                setErrorMessage(request, "Failed to create user. Please try again.");
                showCreateUserForm(request, response);
            }

        } catch (Exception e) {
            System.err.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
            setErrorMessage(request, "Error creating user: " + e.getMessage());
            showCreateUserForm(request, response);
        }
    }

    /**
     * Update existing user
     */
    private void updateUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String userId = getSanitizedParameter(request, PARAM_ID);
            String fullName = getSanitizedParameter(request, "fullName");
            String email = getSanitizedParameter(request, "email");
            String roleStr = getSanitizedParameter(request, "role");
            String isActiveStr = getParameter(request, "isActive", "true");

            if (!ValidationUtils.isNotEmpty(userId)) {
                setErrorMessage(request, "User ID is required");
                showUserList(request, response);
                return;
            }

            User existingUser = userService.findUserById(userId);
            if (existingUser == null) {
                setErrorMessage(request, "User not found");
                showUserList(request, response);
                return;
            }

            // Update fields
            existingUser.setFullName(fullName);
            existingUser.setEmail(email);
            
            // Set role using enum
            if ("ADMIN".equals(roleStr)) {
                existingUser.setRole(User.Role.ADMIN);
            } else {
                existingUser.setRole(User.Role.STAFF);
            }
            
            existingUser.setActive("true".equals(isActiveStr));

            boolean success = userService.updateUser(existingUser);

            if (success) {
                setSuccessMessage(request, "User updated successfully!");
                logAction(request, "UPDATE_USER", "User ID: " + userId);
                response.sendRedirect(request.getContextPath() + "/users");
            } else {
                setErrorMessage(request, "Failed to update user");
                request.setAttribute("user", existingUser);
                forwardToJSP(request, response, "user/edit.jsp");
            }

        } catch (Exception e) {
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
            setErrorMessage(request, "Error updating user: " + e.getMessage());
            showUserList(request, response);
        }
    }

    /**
     * Delete user (deactivate)
     */
    private void deleteUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String userId = getSanitizedParameter(request, PARAM_ID);

            if (!ValidationUtils.isNotEmpty(userId)) {
                setErrorMessage(request, "User ID is required");
                showUserList(request, response);
                return;
            }

            // Prevent admin from deleting themselves
            User currentUser = getCurrentUser(request);
            if (currentUser.getUserId().equals(userId)) {
                setErrorMessage(request, "You cannot delete your own account");
                showUserList(request, response);
                return;
            }

            User userToDelete = userService.findUserById(userId);
            if (userToDelete == null) {
                setErrorMessage(request, "User not found");
                showUserList(request, response);
                return;
            }

            boolean success = userService.deactivateUser(userId);

            if (success) {
                setSuccessMessage(request, "User deactivated successfully!");
                logAction(request, "DELETE_USER", "User ID: " + userId);
                response.sendRedirect(request.getContextPath() + "/users");
            } else {
                setErrorMessage(request, "Failed to deactivate user");
                showUserList(request, response);
            }

        } catch (Exception e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
            setErrorMessage(request, "Error deleting user: " + e.getMessage());
            showUserList(request, response);
        }
    }

    /**
     * Create a concrete User implementation
     * Since User is abstract, we need to create a concrete instance
     */
    private User createConcreteUser() {
        // Create an anonymous implementation of the abstract User class
        return new User() {
            @Override
            public String getDisplayRole() {
                return getRole() != null ? getRole().toString() : "Unknown";
            }

            @Override
            public boolean hasPermission(String permission) {
                if (getRole() == null) return false;
                
                switch (permission) {
                    case "USER_MANAGEMENT":
                        return getRole() == User.Role.ADMIN;
                    case "CUSTOMER_MANAGEMENT":
                    case "ITEM_MANAGEMENT":
                    case "BILL_MANAGEMENT":
                        return getRole() == User.Role.ADMIN || getRole() == User.Role.STAFF;
                    default:
                        return false;
                }
            }
        };
    }

    @Override
    public void destroy() {
        super.destroy();
        this.userService = null;
    }
}