<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Customer Details" />
<jsp:include page="../common/header.jsp" />

<div class="container-fluid">
    <div class="row">
        <div class="col-12">
            <!-- Page Header -->
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 class="h3 mb-0">
                        <i class="bi bi-person-circle"></i> Customer Details
                    </h2>
                </div>
                <div>
                    <a href="${pageContext.request.contextPath}/customers" class="btn btn-outline-secondary me-2">
                        <i class="bi bi-arrow-left"></i> Back to List
                    </a>
                    <a href="${pageContext.request.contextPath}/customers?action=edit&id=${customer.accountNumber}" class="btn btn-primary">
                        <i class="bi bi-pencil"></i> Edit Customer
                    </a>
                </div>
            </div>

            <!-- Customer Information -->
            <div class="row">
                <div class="col-md-8">
                    <div class="card">
                        <div class="card-header">
                            <h5 class="card-title mb-0">Customer Information</h5>
                        </div>
                        <div class="card-body">
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <strong>Account Number:</strong><br>
                                    <span class="badge bg-primary">${customer.accountNumber}</span>
                                </div>

                                <div class="col-md-6 mb-3">
                                    <strong>Status:</strong><br>
                                    <span class="badge bg-${customer.active ? 'success' : 'danger'}">
                                        ${customer.active ? 'Active' : 'Inactive'}
                                    </span>
                                </div>

                                <div class="col-md-12 mb-3">
                                    <strong>Full Name:</strong><br>
                                    ${customer.name}
                                </div>

                                <div class="col-md-6 mb-3">
                                    <strong>Email:</strong><br>
                                    <c:choose>
                                        <c:when test="${not empty customer.email}">
                                            <a href="mailto:${customer.email}">${customer.email}</a>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted">Not provided</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>

                                <div class="col-md-6 mb-3">
                                    <strong>Phone:</strong><br>
                                    <c:choose>
                                        <c:when test="${not empty customer.phoneNumber}">
                                            <a href="tel:${customer.phoneNumber}">${customer.phoneNumber}</a>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted">Not provided</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>

                                <div class="col-md-12 mb-3">
                                    <strong>Address:</strong><br>
                                    <c:choose>
                                        <c:when test="${not empty customer.address}">
                                            ${customer.address}
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted">Not provided</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>

                                <div class="col-md-6 mb-3">
                                    <strong>Credit Limit:</strong><br>
                                    <c:choose>
                                        <c:when test="${customer.creditLimit != null && customer.creditLimit > 0}">
                                            <span class="text-success">Rs. ${customer.creditLimit}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted">No credit limit</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Quick Actions -->
                <div class="col-md-4">
                    <div class="card">
                        <div class="card-header">
                            <h6 class="card-title mb-0">Quick Actions</h6>
                        </div>
                        <div class="card-body">
                            <div class="d-grid gap-2">
                                <a href="${pageContext.request.contextPath}/customers?action=edit&id=${customer.accountNumber}"
                                   class="btn btn-warning">
                                    <i class="bi bi-pencil"></i> Edit Customer
                                </a>

                                <a href="${pageContext.request.contextPath}/bills?action=create&customerId=${customer.accountNumber}"
                                   class="btn btn-success">
                                    <i class="bi bi-receipt"></i> Create Bill
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />