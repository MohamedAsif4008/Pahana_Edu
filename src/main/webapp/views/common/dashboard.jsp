<%--
  Created by IntelliJ IDEA.
  User: MOHAMED
  Date: 16/08/2025
  Time: 11:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Dashboard" />
<jsp:include page="common/header.jsp" />

<!-- Dashboard Content -->
<div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h3">
        <i class="bi bi-speedometer2"></i> Dashboard
    </h1>
    <div class="text-muted">
        Welcome back, <strong>${sessionScope.currentUser.fullName}</strong>!
    </div>
</div>

<!-- Quick Stats Cards -->
<div class="row mb-4">
    <div class="col-md-3">
        <div class="card bg-primary text-white">
            <div class="card-body">
                <div class="d-flex justify-content-between">
                    <div>
                        <h5 class="card-title">Total Customers</h5>
                        <h2 class="mb-0">${totalCustomers != null ? totalCustomers : '0'}</h2>
                    </div>
                    <div class="align-self-center">
                        <i class="bi bi-people" style="font-size: 2rem;"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="col-md-3">
        <div class="card bg-success text-white">
            <div class="card-body">
                <div class="d-flex justify-content-between">
                    <div>
                        <h5 class="card-title">Total Items</h5>
                        <h2 class="mb-0">${totalItems != null ? totalItems : '0'}</h2>
                    </div>
                    <div class="align-self-center">
                        <i class="bi bi-box" style="font-size: 2rem;"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="col-md-3">
        <div class="card bg-info text-white">
            <div class="card-body">
                <div class="d-flex justify-content-between">
                    <div>
                        <h5 class="card-title">Total Bills</h5>
                        <h2 class="mb-0">${totalBills != null ? totalBills : '0'}</h2>
                    </div>
                    <div class="align-self-center">
                        <i class="bi bi-receipt" style="font-size: 2rem;"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="col-md-3">
        <div class="card bg-warning text-white">
            <div class="card-body">
                <div class="d-flex justify-content-between">
                    <div>
                        <h5 class="card-title">Low Stock Items</h5>
                        <h2 class="mb-0">${lowStockCount != null ? lowStockCount : '0'}</h2>
                    </div>
                    <div class="align-self-center">
                        <i class="bi bi-exclamation-triangle" style="font-size: 2rem;"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Quick Actions -->
<div class="row mb-4">
    <div class="col-md-12">
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0"><i class="bi bi-lightning"></i> Quick Actions</h5>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-4 mb-3">
                        <a href="${pageContext.request.contextPath}/bills?action=create"
                           class="btn btn-primary btn-lg w-100">
                            <i class="bi bi-plus-circle"></i><br>
                            Create New Bill
                        </a>
                    </div>
                    <div class="col-md-4 mb-3">
                        <a href="${pageContext.request.contextPath}/customers?action=create"
                           class="btn btn-success btn-lg w-100">
                            <i class="bi bi-person-plus"></i><br>
                            Add Customer
                        </a>
                    </div>
                    <div class="col-md-4 mb-3">
                        <a href="${pageContext.request.contextPath}/items?action=create"
                           class="btn btn-info btn-lg w-100">
                            <i class="bi bi-plus-square"></i><br>
                            Add Item
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Recent Activity -->
<div class="row">
    <div class="col-md-6">
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0"><i class="bi bi-clock-history"></i> Recent Bills</h5>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${not empty recentBills}">
                        <div class="table-responsive">
                            <table class="table table-sm">
                                <thead>
                                <tr>
                                    <th>Bill #</th>
                                    <th>Customer</th>
                                    <th>Amount</th>
                                    <th>Date</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${recentBills}" var="bill" end="4">
                                    <tr>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/bills?action=view&id=${bill.billNumber}">
                                                    ${bill.billNumber}
                                            </a>
                                        </td>
                                        <td>${bill.customerName}</td>
                                        <td>Rs. ${bill.totalAmount}</td>
                                        <td><small class="text-muted">${bill.billDate}</small></td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        <div class="text-center">
                            <a href="${pageContext.request.contextPath}/bills" class="btn btn-sm btn-outline-primary">
                                View All Bills
                            </a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="text-center text-muted py-3">
                            <i class="bi bi-receipt" style="font-size: 2rem;"></i>
                            <p>No bills created yet</p>
                            <a href="${pageContext.request.contextPath}/bills?action=create"
                               class="btn btn-primary btn-sm">
                                Create First Bill
                            </a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <div class="col-md-6">
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0"><i class="bi bi-exclamation-triangle"></i> Low Stock Alert</h5>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${not empty lowStockItems}">
                        <div class="table-responsive">
                            <table class="table table-sm">
                                <thead>
                                <tr>
                                    <th>Item</th>
                                    <th>Current Stock</th>
                                    <th>Reorder Level</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${lowStockItems}" var="item" end="4">
                                    <tr>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/items?action=view&id=${item.itemId}">
                                                    ${item.name}
                                            </a>
                                        </td>
                                        <td>
                                            <span class="badge bg-warning">${item.stockQuantity}</span>
                                        </td>
                                        <td>${item.reorderLevel}</td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        <div class="text-center">
                            <a href="${pageContext.request.contextPath}/items" class="btn btn-sm btn-outline-warning">
                                Manage Inventory
                            </a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="text-center text-muted py-3">
                            <i class="bi bi-check-circle" style="font-size: 2rem; color: green;"></i>
                            <p>All items are well stocked!</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<!-- User Role Specific Content -->
<c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
    <div class="row mt-4">
        <div class="col-md-12">
            <div class="card border-warning">
                <div class="card-header bg-warning text-dark">
                    <h5 class="mb-0"><i class="bi bi-shield-check"></i> Administrator Panel</h5>
                </div>
                <div class="card-body">
                    <p>You have administrator privileges. Additional management options:</p>
                    <div class="row">
                        <div class="col-md-3">
                            <a href="${pageContext.request.contextPath}/users" class="btn btn-outline-dark w-100">
                                <i class="bi bi-people"></i> Manage Users
                            </a>
                        </div>
                        <div class="col-md-3">
                            <a href="${pageContext.request.contextPath}/reports" class="btn btn-outline-dark w-100">
                                <i class="bi bi-graph-up"></i> View Reports
                            </a>
                        </div>
                        <div class="col-md-3">
                            <a href="${pageContext.request.contextPath}/settings" class="btn btn-outline-dark w-100">
                                <i class="bi bi-gear"></i> System Settings
                            </a>
                        </div>
                        <div class="col-md-3">
                            <a href="${pageContext.request.contextPath}/backup" class="btn btn-outline-dark w-100">
                                <i class="bi bi-download"></i> Backup Data
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</c:if>

<jsp:include page="common/footer.jsp" />