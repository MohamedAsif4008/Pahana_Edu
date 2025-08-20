<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="User Details" />
<jsp:include page="../common/header.jsp" />

<div class="container-fluid">
    <!-- Page Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="h3">
            <i class="bi bi-person-circle"></i> User Details
        </h1>
        <div>
            <a href="${pageContext.request.contextPath}/users?action=edit&id=${user.userId}" 
               class="btn btn-warning me-2">
                <i class="bi bi-pencil"></i> Edit User
            </a>
            <a href="${pageContext.request.contextPath}/users" class="btn btn-outline-secondary">
                <i class="bi bi-arrow-left"></i> Back to Users
            </a>
        </div>
    </div>

    <!-- Messages -->
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="bi bi-exclamation-triangle"></i> ${errorMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <!-- User Details -->
    <div class="row">
        <!-- Main User Information -->
        <div class="col-md-8">
            <div class="card shadow-sm">
                <div class="card-header bg-info text-white">
                    <h5 class="card-title mb-0">
                        <i class="bi bi-person-fill"></i> User Information
                    </h5>
                </div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-6">
                            <table class="table table-borderless">
                                <tr>
                                    <td><strong>Username:</strong></td>
                                    <td>
                                        <span class="badge bg-secondary">${user.username}</span>
                                    </td>
                                </tr>
                                <tr>
                                    <td><strong>Full Name:</strong></td>
                                    <td>${user.fullName}</td>
                                </tr>
                                <tr>
                                    <td><strong>Email:</strong></td>
                                    <td>
                                        <c:if test="${not empty user.email}">
                                            <i class="bi bi-envelope"></i> ${user.email}
                                        </c:if>
                                        <c:if test="${empty user.email}">
                                            <span class="text-muted">No email provided</span>
                                        </c:if>
                                    </td>
                                </tr>
                                <tr>
                                    <td><strong>Role:</strong></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${user.role == 'ADMIN'}">
                                                <span class="badge bg-danger">Admin</span>
                                            </c:when>
                                            <c:when test="${user.role == 'STAFF'}">
                                                <span class="badge bg-primary">Staff</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-secondary">${user.role}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </table>
                        </div>
                        <div class="col-md-6">
                            <table class="table table-borderless">
                                <tr>
                                    <td><strong>Status:</strong></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${user.active}">
                                                <span class="badge bg-success">Active</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-warning">Inactive</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                                <tr>
                                    <td><strong>Created Date:</strong></td>
                                    <td>
                                        <c:if test="${not empty user.createdDate}">
                                            <i class="bi bi-calendar"></i> 
                                            <fmt:formatDate value="${user.createdDate}" pattern="dd/MM/yyyy HH:mm"/>
                                        </c:if>
                                        <c:if test="${empty user.createdDate}">
                                            <span class="text-muted">Not available</span>
                                        </c:if>
                                    </td>
                                </tr>
                                <tr>
                                    <td><strong>Last Updated:</strong></td>
                                    <td>
                                        <c:if test="${not empty user.lastLogin}">
                                            <i class="bi bi-calendar-check"></i> 
                                            <fmt:formatDate value="${user.lastLogin}" pattern="dd/MM/yyyy HH:mm"/>
                                        </c:if>
                                        <c:if test="${empty user.lastLogin}">
                                            <span class="text-muted">Not available</span>
                                        </c:if>
                                    </td>
                                </tr>
                                <tr>
                                    <td><strong>User ID:</strong></td>
                                    <td>
                                        <code class="text-muted">${user.userId}</code>
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Role Permissions -->
            <div class="card mt-4">
                <div class="card-header">
                    <h6 class="card-title mb-0">
                        <i class="bi bi-shield-check"></i> Role Permissions
                    </h6>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${user.role == 'ADMIN'}">
                            <div class="row">
                                <div class="col-md-6">
                                    <h6 class="text-success">Admin Permissions:</h6>
                                    <ul class="list-unstyled">
                                        <li><i class="bi bi-check-circle text-success"></i> Manage all users</li>
                                        <li><i class="bi bi-check-circle text-success"></i> Create, edit, delete customers</li>
                                        <li><i class="bi bi-check-circle text-success"></i> Manage inventory items</li>
                                        <li><i class="bi bi-check-circle text-success"></i> Create and manage bills</li>
                                        <li><i class="bi bi-check-circle text-success"></i> Access all system reports</li>
                                        <li><i class="bi bi-check-circle text-success"></i> System configuration</li>
                                    </ul>
                                </div>
                                <div class="col-md-6">
                                    <div class="alert alert-info">
                                        <i class="bi bi-info-circle"></i>
                                        <strong>Admin users</strong> have full access to all system features and can manage other users.
                                    </div>
                                </div>
                            </div>
                        </c:when>
                        <c:when test="${user.role == 'STAFF'}">
                            <div class="row">
                                <div class="col-md-6">
                                    <h6 class="text-primary">Staff Permissions:</h6>
                                    <ul class="list-unstyled">
                                        <li><i class="bi bi-check-circle text-success"></i> Create, edit, delete customers</li>
                                        <li><i class="bi bi-check-circle text-success"></i> Manage inventory items</li>
                                        <li><i class="bi bi-check-circle text-success"></i> Create and manage bills</li>
                                        <li><i class="bi bi-check-circle text-success"></i> View reports</li>
                                        <li><i class="bi bi-x-circle text-muted"></i> Manage other users</li>
                                        <li><i class="bi bi-x-circle text-muted"></i> System configuration</li>
                                    </ul>
                                </div>
                                <div class="col-md-6">
                                    <div class="alert alert-info">
                                        <i class="bi bi-info-circle"></i>
                                        <strong>Staff users</strong> can perform all operational tasks but cannot manage other users.
                                    </div>
                                </div>
                            </div>
                        </c:when>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- Sidebar Actions -->
        <div class="col-md-4">
            <!-- Quick Actions -->
            <div class="card">
                <div class="card-header">
                    <h6 class="card-title mb-0">
                        <i class="bi bi-lightning"></i> Quick Actions
                    </h6>
                </div>
                <div class="card-body">
                    <div class="d-grid gap-2">
                        <a href="${pageContext.request.contextPath}/users?action=edit&id=${user.userId}" 
                           class="btn btn-warning">
                            <i class="bi bi-pencil"></i> Edit User
                        </a>
                        
                        <c:if test="${user.userId != sessionScope.currentUser.userId}">
                            <button type="button" 
                                    class="btn btn-outline-danger"
                                    onclick="confirmDelete('${user.userId}', '${user.username}')">
                                <i class="bi bi-trash"></i> Delete User
                            </button>
                        </c:if>
                        
                        <c:if test="${user.userId == sessionScope.currentUser.userId}">
                            <button type="button" 
                                    class="btn btn-outline-secondary" 
                                    disabled>
                                <i class="bi bi-shield-x"></i> Cannot Delete Self
                            </button>
                        </c:if>
                    </div>
                </div>
            </div>

            <!-- User Status -->
            <div class="card mt-3">
                <div class="card-header">
                    <h6 class="card-title mb-0">
                        <i class="bi bi-info-circle"></i> Account Status
                    </h6>
                </div>
                <div class="card-body">
                    <div class="text-center">
                        <c:choose>
                            <c:when test="${user.active}">
                                <i class="bi bi-check-circle text-success" style="font-size: 3rem;"></i>
                                <h5 class="text-success mt-2">Active Account</h5>
                                <p class="text-muted">This user can log in and use the system</p>
                            </c:when>
                            <c:otherwise>
                                <i class="bi bi-x-circle text-warning" style="font-size: 3rem;"></i>
                                <h5 class="text-warning mt-2">Inactive Account</h5>
                                <p class="text-muted">This user cannot log in to the system</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>

            <!-- System Information -->
            <div class="card mt-3">
                <div class="card-header">
                    <h6 class="card-title mb-0">
                        <i class="bi bi-gear"></i> System Info
                    </h6>
                </div>
                <div class="card-body">
                    <small class="text-muted">
                        <strong>User ID:</strong> ${user.userId}<br>
                        <strong>Created:</strong> 
                        <fmt:formatDate value="${user.createdDate}" pattern="dd/MM/yyyy"/><br>
                        <strong>Last Login:</strong> 
                        <c:if test="${not empty user.lastLogin}">
                            <fmt:formatDate value="${user.lastLogin}" pattern="dd/MM/yyyy"/>
                        </c:if>
                        <c:if test="${empty user.lastLogin}">
                            Never
                        </c:if>
                    </small>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- JavaScript for delete confirmation -->
<script>
    function confirmDelete(userId, username) {
        if (confirm('Are you sure you want to delete user "' + username + '"?\n\nThis action will deactivate the user account and cannot be undone.')) {
            var form = document.createElement('form');
            form.method = 'POST';
            form.action = '${pageContext.request.contextPath}/users';

            var actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            actionInput.value = 'delete';

            var idInput = document.createElement('input');
            idInput.type = 'hidden';
            idInput.name = 'id';
            idInput.value = userId;

            form.appendChild(actionInput);
            form.appendChild(idInput);
            document.body.appendChild(form);
            form.submit();
        }
    }
</script>

<jsp:include page="../common/footer.jsp" />