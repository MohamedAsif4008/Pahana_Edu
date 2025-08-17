<%--
  Simple Dashboard JSP - Fixed version
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Dashboard" />
<jsp:include page="header.jsp" />

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
                <h5 class="card-title mb-0">
                    <i class="bi bi-lightning"></i> Quick Actions
                </h5>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-3">
                        <a href="/Pahana_Edu/customers?action=create" class="btn btn-primary w-100 mb-2">
                            <i class="bi bi-person-plus"></i> Add Customer
                        </a>
                    </div>
                    <div class="col-md-3">
                        <a href="/Pahana_Edu/items?action=create" class="btn btn-success w-100 mb-2">
                            <i class="bi bi-plus-square"></i> Add Item
                        </a>
                    </div>
                    <div class="col-md-3">
                        <a href="/Pahana_Edu/bills?action=create" class="btn btn-info w-100 mb-2">
                            <i class="bi bi-plus-circle"></i> Create Bill
                        </a>
                    </div>
                    <div class="col-md-3">
                        <a href="/Pahana_Edu/reports" class="btn btn-warning w-100 mb-2">
                            <i class="bi bi-graph-up"></i> View Reports
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- System Status -->
<div class="row">
    <div class="col-md-6">
        <div class="card">
            <div class="card-header">
                <h5 class="card-title mb-0">
                    <i class="bi bi-info-circle"></i> System Status
                </h5>
            </div>
            <div class="card-body">
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <span>Database Connection</span>
                    <span class="badge bg-success">Connected</span>
                </div>
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <span>User Role</span>
                    <span class="badge bg-primary">${sessionScope.currentUser.role}</span>
                </div>
                <div class="d-flex justify-content-between align-items-center">
                    <span>Last Login</span>
                    <span class="text-muted">Just now</span>
                </div>
            </div>
        </div>
    </div>

    <div class="col-md-6">
        <div class="card">
            <div class="card-header">
                <h5 class="card-title mb-0">
                    <i class="bi bi-list-check"></i> Quick Links
                </h5>
            </div>
            <div class="card-body">
                <ul class="list-unstyled mb-0">
                    <li class="mb-2">
                        <a href="/Pahana_Edu/customers" class="text-decoration-none">
                            <i class="bi bi-people"></i> Manage Customers
                        </a>
                    </li>
                    <li class="mb-2">
                        <a href="/Pahana_Edu/items" class="text-decoration-none">
                            <i class="bi bi-box-seam"></i> Manage Items
                        </a>
                    </li>
                    <li class="mb-2">
                        <a href="/Pahana_Edu/bills" class="text-decoration-none">
                            <i class="bi bi-receipt"></i> View Bills
                        </a>
                    </li>
                    <li>
                        <a href="/Pahana_Edu/profile" class="text-decoration-none">
                            <i class="bi bi-person-gear"></i> My Profile
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</div>

<!-- Admin Tools (only for admin users) -->
<c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
    <div class="row mt-4">
        <div class="col-md-12">
            <div class="card">
                <div class="card-header">
                    <h5 class="card-title mb-0">
                        <i class="bi bi-gear"></i> Admin Tools
                    </h5>
                </div>
                <div class="card-body">
                    <p class="text-muted">Additional management options:</p>
                    <div class="row">
                        <div class="col-md-3">
                            <a href="/Pahana_Edu/users" class="btn btn-outline-dark w-100">
                                <i class="bi bi-people"></i> Manage Users
                            </a>
                        </div>
                        <div class="col-md-3">
                            <a href="/Pahana_Edu/reports" class="btn btn-outline-dark w-100">
                                <i class="bi bi-graph-up"></i> View Reports
                            </a>
                        </div>
                        <div class="col-md-3">
                            <a href="/Pahana_Edu/settings" class="btn btn-outline-dark w-100">
                                <i class="bi bi-gear"></i> System Settings
                            </a>
                        </div>
                        <div class="col-md-3">
                            <a href="/Pahana_Edu/backup" class="btn btn-outline-dark w-100">
                                <i class="bi bi-download"></i> Backup Data
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</c:if>

</div> <!-- End main-content -->
</div> <!-- End row -->
</div> <!-- End container-fluid -->

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>