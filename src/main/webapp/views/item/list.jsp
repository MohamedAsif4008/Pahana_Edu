<%--
  Created by IntelliJ IDEA.
  User: MOHAMED
  Date: 16/08/2025
  Time: 12:30
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Items" />
<jsp:include page="../common/header.jsp" />

<!-- Page Content -->
<div class="col-md-10 main-content p-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="h3">
            <i class="bi bi-box-seam"></i> Manage Items
        </h1>
        <div>
            <a href="${pageContext.request.contextPath}/items?action=create" class="btn btn-primary">
                <i class="bi bi-plus-circle"></i> Add New Item
            </a>
        </div>
    </div>

    <!-- Search Bar -->
    <div class="row mb-4">
        <div class="col-md-6">
            <form action="${pageContext.request.contextPath}/items" method="get" class="d-flex">
                <input type="hidden" name="action" value="search">
                <input type="text"
                       class="form-control me-2"
                       name="searchTerm"
                       value="${param.searchTerm}"
                       placeholder="Search by name, ID, or category...">
                <button type="submit" class="btn btn-outline-primary">
                    <i class="bi bi-search"></i> Search
                </button>
            </form>
        </div>
        <div class="col-md-6 text-end">
            <small class="text-muted">
                Total Items: <strong>${totalItems != null ? totalItems : 0}</strong> |
                In Stock: <strong>${inStockItems != null ? inStockItems : 0}</strong>
            </small>
        </div>
    </div>

    <!-- Items Table -->
    <div class="card">
        <div class="card-header">
            <h5 class="card-title mb-0">
                <i class="bi bi-list"></i> Inventory List
                <c:if test="${not empty param.searchTerm}">
                    - Search Results for "${param.searchTerm}"
                </c:if>
            </h5>
        </div>
        <div class="card-body">
            <c:choose>
                <c:when test="${not empty items}">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead class="table-light">
                            <tr>
                                <th>Item ID</th>
                                <th>Name</th>
                                <th>Category</th>
                                <th>Price</th>
                                <th>Stock</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${items}" var="item">
                                <tr>
                                    <td>
                                        <strong>${item.itemId}</strong>
                                    </td>
                                    <td>
                                        <div>
                                            <strong>${item.itemName}</strong>
                                            <c:if test="${not empty item.description}">
                                                <br><small class="text-muted">${item.description}</small>
                                            </c:if>
                                        </div>
                                    </td>
                                    <td>
                                        <span class="badge bg-secondary">${item.category}</span>
                                    </td>
                                    <td>
                                        <strong>Rs. <fmt:formatNumber value="${item.price}" pattern="#,##0.00"/></strong>
                                    </td>
                                    <td>
                                            <span class="badge bg-${item.stockQuantity > 10 ? 'success' : item.stockQuantity > 0 ? 'warning' : 'danger'}">
                                                ${item.stockQuantity} units
                                            </span>
                                    </td>
                                    <td>
                                            <span class="badge bg-${item.active ? 'success' : 'danger'}">
                                                    ${item.active ? 'Active' : 'Inactive'}
                                            </span>
                                    </td>
                                    <td>
                                        <div class="btn-group btn-group-sm">
                                            <a href="${pageContext.request.contextPath}/items?action=view&id=${item.itemId}"
                                               class="btn btn-outline-info" title="View Details">
                                                <i class="bi bi-eye"></i>
                                            </a>
                                            <a href="${pageContext.request.contextPath}/items?action=edit&id=${item.itemId}"
                                               class="btn btn-outline-warning" title="Edit">
                                                <i class="bi bi-pencil"></i>
                                            </a>
                                            <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                                                <button type="button"
                                                        class="btn btn-outline-danger"
                                                        title="Delete"
                                                        onclick="confirmDelete('${item.itemId}', '${item.itemName}')">
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
                        <i class="bi bi-box text-muted" style="font-size: 3rem;"></i>
                        <h5 class="text-muted mt-3">
                            <c:choose>
                                <c:when test="${not empty param.searchTerm}">
                                    No items found matching "${param.searchTerm}"
                                </c:when>
                                <c:otherwise>
                                    No items in inventory yet
                                </c:otherwise>
                            </c:choose>
                        </h5>
                        <p class="text-muted">
                            <c:choose>
                                <c:when test="${not empty param.searchTerm}">
                                    Try a different search term or
                                    <a href="${pageContext.request.contextPath}/items">view all items</a>
                                </c:when>
                                <c:otherwise>
                                    Start by adding your first item to the inventory
                                </c:otherwise>
                            </c:choose>
                        </p>
                        <a href="${pageContext.request.contextPath}/items?action=create" class="btn btn-primary">
                            <i class="bi bi-plus-circle"></i> Add First Item
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <!-- Quick Stats -->
    <div class="row mt-4">
        <div class="col-md-3">
            <div class="card bg-primary text-white">
                <div class="card-body text-center">
                    <i class="bi bi-box-seam" style="font-size: 2rem;"></i>
                    <h4 class="mt-2">${totalItems != null ? totalItems : 0}</h4>
                    <p class="mb-0">Total Items</p>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card bg-success text-white">
                <div class="card-body text-center">
                    <i class="bi bi-check-circle" style="font-size: 2rem;"></i>
                    <h4 class="mt-2">${inStockItems != null ? inStockItems : 0}</h4>
                    <p class="mb-0">In Stock</p>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card bg-warning text-white">
                <div class="card-body text-center">
                    <i class="bi bi-exclamation-triangle" style="font-size: 2rem;"></i>
                    <h4 class="mt-2">${lowStockItems != null ? lowStockItems : 0}</h4>
                    <p class="mb-0">Low Stock</p>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card bg-info text-white">
                <div class="card-body text-center">
                    <i class="bi bi-currency-dollar" style="font-size: 2rem;"></i>
                    <h4 class="mt-2">Rs. ${totalValue != null ? totalValue : '0'}</h4>
                    <p class="mb-0">Total Value</p>
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
                    <h6 class="mt-2">Add Item</h6>
                    <a href="${pageContext.request.contextPath}/items?action=create" class="btn btn-sm btn-primary">
                        Create New
                    </a>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card">
                <div class="card-body text-center">
                    <i class="bi bi-upload text-success" style="font-size: 2rem;"></i>
                    <h6 class="mt-2">Import Items</h6>
                    <button class="btn btn-sm btn-success" onclick="importItems()">
                        Upload CSV
                    </button>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card">
                <div class="card-body text-center">
                    <i class="bi bi-download text-info" style="font-size: 2rem;"></i>
                    <h6 class="mt-2">Export Items</h6>
                    <button class="btn btn-sm btn-info" onclick="exportItems()">
                        Download CSV
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />

<script>
    // Delete confirmation
    function confirmDelete(itemId, itemName) {
        if (confirm(`Are you sure you want to delete item "${itemName}" (${itemId})?\n\nThis action cannot be undone.`)) {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '${pageContext.request.contextPath}/items';

            const actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            actionInput.value = 'delete';

            const idInput = document.createElement('input');
            idInput.type = 'hidden';
            idInput.name = 'id';
            idInput.value = itemId;

            form.appendChild(actionInput);
            form.appendChild(idInput);
            document.body.appendChild(form);
            form.submit();
        }
    }

    // Export items function
    function exportItems() {
        window.open('${pageContext.request.contextPath}/items?action=export&format=csv', '_blank');
    }

    // Import items function
    function importItems() {
        // Create file input
        const fileInput = document.createElement('input');
        fileInput.type = 'file';
        fileInput.accept = '.csv';
        fileInput.onchange = function(e) {
            const file = e.target.files[0];
            if (file) {
                if (confirm('Import items from ' + file.name + '?')) {
                    // In real application, this would upload and process the file
                    alert('Import functionality would be implemented here to process CSV file.');
                }
            }
        };
        fileInput.click();
    }

    // Search functionality enhancement
    document.addEventListener('DOMContentLoaded', function() {
        const searchInput = document.querySelector('input[name="searchTerm"]');
        if (searchInput) {
            searchInput.focus();
        }
    });
</script>