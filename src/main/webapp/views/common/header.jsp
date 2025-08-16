<%--
  Created by IntelliJ IDEA.
  User: MOHAMED
  Date: 16/08/2025
  Time: 11:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle != null ? pageTitle : 'Pahana Edu'} - Billing System</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet">

    <!-- Custom styles (minimal) -->
    <style>
        .navbar-brand { font-weight: bold; }
        .sidebar { min-height: calc(100vh - 56px); }
        .main-content { min-height: calc(100vh - 56px); }
        .user-info { font-size: 0.9em; }
    </style>
</head>
<body>
<!-- Navigation Bar -->
<nav class="navbar navbar-expand-lg navbar-dark bg-primary">
    <div class="container-fluid">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard">
            <i class="bi bi-shop"></i> Pahana Edu
        </a>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto">
                <c:if test="${sessionScope.currentUser != null}">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/dashboard">
                            <i class="bi bi-house"></i> Dashboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/customers">
                            <i class="bi bi-people"></i> Customers
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/items">
                            <i class="bi bi-box"></i> Items
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/bills">
                            <i class="bi bi-receipt"></i> Bills
                        </a>
                    </li>
                </c:if>
            </ul>

            <!-- User Info and Logout -->
            <div class="navbar-nav">
                <c:if test="${sessionScope.currentUser != null}">
                        <span class="navbar-text user-info me-3">
                            <i class="bi bi-person-circle"></i> ${sessionScope.currentUser.fullName}
                            <span class="badge bg-secondary">${sessionScope.currentUser.role}</span>
                        </span>
                    <a class="btn btn-outline-light btn-sm" href="${pageContext.request.contextPath}/logout">
                        <i class="bi bi-box-arrow-right"></i> Logout
                    </a>
                </c:if>
                <c:if test="${sessionScope.currentUser == null}">
                    <a class="btn btn-outline-light btn-sm" href="${pageContext.request.contextPath}/login">
                        <i class="bi bi-box-arrow-in-right"></i> Login
                    </a>
                </c:if>
            </div>
        </div>
    </div>
</nav>

<!-- Main Container -->
<div class="container-fluid">
    <div class="row">
        <!-- Sidebar (only show when logged in) -->
        <c:if test="${sessionScope.currentUser != null}">
        <div class="col-md-2 bg-light sidebar p-3">
            <h6 class="text-muted">NAVIGATION</h6>
            <ul class="nav flex-column">
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/dashboard">
                        <i class="bi bi-speedometer2"></i> Dashboard
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/customers">
                        <i class="bi bi-people"></i> Manage Customers
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/customers?action=create">
                        <i class="bi bi-person-plus"></i> Add Customer
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/items">
                        <i class="bi bi-box-seam"></i> Manage Items
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/items?action=create">
                        <i class="bi bi-plus-square"></i> Add Item
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/bills">
                        <i class="bi bi-receipt-cutoff"></i> View Bills
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/bills?action=create">
                        <i class="bi bi-plus-circle"></i> Create Bill
                    </a>
                </li>
            </ul>

            <hr>

            <h6 class="text-muted">SEARCH</h6>
            <ul class="nav flex-column">
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/customers/search">
                        <i class="bi bi-search"></i> Search Customers
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/items/search">
                        <i class="bi bi-search"></i> Search Items
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/bills/search">
                        <i class="bi bi-search"></i> Search Bills
                    </a>
                </li>
            </ul>
        </div>
        </c:if>

        <!-- Main Content Area -->
        <div class="${sessionScope.currentUser != null ? 'col-md-10' : 'col-12'} main-content p-4">

            <!-- Alert Messages -->
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

            <c:if test="${not empty infoMessage}">
            <div class="alert alert-info alert-dismissible fade show" role="alert">
                <i class="bi bi-info-circle"></i> ${infoMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            </c:if>

            <!-- Page Content Starts Here -->