<%--
  Created by IntelliJ IDEA.
  User: MOHAMED
  Date: 16/08/2025
  Time: 12:40
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Edit Item" />
<jsp:include page="../common/header.jsp" />

<!-- Page Content -->
<div class="col-md-10 main-content p-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="h3">
            <i class="bi bi-pencil-square"></i> Edit Item
        </h1>
        <div>
            <a href="${pageContext.request.contextPath}/items?action=view&id=${item.itemId}" class="btn btn-info me-2">
                <i class="bi bi-eye"></i> View Details
            </a>
            <a href="${pageContext.request.contextPath}/items" class="btn btn-secondary">
                <i class="bi bi-arrow-left"></i> Back to List
            </a>
        </div>
    </div>

    <div class="row">
        <div class="col-md-8">
            <div class="card">
                <div class="card-header">
                    <h5 class="card-title mb-0">
                        <i class="bi bi-info-circle"></i> Item Information
                    </h5>
                </div>
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/items" method="post" id="editItemForm">
                        <input type="hidden" name="action" value="update">
                        <input type="hidden" name="id" value="${item.itemId}">

                        <div class="row">
                            <!-- Item ID (Read-only) -->
                            <div class="col-md-6 mb-3">
                                <label for="itemId" class="form-label">
                                    <i class="bi bi-hash"></i> Item ID
                                </label>
                                <input type="text"
                                       class="form-control"
                                       id="itemId"
                                       value="${item.itemId}"
                                       readonly>
                                <small class="form-text text-muted">
                                    Item ID cannot be changed
                                </small>
                            </div>

                            <!-- Item Name -->
                            <div class="col-md-6 mb-3">
                                <label for="name" class="form-label">
                                    <i class="bi bi-tag"></i> Item Name *
                                </label>
                                <input type="text"
                                       class="form-control"
                                       id="name"
                                       name="name"
                                       value="${item.name}"
                                       required>
                            </div>

                            <!-- Category -->
                            <div class="col-md-6 mb-3">
                                <label for="category" class="form-label">
                                    <i class="bi bi-collection"></i> Category *
                                </label>
                                <select class="form-select" id="category" name="category" required>
                                    <option value="">Select Category</option>
                                    <option value="Books" ${item.category == 'Books' ? 'selected' : ''}>Books</option>
                                    <option value="Stationery" ${item.category == 'Stationery' ? 'selected' : ''}>Stationery</option>
                                    <option value="Electronics" ${item.category == 'Electronics' ? 'selected' : ''}>Electronics</option>
                                    <option value="Art Supplies" ${item.category == 'Art Supplies' ? 'selected' : ''}>Art Supplies</option>
                                    <option value="Educational" ${item.category == 'Educational' ? 'selected' : ''}>Educational</option>
                                    <option value="Other" ${item.category == 'Other' ? 'selected' : ''}>Other</option>
                                </select>
                            </div>

                            <!-- Price -->
                            <div class="col-md-6 mb-3">
                                <label for="price" class="form-label">
                                    <i class="bi bi-currency-dollar"></i> Price (Rs.) *
                                </label>
                                <input type="number"
                                       class="form-control"
                                       id="price"
                                       name="price"
                                       value="${item.price}"
                                       min="0.01"
                                       step="0.01"
                                       required>
                            </div>

                            <!-- Stock Quantity -->
                            <div class="col-md-6 mb-3">
                                <label for="stockQuantity" class="form-label">
                                    <i class="bi bi-boxes"></i> Stock Quantity *
                                </label>
                                <input type="number"
                                       class="form-control"
                                       id="stockQuantity"
                                       name="stockQuantity"
                                       value="${item.stockQuantity}"
                                       min="0"
                                       step="1"
                                       required>
                            </div>

                            <!-- Minimum Stock Level -->
                            <div class="col-md-6 mb-3">
                                <label for="minStockLevel" class="form-label">
                                    <i class="bi bi-exclamation-triangle"></i> Minimum Stock Level
                                </label>
                                <input type="number"
                                       class="form-control"
                                       id="minStockLevel"
                                       name="minStockLevel"
                                       value="${item.minStockLevel}"
                                       min="0"
                                       step="1">
                            </div>

                            <!-- Description -->
                            <div class="col-md-12 mb-3">
                                <label for="description" class="form-label">
                                    <i class="bi bi-card-text"></i> Description
                                </label>
                                <textarea class="form-control"
                                          id="description"
                                          name="description"
                                          rows="3">${item.description}</textarea>
                            </div>

                            <!-- Supplier -->
                            <div class="col-md-6 mb-3">
                                <label for="supplier" class="form-label">
                                    <i class="bi bi-building"></i> Supplier
                                </label>
                                <input type="text"
                                       class="form-control"
                                       id="supplier"
                                       name="supplier"
                                       value="${item.supplier}">
                            </div>

                            <!-- Status -->
                            <div class="col-md-6 mb-3">
                                <label for="active" class="form-label">
                                    <i class="bi bi-toggle-on"></i> Status
                                </label>
                                <select class="form-select" id="active" name="active">
                                    <option value="true" ${item.active ? 'selected' : ''}>Active</option>
                                    <option value="false" ${!item.active ? 'selected' : ''}>Inactive</option>
                                </select>
                            </div>
                        </div>

                        <!-- Submit Buttons -->
                        <div class="row">
                            <div class="col-12">
                                <hr>
                                <div class="d-flex justify-content-between">
                                    <button type="button" class="btn btn-warning" onclick="resetToOriginal()">
                                        <i class="bi bi-arrow-clockwise"></i> Reset Changes
                                    </button>
                                    <div>
                                        <button type="button" class="btn btn-outline-primary me-2" onclick="previewChanges()">
                                            <i class="bi bi-eye"></i> Preview Changes
                                        </button>
                                        <button type="submit" class="btn btn-success">
                                            <i class="bi bi-check-circle"></i> Update Item
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Item Info Panel -->
        <div class="col-md-4">
            <div class="card">
                <div class="card-header">
                    <h5 class="card-title mb-0">
                        <i class="bi bi-info-square"></i> Item Summary
                    </h5>
                </div>
                <div class="card-body">
                    <div class="text-center mb-3">
                        <i class="bi bi-box-seam text-primary" style="font-size: 3rem;"></i>
                        <h6 class="mt-2">${item.name}</h6>
                        <span class="badge bg-${item.active ? 'success' : 'danger'}">
                            ${item.active ? 'Active' : 'Inactive'}
                        </span>
                    </div>

                    <table class="table table-sm">
                        <tr>
                            <td><strong>Item ID:</strong></td>
                            <td>${item.itemId}</td>
                        </tr>
                        <tr>
                            <td><strong>Category:</strong></td>
                            <td>${item.category}</td>
                        </tr>
                        <tr>
                            <td><strong>Price:</strong></td>
                            <td>Rs. <fmt:formatNumber value="${item.price}" pattern="#,##0.00"/></td>
                        </tr>
                        <tr>
                            <td><strong>Stock:</strong></td>
                            <td>
                                <span class="badge bg-${item.stockQuantity > 10 ? 'success' : item.stockQuantity > 0 ? 'warning' : 'danger'}">
                                    ${item.stockQuantity} units
                                </span>
                            </td>
                        </tr>
                        <tr>
                            <td><strong>Supplier:</strong></td>
                            <td><small>${item.supplier}</small></td>
                        </tr>
                        <c:if test="${not empty item.createdDate}">
                            <tr>
                                <td><strong>Added:</strong></td>
                                <td><small><fmt:formatDate value="${item.createdDate}" pattern="MMM yyyy"/></small></td>
                            </tr>
                        </c:if>
                    </table>
                </div>
            </div>

            <!-- Stock Management -->
            <div class="card mt-3">
                <div class="card-header">
                    <h5 class="card-title mb-0">
                        <i class="bi bi-boxes"></i> Stock Management
                    </h5>
                </div>
                <div class="card-body">
                    <div class="mb-3">
                        <label class="form-label small">Quick Stock Update:</label>
                        <div class="input-group">
                            <input type="number" class="form-control" id="stockAdjustment" placeholder="0">
                            <button class="btn btn-outline-success" onclick="adjustStock(1)">
                                <i class="bi bi-plus"></i>
                            </button>
                            <button class="btn btn-outline-danger" onclick="adjustStock(-1)">
                                <i class="bi bi-dash"></i>
                            </button>
                        </div>
                    </div>

                    <c:if test="${item.stockQuantity <= item.minStockLevel}">
                        <div class="alert alert-warning alert-sm">
                            <i class="bi bi-exclamation-triangle"></i>
                            <strong>Low Stock Alert!</strong><br>
                            Current stock is at or below minimum level.
                        </div>
                    </c:if>
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
                        <button class="btn btn-primary btn-sm" onclick="addToBill()">
                            <i class="bi bi-cart-plus"></i> Add to New Bill
                        </button>
                        <button class="btn btn-info btn-sm" onclick="viewSalesHistory()">
                            <i class="bi bi-graph-up"></i> View Sales History
                        </button>
                        <button class="btn btn-secondary btn-sm" onclick="duplicateItem()">
                            <i class="bi bi-files"></i> Duplicate Item
                        </button>
                    </div>
                </div>
            </div>

            <!-- Edit History (if available) -->
            <c:if test="${not empty item.lastModifiedDate}">
                <div class="card mt-3">
                    <div class="card-header">
                        <h5 class="card-title mb-0">
                            <i class="bi bi-clock-history"></i> Last Updated
                        </h5>
                    </div>
                    <div class="card-body">
                        <p class="small mb-1">
                            <strong>Date:</strong> <fmt:formatDate value="${item.lastModifiedDate}" pattern="MMM dd, yyyy HH:mm"/>
                        </p>
                        <c:if test="${not empty item.lastModifiedBy}">
                            <p class="small mb-0">
                                <strong>By:</strong> ${item.lastModifiedBy}
                            </p>
                        </c:if>
                    </div>
                </div>
            </c:if>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />

<script>
    // Store original values for reset functionality
    const originalValues = {
        name: '${item.name}',
        category: '${item.category}',
        price: '${item.price}',
        stockQuantity: '${item.stockQuantity}',
        minStockLevel: '${item.minStockLevel}',
        description: '${item.description}',
        supplier: '${item.supplier}',
        active: '${item.active}'
    };

    // Form validation
    document.getElementById('editItemForm').addEventListener('submit', function(e) {
        const name = document.getElementById('name').value.trim();
        const category = document.getElementById('category').value;
        const price = document.getElementById('price').value;
        const stockQuantity = document.getElementById('stockQuantity').value;

        if (!name || !category || !price || stockQuantity === '') {
            e.preventDefault();
            alert('Please fill in all required fields marked with *');
            return false;
        }

        // Price validation
        if (parseFloat(price) <= 0) {
            e.preventDefault();
            alert('Price must be greater than 0');
            return false;
        }

        // Confirm update
        if (!confirm('Are you sure you want to update this item information?')) {
            e.preventDefault();
            return false;
        }
    });

    // Reset to original values
    function resetToOriginal() {
        if (confirm('Are you sure you want to reset all changes?')) {
            document.getElementById('name').value = originalValues.name;
            document.getElementById('category').value = originalValues.category;
            document.getElementById('price').value = originalValues.price;
            document.getElementById('stockQuantity').value = originalValues.stockQuantity;
            document.getElementById('minStockLevel').value = originalValues.minStockLevel;
            document.getElementById('description').value = originalValues.description;
            document.getElementById('supplier').value = originalValues.supplier;
            document.getElementById('active').value = originalValues.active;
        }
    }

    // Preview changes
    function previewChanges() {
        const changes = [];

        if (document.getElementById('name').value !== originalValues.name) {
            changes.push(`Name: "${originalValues.name}" → "${document.getElementById('name').value}"`);
        }
        if (document.getElementById('category').value !== originalValues.category) {
            changes.push(`Category: "${originalValues.category}" → "${document.getElementById('category').value}"`);
        }
        if (document.getElementById('price').value !== originalValues.price) {
            changes.push(`Price: Rs. ${originalValues.price} → Rs. ${document.getElementById('price').value}`);
        }
        if (document.getElementById('stockQuantity').value !== originalValues.stockQuantity) {
            changes.push(`Stock: ${originalValues.stockQuantity} → ${document.getElementById('stockQuantity').value}`);
        }
        if (document.getElementById('minStockLevel').value !== originalValues.minStockLevel) {
            changes.push(`Min Stock: ${originalValues.minStockLevel} → ${document.getElementById('minStockLevel').value}`);
        }
        if (document.getElementById('supplier').value !== originalValues.supplier) {
            changes.push(`Supplier: "${originalValues.supplier}" → "${document.getElementById('supplier').value}"`);
        }
        if (document.getElementById('active').value !== originalValues.active) {
            const oldStatus = originalValues.active === 'true' ? 'Active' : 'Inactive';
            const newStatus = document.getElementById('active').value === 'true' ? 'Active' : 'Inactive';
            changes.push(`Status: ${oldStatus} → ${newStatus}`);
        }

        if (changes.length === 0) {
            alert('No changes detected.');
        } else {
            const preview = 'Changes to be saved:\n\n' + changes.join('\n');
            alert(preview);
        }
    }

    // Stock adjustment
    function adjustStock(direction) {
        const adjustment = parseInt(document.getElementById('stockAdjustment').value) || 0;
        const currentStock = parseInt(document.getElementById('stockQuantity').value) || 0;

        if (adjustment > 0) {
            const newStock = currentStock + (adjustment * direction);
            if (newStock >= 0) {
                document.getElementById('stockQuantity').value = newStock;
                document.getElementById('stockAdjustment').value = '';
            } else {
                alert('Stock cannot be negative');
            }
        } else {
            alert('Please enter a valid adjustment amount');
        }
    }

    // Quick action functions
    function addToBill() {
        window.location.href = '${pageContext.request.contextPath}/bills?action=create&itemId=${item.itemId}';
    }

    function viewSalesHistory() {
        alert('Sales history feature would show billing records for this item.');
    }

    function duplicateItem() {
        window.location.href = '${pageContext.request.contextPath}/items?action=create&duplicateFrom=${item.itemId}';
    }

    // Focus on first editable field when page loads
    window.addEventListener('load', function() {
        document.getElementById('name').focus();
    });

    // Mark form as dirty when changes are made
    let formDirty = false;
    const formInputs = document.querySelectorAll('#editItemForm input, #editItemForm textarea, #editItemForm select');
    formInputs.forEach(input => {
        input.addEventListener('change', function() {
            formDirty = true;
        });
    });

    // Warn user about unsaved changes
    window.addEventListener('beforeunload', function(e) {
        if (formDirty) {
            e.preventDefault();
            e.returnValue = 'You have unsaved changes. Are you sure you want to leave?';
            return e.returnValue;
        }
    });

    // Reset dirty flag when form is submitted
    document.getElementById('editItemForm').addEventListener('submit', function() {
        formDirty = false;
    });
</script>