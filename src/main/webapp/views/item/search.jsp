<%--
  Created by IntelliJ IDEA.
  User: MOHAMED
  Date: 16/08/2025
  Time: 12:50
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Search Items" />
<jsp:include page="../common/header.jsp" />

<!-- Page Content -->
<div class="col-md-10 main-content p-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="h3">
            <i class="bi bi-search"></i> Search Items
        </h1>
        <div>
            <a href="${pageContext.request.contextPath}/items?action=create" class="btn btn-primary me-2">
                <i class="bi bi-plus-circle"></i> Add Item
            </a>
            <a href="${pageContext.request.contextPath}/items" class="btn btn-secondary">
                <i class="bi bi-list"></i> View All
            </a>
        </div>
    </div>

    <!-- Advanced Search Form -->
    <div class="card mb-4">
        <div class="card-header">
            <h5 class="card-title mb-0">
                <i class="bi bi-funnel"></i> Search Filters
            </h5>
        </div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/items" method="get" id="searchForm">
                <input type="hidden" name="action" value="search">

                <div class="row">
                    <!-- Basic Search -->
                    <div class="col-md-6 mb-3">
                        <label for="searchTerm" class="form-label">
                            <i class="bi bi-search"></i> Search Term
                        </label>
                        <input type="text"
                               class="form-control"
                               id="searchTerm"
                               name="searchTerm"
                               value="${param.searchTerm}"
                               placeholder="Item name, ID, or description">
                    </div>

                    <!-- Category Filter -->
                    <div class="col-md-3 mb-3">
                        <label for="category" class="form-label">
                            <i class="bi bi-collection"></i> Category
                        </label>
                        <select class="form-select" id="category" name="category">
                            <option value="">All Categories</option>
                            <option value="Books" ${param.category == 'Books' ? 'selected' : ''}>Books</option>
                            <option value="Stationery" ${param.category == 'Stationery' ? 'selected' : ''}>Stationery</option>
                            <option value="Electronics" ${param.category == 'Electronics' ? 'selected' : ''}>Electronics</option>
                            <option value="Art Supplies" ${param.category == 'Art Supplies' ? 'selected' : ''}>Art Supplies</option>
                            <option value="Educational" ${param.category == 'Educational' ? 'selected' : ''}>Educational</option>
                            <option value="Other" ${param.category == 'Other' ? 'selected' : ''}>Other</option>
                        </select>
                    </div>

                    <!-- Status Filter -->
                    <div class="col-md-3 mb-3">
                        <label for="status" class="form-label">
                            <i class="bi bi-toggle-on"></i> Status
                        </label>
                        <select class="form-select" id="status" name="status">
                            <option value="">All Statuses</option>
                            <option value="active" ${param.status == 'active' ? 'selected' : ''}>Active</option>
                            <option value="inactive" ${param.status == 'inactive' ? 'selected' : ''}>Inactive</option>
                        </select>
                    </div>

                    <!-- Price Range -->
                    <div class="col-md-6 mb-3">
                        <label class="form-label">
                            <i class="bi bi-currency-dollar"></i> Price Range (Rs.)
                        </label>
                        <div class="row">
                            <div class="col-6">
                                <input type="number"
                                       class="form-control"
                                       name="minPrice"
                                       value="${param.minPrice}"
                                       placeholder="Min price"
                                       min="0"
                                       step="0.01">
                            </div>
                            <div class="col-6">
                                <input type="number"
                                       class="form-control"
                                       name="maxPrice"
                                       value="${param.maxPrice}"
                                       placeholder="Max price"
                                       min="0"
                                       step="0.01">
                            </div>
                        </div>
                    </div>

                    <!-- Stock Level -->
                    <div class="col-md-6 mb-3">
                        <label for="stockLevel" class="form-label">
                            <i class="bi bi-boxes"></i> Stock Level
                        </label>
                        <select class="form-select" id="stockLevel" name="stockLevel">
                            <option value="">Any Stock Level</option>
                            <option value="in-stock" ${param.stockLevel == 'in-stock' ? 'selected' : ''}>In Stock (> 0)</option>
                            <option value="low-stock" ${param.stockLevel == 'low-stock' ? 'selected' : ''}>Low Stock (≤ Min Level)</option>
                            <option value="out-of-stock" ${param.stockLevel == 'out-of-stock' ? 'selected' : ''}>Out of Stock (0)</option>
                            <option value="high-stock" ${param.stockLevel == 'high-stock' ? 'selected' : ''}>High Stock (> 50)</option>
                        </select>
                    </div>
                </div>

                <!-- Action Buttons -->
                <div class="row">
                    <div class="col-12">
                        <hr>
                        <div class="d-flex justify-content-between">
                            <button type="button" class="btn btn-outline-secondary" onclick="clearSearch()">
                                <i class="bi bi-x-circle"></i> Clear All
                            </button>
                            <button type="submit" class="btn btn-primary">
                                <i class="bi bi-search"></i> Search Items
                            </button>
                        </div>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <!-- Search Results -->
    <c:if test="${searchPerformed}">
        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <h5 class="card-title mb-0">
                    <i class="bi bi-list-check"></i> Search Results
                    <c:if test="${not empty param.searchTerm}">
                        for "${param.searchTerm}"
                    </c:if>
                </h5>
                <span class="badge bg-primary">${searchResultCount} items found</span>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${not empty items}">
                        <!-- Results Table -->
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
                                        <td><strong>${item.itemId}</strong></td>
                                        <td>
                                            <div>
                                                <strong>${item.itemName}</strong>
                                                <c:if test="${not empty item.description}">
                                                    <br><small class="text-muted">${item.description}</small>
                                                </c:if>
                                            </div>
                                        </td>
                                        <td><span class="badge bg-secondary">${item.category}</span></td>
                                        <td><strong>Rs. <fmt:formatNumber value="${item.price}" pattern="#,##0.00"/></strong></td>
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
                                                   class="btn btn-outline-info" title="View">
                                                    <i class="bi bi-eye"></i>
                                                </a>
                                                <a href="${pageContext.request.contextPath}/items?action=edit&id=${item.itemId}"
                                                   class="btn btn-outline-warning" title="Edit">
                                                    <i class="bi bi-pencil"></i>
                                                </a>
                                                <a href="${pageContext.request.contextPath}/bills?action=create&itemId=${item.itemId}"
                                                   class="btn btn-outline-success" title="Add to Bill">
                                                    <i class="bi bi-cart-plus"></i>
                                                </a>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>

                        <!-- Export Options -->
                        <div class="mt-3 d-flex justify-content-between align-items-center">
                            <div>
                                <button class="btn btn-outline-success btn-sm" onclick="exportResults('csv')">
                                    <i class="bi bi-download"></i> Export CSV
                                </button>
                            </div>
                            <div>
                                <small class="text-muted">
                                    Showing ${items.size()} of ${searchResultCount} results
                                </small>
                            </div>
                        </div>
                    </c:when>

                    <c:otherwise>
                        <!-- No Results -->
                        <div class="text-center py-5">
                            <i class="bi bi-search text-muted" style="font-size: 3rem;"></i>
                            <h5 class="text-muted mt-3">No items found</h5>
                            <p class="text-muted">Try adjusting your search criteria or</p>
                            <div>
                                <button type="button" class="btn btn-outline-primary me-2" onclick="clearSearch()">
                                    <i class="bi bi-arrow-clockwise"></i> Clear Search
                                </button>
                                <a href="${pageContext.request.contextPath}/items?action=create" class="btn btn-primary">
                                    <i class="bi bi-plus-circle"></i> Add New Item
                                </a>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </c:if>

    <!-- Search Tips -->
    <c:if test="${not searchPerformed}">
        <div class="row mt-4">
            <div class="col-md-6">
                <div class="card">
                    <div class="card-header">
                        <h5 class="card-title mb-0">
                            <i class="bi bi-lightbulb"></i> Search Tips
                        </h5>
                    </div>
                    <div class="card-body">
                        <ul class="mb-0">
                            <li>Use partial item names or IDs</li>
                            <li>Filter by category or status</li>
                            <li>Set price ranges for budget analysis</li>
                            <li>Use stock levels for inventory management</li>
                            <li>Combine multiple filters for precise results</li>
                        </ul>
                    </div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="card">
                    <div class="card-header">
                        <h5 class="card-title mb-0">
                            <i class="bi bi-bookmark-star"></i> Quick Searches
                        </h5>
                    </div>
                    <div class="card-body">
                        <div class="d-grid gap-2">
                            <button class="btn btn-outline-primary btn-sm" onclick="quickSearch('low-stock')">
                                <i class="bi bi-exclamation-triangle"></i> Low Stock Items
                            </button>
                            <button class="btn btn-outline-warning btn-sm" onclick="quickSearch('out-of-stock')">
                                <i class="bi bi-x-circle"></i> Out of Stock
                            </button>
                            <button class="btn btn-outline-success btn-sm" onclick="quickSearch('books')">
                                <i class="bi bi-book"></i> Books Category
                            </button>
                            <button class="btn btn-outline-info btn-sm" onclick="quickSearch('high-price')">
                                <i class="bi bi-currency-dollar"></i> High Value Items (Rs. 1000+)
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </c:if>
</div>

<jsp:include page="../common/footer.jsp" />

<script>
    // Clear search form
    function clearSearch() {
        document.getElementById('searchForm').reset();
        window.location.href = '${pageContext.request.contextPath}/items?action=search';
    }

    // Quick search functions
    function quickSearch(type) {
        const form = document.getElementById('searchForm');
        clearSearch();

        switch(type) {
            case 'low-stock':
                document.getElementById('stockLevel').value = 'low-stock';
                break;
            case 'out-of-stock':
                document.getElementById('stockLevel').value = 'out-of-stock';
                break;
            case 'books':
                document.getElementById('category').value = 'Books';
                break;
            case 'high-price':
                document.querySelector('input[name="minPrice"]').value = '1000';
                break;
        }

        form.submit();
    }

    // Export results
    function exportResults(format) {
        const form = document.getElementById('searchForm');
        const exportForm = form.cloneNode(true);
        exportForm.action = '${pageContext.request.contextPath}/items';

        // Add export parameters
        const formatInput = document.createElement('input');
        formatInput.type = 'hidden';
        formatInput.name = 'export';
        formatInput.value = format;
        exportForm.appendChild(formatInput);

        const actionInput = exportForm.querySelector('input[name="action"]');
        actionInput.value = 'export';

        // Submit to new window
        exportForm.target = '_blank';
        exportForm.method = 'POST';
        document.body.appendChild(exportForm);
        exportForm.submit();
        document.body.removeChild(exportForm);
    }

    // Form validation
    document.getElementById('searchForm').addEventListener('submit', function(e) {
        const minPrice = document.querySelector('input[name="minPrice"]').value;
        const maxPrice = document.querySelector('input[name="maxPrice"]').value;

        if (minPrice && maxPrice && parseFloat(minPrice) > parseFloat(maxPrice)) {
            e.preventDefault();
            alert('Minimum price cannot be greater than maximum price');
            return false;
        }
    });

    // Focus on search term when page loads
    window.addEventListener('load', function() {
        document.getElementById('searchTerm').focus();
    });
</script>