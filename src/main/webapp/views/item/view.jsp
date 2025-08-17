<%--
  Created by IntelliJ IDEA.
  User: MOHAMED
  Date: 16/08/2025
  Time: 12:45
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Item Details" />
<jsp:include page="../common/header.jsp" />

<!-- Page Content -->
<div class="col-md-10 main-content p-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="h3">
            <i class="bi bi-box-seam"></i> Item Details
        </h1>
        <div>
            <a href="${pageContext.request.contextPath}/items?action=edit&id=${item.itemId}" class="btn btn-warning me-2">
                <i class="bi bi-pencil"></i> Edit Item
            </a>
            <a href="${pageContext.request.contextPath}/items" class="btn btn-secondary">
                <i class="bi bi-arrow-left"></i> Back to List
            </a>
        </div>
    </div>

    <div class="row">
        <!-- Item Information -->
        <div class="col-md-8">
            <div class="card">
                <div class="card-header">
                    <h5 class="card-title mb-0">
                        <i class="bi bi-info-circle"></i> Item Information
                    </h5>
                </div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-6">
                            <table class="table table-borderless">
                                <tr>
                                    <td><strong><i class="bi bi-hash"></i> Item ID:</strong></td>
                                    <td>${item.itemId}</td>
                                </tr>
                                <tr>
                                    <td><strong><i class="bi bi-tag"></i> Name:</strong></td>
                                    <td>${item.name}</td>
                                </tr>
                                <tr>
                                    <td><strong><i class="bi bi-collection"></i> Category:</strong></td>
                                    <td>
                                        <span class="badge bg-secondary fs-6">${item.category}</span>
                                    </td>
                                </tr>
                                <tr>
                                    <td><strong><i class="bi bi-currency-dollar"></i> Price:</strong></td>
                                    <td>
                                        <strong class="text-success">Rs. <fmt:formatNumber value="${item.price}" pattern="#,##0.00"/></strong>
                                    </td>
                                </tr>
                            </table>
                        </div>
                        <div class="col-md-6">
                            <table class="table table-borderless">
                                <tr>
                                    <td><strong><i class="bi bi-boxes"></i> Stock Quantity:</strong></td>
                                    <td>
                                        <span class="badge bg-${item.stockQuantity > 10 ? 'success' : item.stockQuantity > 0 ? 'warning' : 'danger'} fs-6">
                                            ${item.stockQuantity} units
                                        </span>
                                    </td>
                                </tr>
                                <tr>
                                    <td><strong><i class="bi bi-exclamation-triangle"></i> Min Stock:</strong></td>
                                    <td>${item.minStockLevel} units</td>
                                </tr>
                                <tr>
                                    <td><strong><i class="bi bi-building"></i> Supplier:</strong></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty item.supplier}">
                                                ${item.supplier}
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
                                        <span class="badge bg-${item.active ? 'success' : 'danger'} fs-6">
                                            <i class="bi bi-${item.active ? 'check' : 'x'}-circle"></i>
                                            ${item.active ? 'Active' : 'Inactive'}
                                        </span>
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </div>

                    <!-- Description -->
                    <c:if test="${not empty item.description}">
                        <div class="row mt-3">
                            <div class="col-12">
                                <h6><i class="bi bi-card-text"></i> Description</h6>
                                <p class="text-muted">${item.description}</p>
                            </div>
                        </div>
                    </c:if>
                </div>
            </div>

            <!-- Sales Information -->
            <div class="card mt-4">
                <div class="card-header">
                    <h5 class="card-title mb-0">
                        <i class="bi bi-graph-up"></i> Sales Information
                    </h5>
                </div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-3">
                            <div class="text-center">
                                <h3 class="text-primary">${totalSold != null ? totalSold : 0}</h3>
                                <p class="text-muted mb-0">Units Sold</p>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="text-center">
                                <h3 class="text-success">Rs. ${totalRevenue != null ? totalRevenue : '0.00'}</h3>
                                <p class="text-muted mb-0">Total Revenue</p>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="text-center">
                                <h3 class="text-info">${totalOrders != null ? totalOrders : 0}</h3>
                                <p class="text-muted mb-0">Times Ordered</p>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="text-center">
                                <h3 class="text-warning">Rs. ${avgOrderValue != null ? avgOrderValue : '0.00'}</h3>
                                <p class="text-muted mb-0">Avg. Order Value</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Recent Sales -->
            <div class="card mt-4">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h5 class="card-title mb-0">
                        <i class="bi bi-receipt"></i> Recent Sales
                    </h5>
                    <a href="${pageContext.request.contextPath}/bills?item=${item.itemId}"
                       class="btn btn-sm btn-outline-primary">
                        View All Sales
                    </a>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty recentSales}">
                            <div class="table-responsive">
                                <table class="table table-hover">
                                    <thead>
                                    <tr>
                                        <th>Bill Number</th>
                                        <th>Customer</th>
                                        <th>Quantity</th>
                                        <th>Amount</th>
                                        <th>Date</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach items="${recentSales}" var="sale" end="4">
                                        <tr>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/bills?action=view&id=${sale.billNumber}">
                                                        ${sale.billNumber}
                                                </a>
                                            </td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/customers?action=view&id=${sale.customerAccountNumber}">
                                                        ${sale.customerName}
                                                </a>
                                            </td>
                                            <td>
                                                <span class="badge bg-info">${sale.quantity}</span>
                                            </td>
                                            <td>Rs. ${sale.lineTotal}</td>
                                            <td>
                                                <fmt:formatDate value="${sale.saleDate}" pattern="MMM dd, yyyy"/>
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
                                <p class="text-muted mt-2 mb-3">No sales recorded for this item yet</p>
                                <a href="${pageContext.request.contextPath}/bills?action=create&itemId=${item.itemId}"
                                   class="btn btn-primary">
                                    <i class="bi bi-plus-circle"></i> Create First Sale
                                </a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- Side Panel -->
        <div class="col-md-4">
            <!-- Item Display -->
            <div class="card">
                <div class="card-body text-center">
                    <i class="bi bi-box-seam text-primary" style="font-size: 4rem;"></i>
                    <h5 class="mt-2">${item.name}</h5>
                    <p class="text-muted">${item.itemId}</p>
                    <span class="badge bg-${item.active ? 'success' : 'danger'} fs-6">
                        ${item.active ? 'Available' : 'Unavailable'}
                    </span>

                    <!-- Stock Status -->
                    <div class="mt-3">
                        <c:choose>
                            <c:when test="${item.stockQuantity <= 0}">
                                <div class="alert alert-danger">
                                    <i class="bi bi-exclamation-triangle"></i>
                                    <strong>Out of Stock</strong>
                                </div>
                            </c:when>
                            <c:when test="${item.stockQuantity <= item.minStockLevel}">
                                <div class="alert alert-warning">
                                    <i class="bi bi-exclamation-triangle"></i>
                                    <strong>Low Stock Warning</strong>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="alert alert-success">
                                    <i class="bi bi-check-circle"></i>
                                    <strong>In Stock</strong>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
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
                        <a href="${pageContext.request.contextPath}/bills?action=create&itemId=${item.itemId}"
                           class="btn btn-primary">
                            <i class="bi bi-cart-plus"></i> Add to New Bill
                        </a>
                        <a href="${pageContext.request.contextPath}/items?action=edit&id=${item.itemId}"
                           class="btn btn-warning">
                            <i class="bi bi-pencil"></i> Edit Item
                        </a>
                        <button class="btn btn-info" onclick="printItemInfo()">
                            <i class="bi bi-printer"></i> Print Details
                        </button>
                        <button class="btn btn-success" onclick="updateStock()">
                            <i class="bi bi-boxes"></i> Update Stock
                        </button>
                    </div>
                </div>
            </div>

            <!-- Item Statistics -->
            <div class="card mt-3">
                <div class="card-header">
                    <h5 class="card-title mb-0">
                        <i class="bi bi-bar-chart"></i> Item Statistics
                    </h5>
                </div>
                <div class="card-body">
                    <table class="table table-sm table-borderless">
                        <tr>
                            <td><strong>Current Value:</strong></td>
                            <td>Rs. <fmt:formatNumber value="${item.price * item.stockQuantity}" pattern="#,##0.00"/></td>
                        </tr>
                        <tr>
                            <td><strong>Turnover Rate:</strong></td>
                            <td>
                                <c:choose>
                                    <c:when test="${turnoverRate != null}">
                                        ${turnoverRate}%
                                    </c:when>
                                    <c:otherwise>
                                        N/A
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                        <tr>
                            <td><strong>Days in Stock:</strong></td>
                            <td>
                                <c:choose>
                                    <c:when test="${daysInStock != null}">
                                        ${daysInStock} days
                                    </c:when>
                                    <c:otherwise>
                                        N/A
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                        <tr>
                            <td><strong>Last Sale:</strong></td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty lastSaleDate}">
                                        <fmt:formatDate value="${lastSaleDate}" pattern="MMM dd"/>
                                    </c:when>
                                    <c:otherwise>
                                        Never
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>

            <!-- Item History -->
            <div class="card mt-3">
                <div class="card-header">
                    <h5 class="card-title mb-0">
                        <i class="bi bi-clock-history"></i> Item History
                    </h5>
                </div>
                <div class="card-body">
                    <table class="table table-sm table-borderless">
                        <c:if test="${not empty item.createdDate}">
                            <tr>
                                <td><strong>Added:</strong></td>
                                <td><fmt:formatDate value="${item.createdDate}" pattern="MMM dd, yyyy"/></td>
                            </tr>
                        </c:if>
                        <c:if test="${not empty item.lastModifiedDate}">
                            <tr>
                                <td><strong>Last Modified:</strong></td>
                                <td><fmt:formatDate value="${item.lastModifiedDate}" pattern="MMM dd, yyyy"/></td>
                            </tr>
                        </c:if>
                        <c:if test="${not empty item.lastModifiedBy}">
                            <tr>
                                <td><strong>Modified By:</strong></td>
                                <td>${item.lastModifiedBy}</td>
                            </tr>
                        </c:if>
                        <tr>
                            <td><strong>Item Age:</strong></td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty item.createdDate}">
                                        ${itemAge} days
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

            <!-- Related Items -->
            <c:if test="${not empty relatedItems}">
                <div class="card mt-3">
                    <div class="card-header">
                        <h5 class="card-title mb-0">
                            <i class="bi bi-collection"></i> Related Items
                        </h5>
                    </div>
                    <div class="card-body">
                        <c:forEach items="${relatedItems}" var="relatedItem" end="3">
                            <div class="d-flex justify-content-between align-items-center mb-2">
                                <div>
                                    <small><strong>${relatedItem.name}</strong></small><br>
                                    <small class="text-muted">Rs. ${relatedItem.price}</small>
                                </div>
                                <a href="${pageContext.request.contextPath}/items?action=view&id=${relatedItem.itemId}"
                                   class="btn btn-sm btn-outline-primary">
                                    View
                                </a>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </c:if>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />

<script>
    // Print item information
    function printItemInfo() {
        const printContent = `
        <h2>Item Information</h2>
        <table border="1" cellpadding="5">
            <tr><td><strong>Item ID:</strong></td><td>${item.itemId}</td></tr>
            <tr><td><strong>Name:</strong></td><td>${item.name}</td></tr>
            <tr><td><strong>Category:</strong></td><td>${item.category}</td></tr>
            <tr><td><strong>Price:</strong></td><td>Rs. ${item.price}</td></tr>
            <tr><td><strong>Stock Quantity:</strong></td><td>${item.stockQuantity} units</td></tr>
            <tr><td><strong>Min Stock Level:</strong></td><td>${item.minStockLevel} units</td></tr>
            <tr><td><strong>Supplier:</strong></td><td>${item.supplier}</td></tr>
            <tr><td><strong>Status:</strong></td><td>${item.active ? 'Active' : 'Inactive'}</td></tr>
            <tr><td><strong>Description:</strong></td><td>${item.description}</td></tr>
        </table>
        <p><small>Generated on: ${new Date()}</small></p>
    `;

        const printWindow = window.open('', '_blank');
        printWindow.document.write(`
        <html>
            <head><title>Item Details - ${item.name}</title></head>
            <body>${printContent}</body>
        </html>
    `);
        printWindow.document.close();
        printWindow.print();
    }

    // Update stock function
    function updateStock() {
        const newStock = prompt('Enter new stock quantity:', '${item.stockQuantity}');
        if (newStock !== null && !isNaN(newStock) && newStock >= 0) {
            if (confirm(`Update stock from ${item.stockQuantity} to ${newStock} units?`)) {
                // In real application, this would make an AJAX call to update stock
                window.location.href = '${pageContext.request.contextPath}/items?action=updateStock&id=${item.itemId}&stock=' + newStock;
            }
        } else if (newStock !== null) {
            alert('Please enter a valid stock quantity (0 or greater).');
        }
    }

    // Export item data
    function exportItemData() {
        const data = [
            ['Item ID', '${item.itemId}'],
            ['Name', '${item.name}'],
            ['Category', '${item.category}'],
            ['Price', '${item.price}'],
            ['Stock', '${item.stockQuantity}'],
            ['Min Stock', '${item.minStockLevel}'],
            ['Supplier', '${item.supplier}'],
            ['Status', '${item.active ? 'Active' : 'Inactive'}'],
            ['Description', '${item.description}']
        ];

        let csvContent = "data:text/csv;charset=utf-8,";
        data.forEach(row => {
            csvContent += row.join(",") + "\r\n";
        });

        const encodedUri = encodeURI(csvContent);
        const link = document.createElement("a");
        link.setAttribute("href", encodedUri);
        link.setAttribute("download", "item_${item.itemId}.csv");
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }
</script>