<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Customer Management" />
<jsp:include page="../common/header.jsp" />

<div class="container-fluid">
    <div class="row">
        <div class="col-12">
            <!-- Page Header -->
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 class="h3 mb-0">
                        <i class="bi bi-people"></i> Customer Management
                    </h2>
                    <p class="text-muted">Manage customer accounts and information</p>
                </div>
                <a href="${pageContext.request.contextPath}/customers?action=create" class="btn btn-primary">
                    <i class="bi bi-person-plus"></i> Add New Customer
                </a>
            </div>

            <!-- Error/Success Messages -->
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-triangle"></i> ${errorMessage}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <c:if test="${not empty successMessage}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="bi bi-check-circle"></i> ${successMessage}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <!-- Search and Filter Section -->
            <div class="card mb-4">
                <div class="card-body">
                    <form method="get" action="${pageContext.request.contextPath}/customers" class="row g-3">
                        <div class="col-md-4">
                            <label for="search" class="form-label">Search Customers</label>
                            <input type="text" class="form-control" id="search" name="search"
                                   value="${param.search}" placeholder="Search by name, account, or email...">
                        </div>
                        <div class="col-md-3">
                            <label for="status" class="form-label">Status</label>
                            <select class="form-select" id="status" name="status">
                                <option value="">All Customers</option>
                                <option value="active" ${param.status == 'active' ? 'selected' : ''}>Active Only</option>
                                <option value="inactive" ${param.status == 'inactive' ? 'selected' : ''}>Inactive Only</option>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <label for="sortBy" class="form-label">Sort By</label>
                            <select class="form-select" id="sortBy" name="sortBy">
                                <option value="name" ${param.sortBy == 'name' ? 'selected' : ''}>Name</option>
                                <option value="accountNumber" ${param.sortBy == 'accountNumber' ? 'selected' : ''}>Account Number</option>
                                <option value="registrationDate" ${param.sortBy == 'registrationDate' ? 'selected' : ''}>Registration Date</option>
                            </select>
                        </div>
                        <div class="col-md-2">
                            <label class="form-label">&nbsp;</label>
                            <div class="d-grid">
                                <button type="submit" class="btn btn-outline-primary">
                                    <i class="bi bi-search"></i> Search
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Customers Table -->
            <div class="card">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h5 class="card-title mb-0">
                        <i class="bi bi-table"></i> Customer List
                    </h5>
                    <small class="text-muted">
                        Total: ${customers.size()} customers
                    </small>
                </div>
                <div class="card-body p-0">
                    <c:choose>
                        <c:when test="${not empty customers}">
                            <div class="table-responsive">
                                <table class="table table-hover mb-0">
                                    <thead class="table-light">
                                    <tr>
                                        <th>Account Number</th>
                                        <th>Customer Details</th>
                                        <th>Contact Info</th>
                                        <th>Status</th>
                                        <th>Actions</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="customer" items="${customers}">
                                        <tr>
                                            <td>
                                                <span class="fw-bold text-primary">${customer.accountNumber}</span>
                                            </td>
                                            <td>
                                                <div>
                                                    <!-- FIXED: Changed from customer.fullName to customer.name -->
                                                    <strong>${customer.name}</strong>
                                                    <c:if test="${not empty customer.email}">
                                                        <br><small class="text-muted">${customer.email}</small>
                                                    </c:if>
                                                    <c:if test="${not empty customer.address}">
                                                        <br><small class="text-muted">
                                                        <i class="bi bi-geo-alt"></i>
                                                            ${customer.address.length() > 30 ? customer.address.substring(0, 30).concat('...') : customer.address}
                                                    </small>
                                                    </c:if>
                                                </div>
                                            </td>
                                            <td>
                                                <c:if test="${not empty customer.phoneNumber}">
                                                    <div class="mb-1">
                                                        <i class="bi bi-telephone"></i> ${customer.phoneNumber}
                                                    </div>
                                                </c:if>
                                                <c:if test="${not empty customer.email}">
                                                    <div>
                                                        <i class="bi bi-envelope"></i>
                                                        <small>${customer.email}</small>
                                                    </div>
                                                </c:if>
                                                <c:if test="${empty customer.phoneNumber && empty customer.email}">
                                                    <small class="text-muted">No contact info</small>
                                                </c:if>
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
                                                       class="btn btn-outline-warning" title="Edit Customer">
                                                        <i class="bi bi-pencil"></i>
                                                    </a>
                                                    <a href="${pageContext.request.contextPath}/bills?action=create&customerId=${customer.accountNumber}"
                                                       class="btn btn-outline-success" title="Create Bill">
                                                        <i class="bi bi-receipt"></i>
                                                    </a>
                                                    <button type="button" class="btn btn-outline-danger"
                                                            onclick="confirmDelete('${customer.accountNumber}', '${customer.name}')"
                                                            title="Delete Customer">
                                                        <i class="bi bi-trash"></i>
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>

                            <!-- Pagination -->
                            <c:if test="${totalPages > 1}">
                                <div class="d-flex justify-content-center mt-3">
                                    <nav aria-label="Customer pagination">
                                        <ul class="pagination">
                                            <c:if test="${currentPage > 1}">
                                                <li class="page-item">
                                                    <a class="page-link" href="?page=${currentPage - 1}&search=${param.search}&status=${param.status}&sortBy=${param.sortBy}">
                                                        Previous
                                                    </a>
                                                </li>
                                            </c:if>

                                            <c:forEach begin="1" end="${totalPages}" var="pageNum">
                                                <li class="page-item ${pageNum == currentPage ? 'active' : ''}">
                                                    <a class="page-link" href="?page=${pageNum}&search=${param.search}&status=${param.status}&sortBy=${param.sortBy}">
                                                            ${pageNum}
                                                    </a>
                                                </li>
                                            </c:forEach>

                                            <c:if test="${currentPage < totalPages}">
                                                <li class="page-item">
                                                    <a class="page-link" href="?page=${currentPage + 1}&search=${param.search}&status=${param.status}&sortBy=${param.sortBy}">
                                                        Next
                                                    </a>
                                                </li>
                                            </c:if>
                                        </ul>
                                    </nav>
                                </div>
                            </c:if>

                        </c:when>
                        <c:otherwise>
                            <div class="text-center py-5">
                                <i class="bi bi-people" style="font-size: 3rem; color: #ccc;"></i>
                                <h5 class="mt-3 text-muted">No Customers Found</h5>
                                <p class="text-muted">
                                    <c:choose>
                                        <c:when test="${not empty param.search}">
                                            No customers match your search criteria.
                                        </c:when>
                                        <c:otherwise>
                                            Start by adding your first customer to the system.
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


        </div>
    </div>
</div>

<!-- JavaScript for additional functionality -->
<script>
    function confirmDelete(accountNumber, customerName) {
        if (confirm('Are you sure you want to delete customer "' + customerName + '" (' + accountNumber + ')?\n\nThis action will deactivate the customer account.')) {
            // Create and submit form
            var form = document.createElement('form');
            form.method = 'POST';
            form.action = '${pageContext.request.contextPath}/customers';

            var actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            actionInput.value = 'delete';

            var idInput = document.createElement('input');
            idInput.type = 'hidden';
            idInput.name = 'id';
            idInput.value = accountNumber;

            form.appendChild(actionInput);
            form.appendChild(idInput);
            document.body.appendChild(form);

            console.log('Submitting delete form for customer: ' + accountNumber);
            form.submit();
        }
    }

    function exportCustomers(format) {
        const searchParams = new URLSearchParams(window.location.search);
        searchParams.set('export', format);
        window.location.href = '${pageContext.request.contextPath}/customers?' + searchParams.toString();
    }

    // Auto-submit search form on status/sort change
    document.addEventListener('DOMContentLoaded', function() {
        var statusSelect = document.getElementById('status');
        var sortSelect = document.getElementById('sortBy');

        if (statusSelect) {
            statusSelect.addEventListener('change', function() {
                this.form.submit();
            });
        }

        if (sortSelect) {
            sortSelect.addEventListener('change', function() {
                this.form.submit();
            });
        }
    });
</script>

<jsp:include page="../common/footer.jsp" />