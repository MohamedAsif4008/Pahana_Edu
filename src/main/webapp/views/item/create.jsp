<%--
  Created by IntelliJ IDEA.
  User: MOHAMED
  Date: 16/08/2025
  Time: 12:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Add Item" />
<jsp:include page="../common/header.jsp" />

<!-- Page Content -->
<div class="col-md-10 main-content p-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="h3">
            <i class="bi bi-plus-circle"></i> Add New Item
        </h1>
        <div>
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
                    <form action="${pageContext.request.contextPath}/items" method="post" id="itemForm">
                        <input type="hidden" name="action" value="create">

                        <div class="row">
                            <!-- Item ID -->
                            <div class="col-md-6 mb-3">
                                <label for="itemId" class="form-label">
                                    <i class="bi bi-hash"></i> Item ID *
                                </label>
                                <input type="text"
                                       class="form-control"
                                       id="itemId"
                                       name="itemId"
                                       value="${param.itemId}"
                                       placeholder="Enter unique item ID"
                                       required>
                                <small class="form-text text-muted">
                                    Must be unique (e.g., BOOK001, ITM001)
                                </small>
                            </div>

                            <!-- Item Name -->
                            <div class="col-md-6 mb-3">
                                <label for="itemName" class="form-label">
                                    <i class="bi bi-tag"></i> Item Name *
                                </label>
                                <input type="text"
                                       class="form-control"
                                       id="itemName"
                                       name="itemName"
                                       value="${param.itemName}"
                                       placeholder="Enter item name"
                                       required>
                            </div>

                            <!-- Category -->
                            <div class="col-md-6 mb-3">
                                <label for="category" class="form-label">
                                    <i class="bi bi-collection"></i> Category *
                                </label>
                                <select class="form-select" id="category" name="category" required>
                                    <option value="">Select Category</option>
                                    <option value="Books" ${param.category == 'Books' ? 'selected' : ''}>Books</option>
                                    <option value="Stationery" ${param.category == 'Stationery' ? 'selected' : ''}>Stationery</option>
                                    <option value="Electronics" ${param.category == 'Electronics' ? 'selected' : ''}>Electronics</option>
                                    <option value="Art Supplies" ${param.category == 'Art Supplies' ? 'selected' : ''}>Art Supplies</option>
                                    <option value="Educational" ${param.category == 'Educational' ? 'selected' : ''}>Educational</option>
                                    <option value="Other" ${param.category == 'Other' ? 'selected' : ''}>Other</option>
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
                                       value="${param.price}"
                                       placeholder="0.00"
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
                                       value="${param.stockQuantity != null ? param.stockQuantity : '0'}"
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
                                       value="${param.minStockLevel != null ? param.minStockLevel : '5'}"
                                       min="0"
                                       step="1">
                                <small class="form-text text-muted">
                                    Alert when stock falls below this level
                                </small>
                            </div>

                            <!-- Description -->
                            <div class="col-md-12 mb-3">
                                <label for="description" class="form-label">
                                    <i class="bi bi-card-text"></i> Description
                                </label>
                                <textarea class="form-control"
                                          id="description"
                                          name="description"
                                          rows="3"
                                          placeholder="Enter item description (optional)">${param.description}</textarea>
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
                                       value="${param.supplier}"
                                       placeholder="Supplier name">
                            </div>

                            <!-- Status -->
                            <div class="col-md-6 mb-3">
                                <label for="active" class="form-label">
                                    <i class="bi bi-toggle-on"></i> Status
                                </label>
                                <select class="form-select" id="active" name="active">
                                    <option value="true" ${param.active != 'false' ? 'selected' : ''}>Active</option>
                                    <option value="false" ${param.active == 'false' ? 'selected' : ''}>Inactive</option>
                                </select>
                            </div>
                        </div>

                        <!-- Submit Buttons -->
                        <div class="row">
                            <div class="col-12">
                                <hr>
                                <div class="d-flex justify-content-between">
                                    <button type="button" class="btn btn-secondary" onclick="resetForm()">
                                        <i class="bi bi-arrow-clockwise"></i> Reset Form
                                    </button>
                                    <div>
                                        <button type="button" class="btn btn-outline-primary me-2" onclick="previewItem()">
                                            <i class="bi bi-eye"></i> Preview
                                        </button>
                                        <button type="submit" class="btn btn-primary">
                                            <i class="bi bi-check-circle"></i> Create Item
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Help Panel -->
        <div class="col-md-4">
            <div class="card">
                <div class="card-header">
                    <h5 class="card-title mb-0">
                        <i class="bi bi-lightbulb"></i> Help & Tips
                    </h5>
                </div>
                <div class="card-body">
                    <h6 class="text-primary">Required Fields</h6>
                    <ul class="small">
                        <li>Item ID (unique)</li>
                        <li>Item Name</li>
                        <li>Category</li>
                        <li>Price</li>
                        <li>Stock Quantity</li>
                    </ul>

                    <h6 class="text-primary mt-3">Item ID Tips</h6>
                    <ul class="small">
                        <li>Use format: BOOK001, STAT001</li>
                        <li>Must be unique in system</li>
                        <li>Cannot be changed later</li>
                    </ul>

                    <h6 class="text-primary mt-3">Stock Management</h6>
                    <p class="small">
                        Set minimum stock level to get alerts when inventory is low.
                        This helps maintain adequate stock for customer demand.
                    </p>
                </div>
            </div>

            <!-- Sample Data -->
            <div class="card mt-3">
                <div class="card-header">
                    <h5 class="card-title mb-0">
                        <i class="bi bi-clipboard-data"></i> Sample Data
                    </h5>
                </div>
                <div class="card-body">
                    <button type="button" class="btn btn-sm btn-outline-info w-100 mb-2" onclick="fillSampleBook()">
                        <i class="bi bi-book"></i> Sample Book
                    </button>
                    <button type="button" class="btn btn-sm btn-outline-success w-100" onclick="fillSampleStationery()">
                        <i class="bi bi-pencil"></i> Sample Stationery
                    </button>
                    <small class="text-muted d-block mt-2">
                        Click to populate form with sample data for testing
                    </small>
                </div>
            </div>

            <!-- Price Calculator -->
            <div class="card mt-3">
                <div class="card-header">
                    <h5 class="card-title mb-0">
                        <i class="bi bi-calculator"></i> Price Calculator
                    </h5>
                </div>
                <div class="card-body">
                    <div class="mb-2">
                        <label class="form-label small">Cost Price:</label>
                        <input type="number" class="form-control form-control-sm" id="costPrice" placeholder="0.00" step="0.01">
                    </div>
                    <div class="mb-2">
                        <label class="form-label small">Markup %:</label>
                        <input type="number" class="form-control form-control-sm" id="markup" placeholder="30" step="1">
                    </div>
                    <button class="btn btn-sm btn-outline-primary w-100" onclick="calculatePrice()">
                        Calculate Selling Price
                    </button>
                    <div class="mt-2" id="calculatedPrice"></div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />

<script>
    // Form validation
    document.getElementById('itemForm').addEventListener('submit', function(e) {
        const itemId = document.getElementById('itemId').value.trim();
        const itemName = document.getElementById('itemName').value.trim();
        const category = document.getElementById('category').value;
        const price = document.getElementById('price').value;
        const stockQuantity = document.getElementById('stockQuantity').value;

        if (!itemId || !itemName || !category || !price || !stockQuantity) {
            e.preventDefault();
            alert('Please fill in all required fields marked with *');
            return false;
        }

        // Item ID validation
        if (itemId.length < 3) {
            e.preventDefault();
            alert('Item ID must be at least 3 characters long');
            return false;
        }

        // Price validation
        if (parseFloat(price) <= 0) {
            e.preventDefault();
            alert('Price must be greater than 0');
            return false;
        }
    });

    // Reset form
    function resetForm() {
        if (confirm('Are you sure you want to reset the form? All entered data will be lost.')) {
            document.getElementById('itemForm').reset();
            document.getElementById('itemId').focus();
        }
    }

    // Preview item data
    function previewItem() {
        const formData = new FormData(document.getElementById('itemForm'));
        let preview = 'Item Preview:\n\n';

        preview += `Item ID: ${formData.get('itemId')}\n`;
        preview += `Name: ${formData.get('itemName')}\n`;
        preview += `Category: ${formData.get('category')}\n`;
        preview += `Price: Rs. ${formData.get('price')}\n`;
        preview += `Stock: ${formData.get('stockQuantity')} units\n`;
        preview += `Min Stock: ${formData.get('minStockLevel')}\n`;
        preview += `Supplier: ${formData.get('supplier')}\n`;
        preview += `Status: ${formData.get('active') === 'true' ? 'Active' : 'Inactive'}\n`;
        preview += `Description: ${formData.get('description')}`;

        alert(preview);
    }

    // Fill sample book data
    function fillSampleBook() {
        const timestamp = Date.now().toString().slice(-4);

        document.getElementById('itemId').value = 'BOOK' + timestamp;
        document.getElementById('itemName').value = 'Advanced Programming Guide';
        document.getElementById('category').value = 'Books';
        document.getElementById('price').value = '2500.00';
        document.getElementById('stockQuantity').value = '20';
        document.getElementById('minStockLevel').value = '5';
        document.getElementById('description').value = 'Comprehensive guide for advanced programming concepts and best practices.';
        document.getElementById('supplier').value = 'Educational Publishers Ltd';
        document.getElementById('active').value = 'true';
    }

    // Fill sample stationery data
    function fillSampleStationery() {
        const timestamp = Date.now().toString().slice(-4);

        document.getElementById('itemId').value = 'STAT' + timestamp;
        document.getElementById('itemName').value = 'Premium Notebook Set';
        document.getElementById('category').value = 'Stationery';
        document.getElementById('price').value = '450.00';
        document.getElementById('stockQuantity').value = '50';
        document.getElementById('minStockLevel').value = '10';
        document.getElementById('description').value = 'High-quality notebook set with ruled pages, perfect for students.';
        document.getElementById('supplier').value = 'Office Supplies Co';
        document.getElementById('active').value = 'true';
    }

    // Price calculator
    function calculatePrice() {
        const costPrice = parseFloat(document.getElementById('costPrice').value) || 0;
        const markup = parseFloat(document.getElementById('markup').value) || 0;

        if (costPrice > 0 && markup > 0) {
            const sellingPrice = costPrice * (1 + markup / 100);
            document.getElementById('calculatedPrice').innerHTML =
                `<strong>Selling Price: Rs. ${sellingPrice.toFixed(2)}</strong>`;

            // Auto-fill the price field
            document.getElementById('price').value = sellingPrice.toFixed(2);
        } else {
            document.getElementById('calculatedPrice').innerHTML =
                '<small class="text-muted">Enter cost price and markup %</small>';
        }
    }

    // Auto-generate item ID suggestion
    document.getElementById('itemName').addEventListener('blur', function() {
        const itemIdField = document.getElementById('itemId');
        const category = document.getElementById('category').value;

        if (!itemIdField.value && this.value && category) {
            const name = this.value.trim().toUpperCase();
            const categoryPrefix = category.substring(0, 4).toUpperCase();
            const timestamp = Date.now().toString().slice(-3);
            itemIdField.value = categoryPrefix + timestamp;
        }
    });

    // Focus on first field when page loads
    window.addEventListener('load', function() {
        document.getElementById('itemId').focus();
    });
</script>