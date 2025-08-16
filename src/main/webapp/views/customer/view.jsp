<%--
  Created by IntelliJ IDEA.
  User: MOHAMED
  Date: 16/08/2025
  Time: 12:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Customer Details" />
<jsp:include page="../common/header.jsp" />

<!-- Page Content -->
<div class="col-md-10 main-content p-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="h3">
            <i class="bi bi-person-circle"></i> Customer Details
        </h1>
        <div>
            <a href="${pageContext.request.contextPath}/customers?action=edit&id=${customer.accountNumber}" class="btn btn-warning me-2">
                <i class="bi bi-pencil"></i> Edit Customer
            </a>
            <a href="${pageContext.request.contextPath}/customers" class="btn btn-secondary">
                <i class="bi bi-arrow-left"></i> Back to List
            </a>
        </div>
    </div>

    <div class="row">
        <!-- Customer Information -->
        <div class="col-md-8">
            <div class="card">
                <div class="card-header">
                    <h5 class="card-title mb-0">
                        <i class="bi bi-info-circle"></i> Personal Information
                    </h5>
                </div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-6">
                            <table class="table table-borderless">
                                <tr>
                                    <td><strong><i class="bi bi-hash"></i> Account Number:</strong></td>
                                    <td>${customer.accountNumber}</td>
                                </tr>
                                <tr>
                                    <td><strong><i class="bi bi-person"></i> Full Name:</strong></td>
                                    <td>${customer.fullName}</td>
                                </tr>
                                <tr>
                                    <td><strong><i class="bi bi-envelope"></i> Email:</strong></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty customer.email}">
                                                <a href="mailto:${customer.email}">${customer.email}</a>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">Not provided</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                                <tr>
                                    <td><strong><i class="bi bi-telephone"></i> Phone:</strong></td>
                                    <td>
                                        <a href="tel:${customer.phoneNumber}">${customer.phoneNumber}</a>
                                    </td>
                                </tr>
                            </table>
                        </div>
                        <div class="col-md-6">
                            <table class="table table-borderless">
                                <tr>
                                    <td><strong><i class="bi bi-geo-alt"></i> Address:</strong></td>
                                    <td>${customer.address}</td>
                                </tr>
                                <tr>
                                    <td><strong><i class="bi bi-building"></i> City:</strong></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty customer.city}">
                                                ${customer.city}
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">Not specified</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                                <tr>
                                    <td><strong><i class="bi bi-mailbox"></i> Postal Code:</strong></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty customer.postalCode}">
                                                ${customer.postalCode}
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">Not specified</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                                <tr>
                                    <td><strong><i class="bi bi-toggle-on"></i> Status:</strong></td>
                                    <td>
                                        <span class="badge bg-${customer.active ? 'success' : 'danger'} fs-6">
                                            <i class="bi bi-${customer.active ? 'check' : 'x'}-circle"></i>
                                            ${customer.active ? 'Active' : 'Inactive'}
                                        </span>
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Billing Information -->
            <div class="card mt-4">
                <div class="card-header">
                    <h5 class="card-title mb-0">
                        <i class="bi bi-graph-up"></i> Billing Information
                    </h5>
                </div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-3">
                            <div class="text-center">
                                <h3 class="text-primary">${customer.unitsConsumed}</h3>
                                <p class="text-muted mb-0">Units Consumed</p>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="text-center">
                                <h3 class="text-success">${totalBills != null ? totalBills : 0}</h3>
                                <p class="text-muted mb-0">Total Bills</p>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="text-center">
                                <h3 class="text-info">Rs. ${totalAmount != null ? totalAmount : '0.00'}</h3>
                                <p class="text-muted mb-0">Total Amount</p>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="text-center">
                                <h3 class="text-warning">Rs. ${averageBill != null ? averageBill : '0.00'}</h3>
                                <p class="text-muted mb-0">Avg. Bill</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Recent Bills -->
            <div class="card mt-4">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h5 class="card-title mb-0">
                        <i class="bi bi-receipt"></i> Recent Bills
                    </h5>
                    <a href="${pageContext.request.contextPath}/bills?customer=${customer.accountNumber}"
                       class="btn btn-sm btn-outline-primary">
                        View All Bills
                    </a>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty recentBills}">
                            <div class="table-responsive">
                                <table class="table table-hover">
                                    <thead>
                                    <tr>
                                        <th>Bill Number</th>
                                        <th>Date</th>
                                        <th>Amount</th>
                                        <th>Status</th>
                                        <th>Actions</th>
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
                                            <td>
                                                <fmt:formatDate value="${bill.billDate}" pattern="MMM dd, yyyy"/>
                                            </td>
                                            <td>Rs. ${bill.totalAmount}</td>
                                            <td>
                                                    <span class="badge bg-${bill.status == 'PAID' ? 'success' : bill.status == 'PENDING' ? 'warning' : 'danger'}">
                                                            ${bill.status}
                                                    </span>
                                            </td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/bills?action=view&id=${bill.billNumber}"
                                                   class="btn btn-sm btn-outline-info">
                                                    <i class="bi bi-eye"></i>
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="text-center py-4">
                                <i class="bi bi-receipt text-muted" style="font-size: 2rem;"></i>
                                <p class="text-muted mt-2 mb-3">No bills found for this customer</p>
                                <a href="${pageContext.request.contextPath}/bills?action=create&customerId=${customer.accountNumber}"
                                   class="btn btn-primary">
                                    <i class="bi bi-plus-circle"></i> Create First Bill
                                </a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- Side Panel -->
        <div class="col-md-4">
            <!-- Customer Photo/Avatar -->
            <div class="card">
                <div class="card-body text-center">
                    <i class="bi bi-person-circle text-primary" style="font-size: 4rem;"></i>
                    <h5 class="mt-2">${customer.fullName}</h5>
                    <p class="text-muted">${customer.accountNumber}</p>
                    <span class="badge bg-${customer.active ? 'success' : 'danger'} fs-6">
                        ${customer.active ? 'Active Account' : 'Inactive Account'}
                    </span>
                </div>
            </div>

            <!-- Quick Actions -->
            <div class="card mt-3">
                <div class="card-header">
                    <h5 class="card-title mb-0">
                        <i class="bi bi-lightning"></i> Quick Actions
                    </h5>
                </div>
                <div class="card-body">
                    <div class="d-grid gap-2">
                        <a href="${pageContext.request.contextPath}/bills?action=create&customerId=${customer.accountNumber}"
                           class="btn btn-primary">
                            <i class="bi bi-plus-circle"></i> Create New Bill
                        </a>
                        <a href="${pageContext.request.contextPath}/customers?action=edit&id=${customer.accountNumber}"
                           class="btn btn-warning">
                            <i class="bi bi-pencil"></i> Edit Information
                        </a>
                        <button class="btn btn-info" onclick="printCustomerInfo()">
                            <i class="bi bi-printer"></i> Print Details
                        </button>
                        <button class="btn btn-secondary" onclick="exportCustomerData()">
                            <i class="bi bi-download"></i> Export Data
                        </button>
                    </div>
                </div>
            </div>

            <!-- Account Information -->
            <div class="card mt-3">
                <div class="card-header">
                    <h5 class="card-title mb-0">
                        <i class="bi bi-info-square"></i> Account Information
                    </h5>
                </div>
                <div class="card-body">
                    <table class="table table-sm table-borderless">
                        <c:if test="${not empty customer.createdDate}">
                            <tr>
                                <td><strong>Created:</strong></td>
                                <td><fmt:formatDate value="${customer.createdDate}" pattern="MMM dd, yyyy"/></td>
                            </tr>
                        </c:if>
                        <c:if test="${not empty customer.lastModifiedDate}">
                            <tr>
                                <td><strong>Last Updated:</strong></td>
                                <td><fmt:formatDate value="${customer.lastModifiedDate}" pattern="MMM dd, yyyy"/></td>
                            </tr>
                        </c:if>
                        <c:if test="${not empty customer.lastBillDate}">
                            <tr>
                                <td><strong>Last Bill:</strong></td>
                                <td><fmt:formatDate value="${customer.lastBillDate}" pattern="MMM dd, yyyy"/></td>
                            </tr>
                        </c:if>
                        <tr>
                            <td><strong>Account Age:</strong></td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty customer.createdDate}">
                                        ${accountAge} days
                                    </c:when>
                                    <c:otherwise>
                                        Unknown
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>

            <!-- Contact Methods -->
            <div class="card mt-3">
                <div class="card-header">
                    <h5 class="card-title mb-0">
                        <i class="bi bi-chat-dots"></i> Contact Customer
                    </h5>
                </div>
                <div class="card-body">
                    <div class="d-grid gap-2">
                        <c:if test="${not empty customer.phoneNumber}">
                            <a href="tel:${customer.phoneNumber}" class="btn btn-outline-success btn-sm">
                                <i class="bi bi-telephone"></i> Call
                            </a>
                        </c:if>
                        <c:if test="${not empty customer.email}">
                            <a href="mailto:${customer.email}" class="btn btn-outline-primary btn-sm">
                                <i class="bi bi-envelope"></i> Email
                            </a>
                        </c:if>
                        <button class="btn btn-outline-warning btn-sm" onclick="sendSMS()">
                            <i class="bi bi-chat-text"></i> Send SMS
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />

<script>
    // Print customer information
    function printCustomerInfo() {
        const printContent = `
        <h2>Customer Information</h2>
        <table border="1" cellpadding="5">
            <tr><td><strong>Account Number:</strong></td><td>${customer.accountNumber}</td></tr>
            <tr><td><strong>Name:</strong></td><td>${customer.fullName}</td></tr>
            <tr><td><strong>Phone:</strong></td><td>${customer.phoneNumber}</td></tr>
            <tr><td><strong>Email:</strong></td><td>${customer.email}</td></tr>
            <tr><td><strong>Address:</strong></td><td>${customer.address}</td></tr>
            <tr><td><strong>City:</strong></td><td>${customer.city}</td></tr>
            <tr><td><strong>Postal Code:</strong></td><td>${customer.postalCode}</td></tr>
            <tr><td><strong>Units Consumed:</strong></td><td>${customer.unitsConsumed}</td></tr>
            <tr><td><strong>Status:</strong></td><td>${customer.active ? 'Active' : 'Inactive'}</td></tr>
        </table>
        <p><small>Generated on: ${new Date()}</small></p>
    `;

        const printWindow = window.open('', '_blank');
        printWindow.document.write(`
        <html>
            <head><title>Customer Details - ${customer.fullName}</title></head>
            <body>${printContent}</body>
        </html>
    `);
        printWindow.document.close();
        printWindow.print();
    }

    // Export customer data
    function exportCustomerData() {
        const data = [
            ['Account Number', '${customer.accountNumber}'],
            ['Full Name', '${customer.fullName}'],
            ['Email', '${customer.email}'],
            ['Phone', '${customer.phoneNumber}'],
            ['Address', '${customer.address}'],
            ['City', '${customer.city}'],
            ['Postal Code', '${customer.postalCode}'],
            ['Units Consumed', '${customer.unitsConsumed}'],
            ['Status', '${customer.active ? 'Active' : 'Inactive'}']
        ];

        let csvContent = "data:text/csv;charset=utf-8,";
        data.forEach(row => {
            csvContent += row.join(",") + "\r\n";
        });

        const encodedUri = encodeURI(csvContent);
        const link = document.createElement("a");
        link.setAttribute("href", encodedUri);
        link.setAttribute("download", "customer_${customer.accountNumber}.csv");
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }

    // Send SMS placeholder
    function sendSMS() {
        const phone = '${customer.phoneNumber}';
        const message = `Hello ${customer.fullName}, this is a message from Pahana Edu regarding your account ${customer.accountNumber}.`;

        if (phone) {
            // In a real application, this would integrate with an SMS service
            alert(`SMS would be sent to ${phone}\n\nMessage: ${message}`);
        } else {
            alert('No phone number available for this customer.');
        }
    }

    // Auto-refresh customer data every 30 seconds (optional)
    // setInterval(function() {
    //     location.reload();
    // }, 30000);
</script>