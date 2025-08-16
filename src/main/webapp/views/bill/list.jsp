<%--
  Created by IntelliJ IDEA.
  User: MOHAMED
  Date: 16/08/2025
  Time: 13:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Bills" />
<jsp:include page="../common/header.jsp" />

<!-- Page Content -->
<div class="col-md-10 main-content p-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="h3">
            <i class="bi bi-receipt"></i> Manage Bills
        </h1>
        <div>
            <a href="${pageContext.request.contextPath}/bills?action=create" class="btn btn-primary">
                <i class="bi bi-plus-circle"></i> Create New Bill
            </a>
        </div>
    </div>

    <!-- Search Bar -->
    <div class="row mb-4">
        <div class="col-md-6">
            <form action="${pageContext.request.contextPath}/bills" method="get" class="d-flex">
                <input type="hidden" name="action" value="search">
                <input type="text"
                       class="form-control me-2"
                       name="searchTerm"
                       value="${param.searchTerm}"
                       placeholder="Search by bill number, customer name...">
                <button type="submit" class="btn btn-outline-primary">
                    <i class="bi bi-search"></i> Search
                </button>
            </form>
        </div>
        <div class="col-md-6 text-end">
            <small class="text-muted">
                Total Bills: <strong>${totalBills != null ? totalBills : 0}</strong> |
                Total Amount: <strong>Rs. ${totalAmount != null ? totalAmount : '0.00'}</strong>
            </small>
        </div>
    </div>

    <!-- Bills Table -->
    <div class="card">
        <div class="card-header">
            <h5 class="card-title mb-0">
                <i class="bi bi-list"></i> Bills List
                <c:if test="${not empty param.searchTerm}">
                    - Search Results for "${param.searchTerm}"
                </c:if>
            </h5>
        </div>
        <div class="card-body">
            <c:choose>
                <c:when test="${not empty bills}">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead class="table-light">
                            <tr>
                                <th>Bill Number</th>
                                <th>Customer</th>
                                <th>Items</th>
                                <th>Total Amount</th>
                                <th>Payment Method</th>
                                <th>Date</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${bills}" var="bill">
                                <tr>
                                    <td>
                                        <strong>${bill.billNumber}</strong>
                                    </td>
                                    <td>
                                        <div>
                                            <strong>${bill.customer.fullName}</strong>
                                            <br><small class="text-muted">${bill.customer.accountNumber}</small>
                                        </div>
                                    </td>
                                    <td>
                                        <span class="badge bg-info">${bill.billItems.size()} items</span>
                                    </td>
                                    <td>
                                        <strong class="text-success">Rs. <fmt:formatNumber value="${bill.totalAmount}" pattern="#,##0.00"/></strong>
                                    </td>
                                    <td>
                                            <span class="badge bg-${bill.paymentMethod == 'CASH' ? 'success' : 'primary'}">
                                                    ${bill.paymentMethod}
                                            </span>
                                    </td>
                                    <td>
                                        <fmt:formatDate value="${bill.billDate}" pattern="MMM dd, yyyy"/>
                                    </td>
                                    <td>
                                            <span class="badge bg-${bill.status == 'PAID' ? 'success' : bill.status == 'PENDING' ? 'warning' : 'danger'}">
                                                    ${bill.status}
                                            </span>
                                    </td>
                                    <td>
                                        <div class="btn-group btn-group-sm">
                                            <a href="${pageContext.request.contextPath}/bills?action=view&id=${bill.billNumber}"
                                               class="btn btn-outline-info" title="View & Print">
                                                <i class="bi bi-eye"></i>
                                            </a>
                                            <a href="${pageContext.request.contextPath}/bills?action=print&id=${bill.billNumber}"
                                               class="btn btn-outline-success" title="Print Bill" target="_blank">
                                                <i class="bi bi-printer"></i>
                                            </a>
                                            <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                                                <button type="button"
                                                        class="btn btn-outline-danger"
                                                        title="Delete"
                                                        onclick="confirmDelete('${bill.billNumber}', '${bill.totalAmount}')">
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
                        <i class="bi bi-receipt text-muted" style="font-size: 3rem;"></i>
                        <h5 class="text-muted mt-3">
                            <c:choose>
                                <c:when test="${not empty param.searchTerm}">
                                    No bills found matching "${param.searchTerm}"
                                </c:when>
                                <c:otherwise>
                                    No bills created yet
                                </c:otherwise>
                            </c:choose>
                        </h5>
                        <p class="text-muted">
                            <c:choose>
                                <c:when test="${not empty param.searchTerm}">
                                    Try a different search term or
                                    <a href="${pageContext.request.contextPath}/bills">view all bills</a>
                                </c:when>
                                <c:otherwise>
                                    Start by creating your first bill
                                </c:otherwise>
                            </c:choose>
                        </p>
                        <a href="${pageContext.request.contextPath}/bills?action=create" class="btn btn-primary">
                            <i class="bi bi-plus-circle"></i> Create First Bill
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <!-- Bill Statistics -->
    <div class="row mt-4">
        <div class="col-md-3">
            <div class="card bg-primary text-white">
                <div class="card-body text-center">
                    <i class="bi bi-receipt" style="font-size: 2rem;"></i>
                    <h4 class="mt-2">${totalBills != null ? totalBills : 0}</h4>
                    <p class="mb-0">Total Bills</p>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card bg-success text-white">
                <div class="card-body text-center">
                    <i class="bi bi-check-circle" style="font-size: 2rem;"></i>
                    <h4 class="mt-2">${paidBills != null ? paidBills : 0}</h4>
                    <p class="mb-0">Paid Bills</p>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card bg-info text-white">
                <div class="card-body text-center">
                    <i class="bi bi-currency-dollar" style="font-size: 2rem;"></i>
                    <h4 class="mt-2">Rs. ${totalAmount != null ? totalAmount : '0'}</h4>
                    <p class="mb-0">Total Revenue</p>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card bg-warning text-white">
                <div class="card-body text-center">
                    <i class="bi bi-graph-up" style="font-size: 2rem;"></i>
                    <h4 class="mt-2">Rs. ${avgBillAmount != null ? avgBillAmount : '0'}</h4>
                    <p class="mb-0">Avg. Bill Amount</p>
                </div>
            </div>
        </div>
    </div>

    <!-- Quick Actions -->
    <div class="row mt-4">
        <div class="col-md-4">
            <div class="card">
                <div class="card-body text-center">
                    <i class="bi bi-plus-circle text-primary" style="font-size: 2rem;"></i>
                    <h6 class="mt-2">New Bill</h6>
                    <a href="${pageContext.request.contextPath}/bills?action=create" class="btn btn-sm btn-primary">
                        Create Bill
                    </a>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card">
                <div class="card-body text-center">
                    <i class="bi bi-download text-success" style="font-size: 2rem;"></i>
                    <h6 class="mt-2">Export Bills</h6>
                    <button class="btn btn-sm btn-success" onclick="exportBills()">
                        Download CSV
                    </button>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card">
                <div class="card-body text-center">
                    <i class="bi bi-graph-up text-info" style="font-size: 2rem;"></i>
                    <h6 class="mt-2">Sales Report</h6>
                    <a href="${pageContext.request.contextPath}/reports?type=sales" class="btn btn-sm btn-info">
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
    function confirmDelete(billNumber, amount) {
        if (confirm(`Are you sure you want to delete bill "${billNumber}" (Rs. ${amount})?\n\nThis action cannot be undone.`)) {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '${pageContext.request.contextPath}/bills';

            const actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            actionInput.value = 'delete';

            const idInput = document.createElement('input');
            idInput.type = 'hidden';
            idInput.name = 'id';
            idInput.value = billNumber;

            form.appendChild(actionInput);
            form.appendChild(idInput);
            document.body.appendChild(form);
            form.submit();
        }
    }

    // Export bills function
    function exportBills() {
        window.open('${pageContext.request.contextPath}/bills?action=export&format=csv', '_blank');
    }

    // Search functionality enhancement
    document.addEventListener('DOMContentLoaded', function() {
        const searchInput = document.querySelector('input[name="searchTerm"]');
        if (searchInput) {
            searchInput.focus();
        }
    });
</script>