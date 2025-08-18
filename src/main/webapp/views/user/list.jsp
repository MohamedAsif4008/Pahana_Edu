<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="User Management" />
<jsp:include page="../common/header.jsp" />

<div class="container-fluid">
    <!-- Page Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="h3">
            <i class="bi bi-people-fill"></i> User Management
        </h1>
        <a href="${pageContext.request.contextPath}/users?action=create" class="btn btn-primary">
            <i class="bi bi-person-plus"></i> Add New User
        </a>
    </div>

    <!-- Messages -->
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="bi bi-check-circle"></i> ${successMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="bi bi-exclamation-triangle"></i> ${errorMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <!-- User List -->
    <div class="card">
        <div class="card-header">
            <h5 class="card-title mb-0">
                <i class="bi bi-list"></i> All Users
            </h5>
        </div>
        <div class="card-body">
            <c:choose>
                <c:when test="${not empty users}">
                    <div class="table-responsive">
                        <table class="table table-striped table-hover">
                            <thead class="table-dark">
                                <tr>
                                    <th>Username</th>
                                    <th>Full Name</th>
                                    <th>Email</th>
                                    <th>Role</th>
                                    <th>Status</th>
                                    <th>Created Date</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${users}" var="user">
                                    <tr>
                                        <td>
                                            <strong>${user.username}</strong>
                                        </td>
                                        <td>${user.fullName}</td>
                                        <td>
                                            <c:if test="${not empty user.email}">
                                                <i class="bi bi-envelope"></i> ${user.email}
                                            </c:if>
                                            <c:if test="${empty user.email}">
                                                <span class="text-muted">No email</span>
                                            </c:if>
                                        </td>
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
                                        <td>
                                            <c:if test="${not empty user.createdDate}">
                                                <fmt:formatDate value="${user.createdDate}" pattern="dd/MM/yyyy HH:mm"/>
                                            </c:if>
                                            <c:if test="${empty user.createdDate}">
                                                <span class="text-muted">N/A</span>
                                            </c:if>
                                        </td>
                                        <td>
                                            <div class="btn-group btn-group-sm">
                                                <a href="${pageContext.request.contextPath}/users?action=view&id=${user.userId}"
                                                   class="btn btn-outline-info" title="View Details">
                                                    <i class="bi bi-eye"></i>
                                                </a>
                                                <a href="${pageContext.request.contextPath}/users?action=edit&id=${user.userId}"
                                                   class="btn btn-outline-warning" title="Edit User">
                                                    <i class="bi bi-pencil"></i>
                                                </a>
                                                <c:if test="${user.userId != sessionScope.currentUser.userId}">
                                                    <button type="button" class="btn btn-outline-danger"
                                                            onclick="confirmDelete('${user.userId}', '${user.username}')"
                                                            title="Delete User">
                                                        <i class="bi bi-trash"></i>
                                                    </button>
                                                </c:if>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="text-center py-5">
                        <i class="bi bi-people text-muted" style="font-size: 3rem;"></i>
                        <h5 class="text-muted mt-3">No Users Found</h5>
                        <p class="text-muted">There are no users in the system yet.</p>
                        <a href="${pageContext.request.contextPath}/users?action=create" class="btn btn-primary">
                            <i class="bi bi-person-plus"></i> Add First User
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <!-- Quick Stats -->
    <div class="row mt-4">
        <div class="col-md-3">
            <div class="card bg-primary text-white">
                <div class="card-body text-center">
                    <i class="bi bi-people" style="font-size: 2rem;"></i>
                    <h4 class="mt-2">${users.size()}</h4>
                    <p class="mb-0">Total Users</p>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card bg-success text-white">
                <div class="card-body text-center">
                    <i class="bi bi-check-circle" style="font-size: 2rem;"></i>
                    <h4 class="mt-2">
                        <c:set var="activeCount" value="0"/>
                        <c:forEach items="${users}" var="user">
                            <c:if test="${user.active}">
                                <c:set var="activeCount" value="${activeCount + 1}"/>
                            </c:if>
                        </c:forEach>
                        ${activeCount}
                    </h4>
                    <p class="mb-0">Active Users</p>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card bg-info text-white">
                <div class="card-body text-center">
                    <i class="bi bi-person-badge" style="font-size: 2rem;"></i>
                    <h4 class="mt-2">
                        <c:set var="staffCount" value="0"/>
                        <c:forEach items="${users}" var="user">
                            <c:if test="${user.role == 'STAFF'}">
                                <c:set var="staffCount" value="${staffCount + 1}"/>
                            </c:if>
                        </c:forEach>
                        ${staffCount}
                    </h4>
                    <p class="mb-0">Staff Members</p>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card bg-warning text-white">
                <div class="card-body text-center">
                    <i class="bi bi-shield-check" style="font-size: 2rem;"></i>
                    <h4 class="mt-2">
                        <c:set var="adminCount" value="0"/>
                        <c:forEach items="${users}" var="user">
                            <c:if test="${user.role == 'ADMIN'}">
                                <c:set var="adminCount" value="${adminCount + 1}"/>
                            </c:if>
                        </c:forEach>
                        ${adminCount}
                    </h4>
                    <p class="mb-0">Admins</p>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- JavaScript for delete confirmation -->
<script>
    function confirmDelete(userId, username) {
        if (confirm('Are you sure you want to delete user "' + username + '"?\n\nThis action will deactivate the user account.')) {
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