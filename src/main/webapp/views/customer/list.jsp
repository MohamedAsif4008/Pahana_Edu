<%--
  Created by IntelliJ IDEA.
  User: MOHAMED
  Date: 16/08/2025
  Time: 12:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Customers" />
<jsp:include page="../common/header.jsp" />

<!-- Page Content -->
<div class="col-md-10 main-content p-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="h3">
            <i class="bi bi-people"></i> Manage Customers
        </h1>
        <div>
            <a href="${pageContext.request.contextPath}/customers?action=create" class="btn btn-primary">
                <i class="bi bi-person-plus"></i> Add New Customer
            </a>
        </div>
    </div>

    <!-- Search Bar -->
    <div class="row mb-4">
        <div class="col-md-6">
            <form action="${pageContext.request.contextPath}/customers" method="get" class="d-flex">
                <input type="hidden" name="action" value="search">
                <input type="text"
                       class="form-control me-2"
                       name="searchTerm"
                       value="${param.searchTerm}"
                       placeholder="Search by name, account number, or phone...">
                <button type="submit" class="btn btn-outline-primary">
                    <i class="bi bi-search"></i> Search
                </button>
            </form>
        </div>
        <div class="col-md-6 text-end">
            <small class="text-muted">
                Total Customers: <strong>${totalCustomers != null ? totalCustomers : 0}</strong>
            </small>
        </div>
    </div>

    <!-- Customer Table -->
    <div class="card">
        <div class="card-header">
            <h5 class="card-title mb-0">
                <i class="bi bi-list"></i> Customer List
                <c:if test="${not empty param.searchTerm}">
                    - Search Results for "${param.searchTerm}"
                </c:if>
            </h5>
        </div>
        <div class="card-body">
            <c:choose>
                <c:when test="${not empty customers}">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead class="table-light">
                            <tr>
                                <th>Account Number</th>
                                <th>Name</th>
                                <th>Phone</th>
                                <th>Address</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${customers}" var="customer">
                                <tr>
                                    <td>
                                        <strong>${customer.accountNumber}</strong>
                                    </td>
                                    <td>
                                        <div>
                                            <strong>${customer.fullName}</strong>
                                            <c:if test="${not empty customer.email}">
                                                <br><small class="text-muted">${customer.email}</small>
                                            </c:if>
                                        </div>
                                    </td>
                                    <td>${customer.phoneNumber}</td>
                                    <td>
                                        <small>${customer.address}</small>
                                    </td>
                                    <td>
                                            <span class="badge bg-${customer.active ? 'success' : 'danger'}">
                                                    ${customer.active ? 'Active' : 'Inactive'}
                                            </span>
                                    </td>
                                    <td>
                                        <div class="btn-group btn-group-sm">
                                            <a href="${pageContext.request.contextPath}/customers?action=view&id=${customer.accountNumber}"
                                               class="btn btn-outline-info" title="View Details">
                                                <i class="bi bi-eye"></i>
                                            </a>
                                            <a href="${pageContext.request.contextPath}/customers?action=edit&id=${customer.accountNumber}"
                                               class="btn btn-outline-warning" title="Edit">
                                                <i class="bi bi-pencil"></i>
                                            </a>
                                            <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                                                <button type="button"
                                                        class="btn btn-outline-danger"
                                                        title="Delete"
                                                        onclick="confirmDelete('${customer.accountNumber}', '${customer.fullName}')">
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

                    <!-- Pagination (if needed) -->
                    <c:if test="${totalPages > 1}">
                        <nav class="mt-3">
                            <ul class="pagination justify-content-center">
                                <c:if test="${currentPage > 1}">
                                    <li class="page-item">
                                        <a class="page-link" href="?page=${currentPage - 1}&searchTerm=${param.searchTerm}">Previous</a>
                                    </li>
                                </c:if>

                                <c:forEach begin="1" end="${totalPages}" var="i">
                                    <li class="page-item ${i == currentPage ? 'active' : ''}">
                                        <a class="page-link" href="?page=${i}&searchTerm=${param.searchTerm}">${i}</a>
                                    </li>
                                </c:forEach>

                                <c:if test="${currentPage < totalPages}">
                                    <li class="page-item">
                                        <a class="page-link" href="?page=${currentPage + 1}&searchTerm=${param.searchTerm}">Next</a>
                                    </li>
                                </c:if>
                            </ul>
                        </nav>
                    </c:if>
                </c:when>

                <c:otherwise>
                    <div class="text-center py-5">
                        <i class="bi bi-people text-muted" style="font-size: 3rem;"></i>
                        <h5 class="text-muted mt-3">
                            <c:choose>
                                <c:when test="${not empty param.searchTerm}">
                                    No customers found matching "${param.searchTerm}"
                                </c:when>
                                <c:otherwise>
                                    No customers registered yet
                                </c:otherwise>
                            </c:choose>
                        </h5>
                        <p class="text-muted">
                            <c:choose>
                                <c:when test="${not empty param.searchTerm}">
                                    Try a different search term or
                                    <a href="${pageContext.request.contextPath}/customers">view all customers</a>
                                </c:when>
                                <c:otherwise>
                                    Start by adding your first customer to the system
                                </c:otherwise>
                            </c:choose>
                        </p>
                        <a href="${pageContext.request.contextPath}/customers?action=create" class="btn btn-primary">
                            <i class="bi bi-person-plus"></i> Add First Customer
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <!-- Quick Actions -->
    <div class="row mt-4">
        <div class="col-md-4">
            <div class="card">
                <div class="card-body text-center">
                    <i class="bi bi-person-plus text-primary" style="font-size: 2rem;"></i>
                    <h6 class="mt-2">Add Customer</h6>
                    <a href="${pageContext.request.contextPath}/customers?action=create" class="btn btn-sm btn-primary">
                        Create New
                    </a>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card">
                <div class="card-body text-center">
                    <i class="bi bi-download text-info" style="font-size: 2rem;"></i>
                    <h6 class="mt-2">Export Data</h6>
                    <button class="btn btn-sm btn-info" onclick="exportCustomers()">
                        Download CSV
                    </button>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card">
                <div class="card-body text-center">
                    <i class="bi bi-graph-up text-success" style="font-size: 2rem;"></i>
                    <h6 class="mt-2">Customer Report</h6>
                    <a href="${pageContext.request.contextPath}/reports?type=customers" class="btn btn-sm btn-success">
                        View Report
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />

<script>
    // Delete confirmation
    function confirmDelete(accountNumber, customerName) {
        if (confirm(`Are you sure you want to delete customer "${customerName}" (${accountNumber})?\n\nThis action cannot be undone.`)) {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '${pageContext.request.contextPath}/customers';

            const actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            actionInput.value = 'delete';

            const idInput = document.createElement('input');
            idInput.type = 'hidden';
            idInput.name = 'id';
            idInput.value = accountNumber;

            form.appendChild(actionInput);
            form.appendChild(idInput);
            document.body.appendChild(form);
            form.submit();
        }
    }

    // Export customers function
    function exportCustomers() {
        window.open('${pageContext.request.contextPath}/customers?action=export&format=csv', '_blank');
    }

    // Search functionality enhancement
    document.addEventListener('DOMContentLoaded', function() {
        const searchInput = document.querySelector('input[name="searchTerm"]');
        if (searchInput) {
            searchInput.focus();
        }
    });
</script>