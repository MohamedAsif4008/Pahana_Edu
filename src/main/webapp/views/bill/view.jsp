<%--
  Created by IntelliJ IDEA.
  User: MOHAMED
  Date: 16/08/2025
  Time: 13:20
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Bill Details" />
<jsp:include page="../common/header.jsp" />

<!-- Page Content -->
<div class="col-md-10 main-content p-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="h3">
            <i class="bi bi-receipt"></i> Bill Details
        </h1>
        <div>
            <button type="button" class="btn btn-success me-2" onclick="printBill()">
                <i class="bi bi-printer"></i> Print Bill
            </button>
            <a href="${pageContext.request.contextPath}/bills" class="btn btn-secondary">
                <i class="bi bi-arrow-left"></i> Back to List
            </a>
        </div>
    </div>

    <!-- Bill Information -->
    <div class="row">
        <div class="col-md-8">
            <!-- Bill Header -->
            <div class="card" id="billToPrint">
                <div class="card-header bg-primary text-white">
                    <div class="row">
                        <div class="col-md-6">
                            <h4 class="mb-0">
                                <i class="bi bi-shop"></i> Pahana Edu
                            </h4>
                            <p class="mb-0">Billing System</p>
                        </div>
                        <div class="col-md-6 text-end">
                            <h5 class="mb-0">INVOICE</h5>
                            <p class="mb-0">Bill #: ${bill.billNumber}</p>
                        </div>
                    </div>
                </div>
                <div class="card-body">
                    <!-- Company & Customer Info -->
                    <div class="row mb-4">
                        <div class="col-md-6">
                            <h6 class="text-primary">From:</h6>
                            <address>
                                <strong>Pahana Edu Bookshop</strong><br>
                                123 Colombo Street<br>
                                Colombo 03, Sri Lanka<br>
                                <abbr title="Phone">P:</abbr> +94 11 234 5678<br>
                                <abbr title="Email">E:</abbr> info@pahanaedu.lk
                            </address>
                        </div>
                        <div class="col-md-6">
                            <h6 class="text-primary">To:</h6>
                            <address>
                                <strong>${bill.customer.name}</strong><br>
                                Account: ${bill.customer.accountNumber}<br>
                                ${bill.customer.address}<br>
                                <abbr title="Phone">P:</abbr> ${bill.customer.phoneNumber}<br>
                                <c:if test="${not empty bill.customer.email}">
                                    <abbr title="Email">E:</abbr> ${bill.customer.email}
                                </c:if>
                            </address>
                        </div>
                    </div>

                    <!-- Bill Details -->
                    <div class="row mb-4">
                        <div class="col-md-6">
                            <table class="table table-borderless table-sm">
                                <tr>
                                    <td><strong>Bill Number:</strong></td>
                                    <td>${bill.billNumber}</td>
                                </tr>
                                <tr>
                                    <td><strong>Bill Date:</strong></td>
                                    <td><fmt:formatDate value="${bill.billDate}" pattern="MMM dd, yyyy"/></td>
                                </tr>
                                <tr>
                                    <td><strong>Payment Method:</strong></td>
                                    <td>
                                        <span class="badge bg-${bill.paymentMethod == 'CASH' ? 'success' : 'primary'}">
                                            ${bill.paymentMethod}
                                        </span>
                                    </td>
                                </tr>
                            </table>
                        </div>
                        <div class="col-md-6">
                            <table class="table table-borderless table-sm">
                                <tr>
                                    <td><strong>Status:</strong></td>
                                    <td>
                                        <span class="badge bg-${bill.status == 'PAID' ? 'success' : bill.status == 'PENDING' ? 'warning' : 'danger'}">
                                            ${bill.status}
                                        </span>
                                    </td>
                                </tr>
                                
                                <c:if test="${not empty bill.notes}">
                                    <tr>
                                        <td><strong>Notes:</strong></td>
                                        <td>${bill.notes}</td>
                                    </tr>
                                </c:if>
                            </table>
                        </div>
                    </div>

                    <!-- Bill Items Table -->
                    <div class="table-responsive">
                        <table class="table table-striped">
                            <thead class="table-dark">
                            <tr>
                                <th>#</th>
                                <th>Item</th>
                                <th>Description</th>
                                <th class="text-center">Qty</th>
                                <th class="text-end">Unit Price</th>
                                <th class="text-end">Total</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${bill.billItems}" var="item" varStatus="status">
                                <tr>
                                    <td>${status.index + 1}</td>
                                    <td>
                                        <strong>${item.item.itemId}</strong><br>
                                        <small class="text-muted">${item.item.name}</small>
                                    </td>
                                    <td>
                                        <small>${item.item.description}</small>
                                    </td>
                                    <td class="text-center">
                                        <span class="badge bg-info">${item.quantity}</span>
                                    </td>
                                    <td class="text-end">
                                        Rs. <fmt:formatNumber value="${item.unitPrice}" pattern="#,##0.00"/>
                                    </td>
                                    <td class="text-end">
                                        <strong>Rs. <fmt:formatNumber value="${item.lineTotal}" pattern="#,##0.00"/></strong>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <!-- Bill Totals -->
                    <div class="row">
                        <div class="col-md-6">
                            <!-- Payment Information -->
                            <div class="alert alert-info">
                                <h6 class="alert-heading">
                                    <i class="bi bi-credit-card"></i> Payment Information
                                </h6>
                                <p class="mb-1"><strong>Method:</strong> ${bill.paymentMethod}</p>
                                <p class="mb-1"><strong>Status:</strong> ${bill.status}</p>
                                <p class="mb-0"><strong>Date:</strong> <fmt:formatDate value="${bill.billDate}" pattern="MMM dd, yyyy HH:mm"/></p>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <table class="table table-borderless">
                                <tr>
                                    <td><strong>Subtotal:</strong></td>
                                    <td class="text-end">Rs. <fmt:formatNumber value="${bill.subtotal}" pattern="#,##0.00"/></td>
                                </tr>
                                <tr>
                                    <td>Tax (0%):</td>
                                    <td class="text-end">Rs. <fmt:formatNumber value="${bill.taxAmount}" pattern="#,##0.00"/></td>
                                </tr>
                                <c:if test="${bill.discountAmount > 0}">
                                    <tr>
                                        <td>Discount:</td>
                                        <td class="text-end text-success">- Rs. <fmt:formatNumber value="${bill.discountAmount}" pattern="#,##0.00"/></td>
                                    </tr>
                                </c:if>
                                <tr class="table-primary">
                                    <td><h5><strong>Total Amount:</strong></h5></td>
                                    <td class="text-end"><h5><strong>Rs. <fmt:formatNumber value="${bill.totalAmount}" pattern="#,##0.00"/></strong></h5></td>
                                </tr>
                            </table>
                        </div>
                    </div>

                    <!-- Footer -->
                    <div class="row mt-4">
                        <div class="col-12">
                            <hr>
                            <div class="text-center">
                                <p class="mb-1"><strong>Thank you for your business!</strong></p>
                                <p class="text-muted small mb-0">
                                    Generated on <fmt:formatDate value="${bill.billDate}" pattern="MMM dd, yyyy HH:mm"/> 
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Side Panel -->
        <div class="col-md-4">
            

            <!-- Bill Summary -->
            <div class="card mt-3">
                <div class="card-header">
                    <h5 class="card-title mb-0">
                        <i class="bi bi-info-square"></i> Bill Summary
                    </h5>
                </div>
                <div class="card-body">
                    <table class="table table-sm table-borderless">
                        <tr>
                            <td><strong>Items Count:</strong></td>
                            <td>${bill.billItems.size()}</td>
                        </tr>
                        <tr>
                            <td><strong>Total Quantity:</strong></td>
                            <td>
                                <c:set var="totalQty" value="0"/>
                                <c:forEach items="${bill.billItems}" var="item">
                                    <c:set var="totalQty" value="${totalQty + item.quantity}"/>
                                </c:forEach>
                                ${totalQty} units
                            </td>
                        </tr>
                        <tr>
                            <td><strong>Average Item Price:</strong></td>
                            <td>
                                <c:choose>
                                    <c:when test="${totalQty > 0}">
                                        Rs. <fmt:formatNumber value="${bill.subtotal / totalQty}" pattern="#,##0.00"/>
                                    </c:when>
                                    <c:otherwise>
                                        Rs. 0.00
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                        <tr>
                            <td><strong>Bill Age:</strong></td>
                            <td>
                                <jsp:useBean id="now" class="java.util.Date" />
                                <c:set var="diffInMillies" value="${now.time - bill.billDate.time}" />
                                <c:set var="diffInDays" value="${diffInMillies / (1000 * 60 * 60 * 24)}" />
                                <fmt:formatNumber value="${diffInDays}" maxFractionDigits="0" /> days
                            </td>
                        </tr>
                    </table>
                </div>
            </div>


        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />

<script>
    // Print bill function
    function printBill() {
        const printContent = document.getElementById('billToPrint').innerHTML;
        const originalContent = document.body.innerHTML;

        // Create print-friendly styles
        const printStyles = `
        <style>
            body { font-family: Arial, sans-serif; margin: 20px; }
            .card { border: 1px solid #ddd; border-radius: 5px; }
            .card-header { background: #f8f9fa; padding: 15px; border-bottom: 1px solid #ddd; }
            .card-body { padding: 20px; }
            .table { width: 100%; border-collapse: collapse; margin: 10px 0; }
            .table th, .table td { padding: 8px; border: 1px solid #ddd; text-align: left; }
            .table-dark th { background: #343a40; color: white; }
            .table-striped tr:nth-child(even) { background: #f2f2f2; }
            .text-end { text-align: right; }
            .text-center { text-align: center; }
            .badge { padding: 3px 6px; border-radius: 3px; font-size: 12px; }
            .bg-success { background: #28a745; color: white; }
            .bg-primary { background: #007bff; color: white; }
            .bg-info { background: #17a2b8; color: white; }
            .alert { padding: 10px; border-radius: 5px; margin: 10px 0; }
            .alert-info { background: #d1ecf1; border: 1px solid #bee5eb; }
            @media print {
                .btn, .no-print { display: none; }
                body { margin: 0; }
            }
        </style>
    `;

        document.body.innerHTML = printStyles + '<div>' + printContent + '</div>';
        window.print();
        document.body.innerHTML = originalContent;
        window.location.reload(); // Refresh to restore original content
    }

    // Download PDF function
    function downloadPDF() {
        alert('PDF download functionality would be implemented here using a PDF library like jsPDF or server-side PDF generation.');
    }

    // Email bill function
    function emailBill() {
        const customerEmail = '${bill.customer.email}';
        if (customerEmail) {
            if (confirm(`Send bill ${bill.billNumber} to ${customerEmail}?`)) {
                // In real application, this would make an AJAX call to send email
                alert('Bill email sent successfully to ' + customerEmail);
            }
        } else {
            alert('Customer email not available. Please update customer information first.');
        }
    }

    // Duplicate bill function
    function duplicateBill() {
        if (confirm('Create a new bill based on this one?')) {
            window.location.href = '${pageContext.request.contextPath}/bills?action=create&duplicateFrom=${bill.billNumber}';
        }
    }

    // Create new bill for same customer
    function createNewBillForCustomer() {
        window.location.href = '${pageContext.request.contextPath}/bills?action=create&customerId=${bill.customer.accountNumber}';
    }

    // Add to favorites (placeholder)
    function addToFavorites() {
        alert('Bill added to favorites for quick access.');
    }
</script>