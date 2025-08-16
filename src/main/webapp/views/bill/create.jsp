<%--
  Created by IntelliJ IDEA.
  User: MOHAMED
  Date: 16/08/2025
  Time: 13:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Create Bill" />
<jsp:include page="../common/header.jsp" />

<!-- Page Content -->
<div class="col-md-10 main-content p-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="h3">
            <i class="bi bi-plus-circle"></i> Create New Bill
        </h1>
        <div>
            <a href="${pageContext.request.contextPath}/bills" class="btn btn-secondary">
                <i class="bi bi-arrow-left"></i> Back to List
            </a>
        </div>
    </div>

    <form action="${pageContext.request.contextPath}/bills" method="post" id="billForm">
        <input type="hidden" name="action" value="create">

        <div class="row">
            <!-- Bill Information -->
            <div class="col-md-8">
                <div class="card">
                    <div class="card-header">
                        <h5 class="card-title mb-0">
                            <i class="bi bi-info-circle"></i> Bill Information
                        </h5>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <!-- Bill Number -->
                            <div class="col-md-6 mb-3">
                                <label for="billNumber" class="form-label">
                                    <i class="bi bi-hash"></i> Bill Number *
                                </label>
                                <input type="text"
                                       class="form-control"
                                       id="billNumber"
                                       name="billNumber"
                                       value="${param.billNumber != null ? param.billNumber : generatedBillNumber}"
                                       placeholder="Auto-generated"
                                       required>
                                <small class="form-text text-muted">
                                    Unique bill identifier
                                </small>
                            </div>

                            <!-- Customer Selection -->
                            <div class="col-md-6 mb-3">
                                <label for="customerAccountNumber" class="form-label">
                                    <i class="bi bi-person"></i> Customer *
                                </label>
                                <div class="input-group">
                                    <select class="form-select" id="customerAccountNumber" name="customerAccountNumber" required>
                                        <option value="">Select Customer</option>
                                        <c:forEach items="${customers}" var="customer">
                                            <option value="${customer.accountNumber}"
                                                ${param.customerId == customer.accountNumber ? 'selected' : ''}>
                                                    ${customer.accountNumber} - ${customer.fullName}
                                            </option>
                                        </c:forEach>
                                    </select>
                                    <a href="${pageContext.request.contextPath}/customers?action=create"
                                       class="btn btn-outline-primary" target="_blank" title="Add New Customer">
                                        <i class="bi bi-plus"></i>
                                    </a>
                                </div>
                            </div>

                            <!-- Payment Method -->
                            <div class="col-md-6 mb-3">
                                <label for="paymentMethod" class="form-label">
                                    <i class="bi bi-credit-card"></i> Payment Method *
                                </label>
                                <select class="form-select" id="paymentMethod" name="paymentMethod" required>
                                    <option value="">Select Payment Method</option>
                                    <option value="CASH" ${param.paymentMethod == 'CASH' ? 'selected' : ''}>Cash</option>
                                    <option value="CARD" ${param.paymentMethod == 'CARD' ? 'selected' : ''}>Card</option>
                                </select>
                            </div>

                            <!-- Bill Date -->
                            <div class="col-md-6 mb-3">
                                <label for="billDate" class="form-label">
                                    <i class="bi bi-calendar"></i> Bill Date
                                </label>
                                <input type="date"
                                       class="form-control"
                                       id="billDate"
                                       name="billDate"
                                       value="${param.billDate != null ? param.billDate : todayDate}">
                            </div>

                            <!-- Notes -->
                            <div class="col-md-12 mb-3">
                                <label for="notes" class="form-label">
                                    <i class="bi bi-card-text"></i> Notes
                                </label>
                                <textarea class="form-control"
                                          id="notes"
                                          name="notes"
                                          rows="2"
                                          placeholder="Additional notes (optional)">${param.notes}</textarea>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Bill Items -->
                <div class="card mt-4">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h5 class="card-title mb-0">
                            <i class="bi bi-list"></i> Bill Items
                        </h5>
                        <button type="button" class="btn btn-sm btn-primary" onclick="addBillItem()">
                            <i class="bi bi-plus"></i> Add Item
                        </button>
                    </div>
                    <div class="card-body">
                        <div id="billItemsContainer">
                            <!-- Dynamic bill items will be added here -->
                            <div class="bill-item row mb-3" id="billItem_0">
                                <div class="col-md-4">
                                    <label class="form-label">Item *</label>
                                    <select class="form-select item-select" name="itemId[]" required onchange="updateItemPrice(0)">
                                        <option value="">Select Item</option>
                                        <c:forEach items="${items}" var="item">
                                            <option value="${item.itemId}"
                                                    data-price="${item.price}"
                                                    data-stock="${item.stockQuantity}"
                                                ${param.itemId == item.itemId ? 'selected' : ''}>
                                                    ${item.itemId} - ${item.itemName} (Rs. ${item.price})
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="col-md-2">
                                    <label class="form-label">Quantity *</label>
                                    <input type="number"
                                           class="form-control quantity-input"
                                           name="quantity[]"
                                           min="1"
                                           value="1"
                                           required
                                           onchange="calculateLineTotal(0)">
                                </div>
                                <div class="col-md-2">
                                    <label class="form-label">Unit Price</label>
                                    <input type="number"
                                           class="form-control unit-price"
                                           name="unitPrice[]"
                                           step="0.01"
                                           readonly>
                                </div>
                                <div class="col-md-2">
                                    <label class="form-label">Line Total</label>
                                    <input type="number"
                                           class="form-control line-total"
                                           name="lineTotal[]"
                                           step="0.01"
                                           readonly>
                                </div>
                                <div class="col-md-2">
                                    <label class="form-label">&nbsp;</label>
                                    <button type="button" class="btn btn-outline-danger w-100" onclick="removeBillItem(0)">
                                        <i class="bi bi-trash"></i>
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Submit Buttons -->
                <div class="mt-4 text-end">
                    <button type="button" class="btn btn-outline-secondary me-2" onclick="resetForm()">
                        <i class="bi bi-arrow-clockwise"></i> Reset
                    </button>
                    <button type="button" class="btn btn-outline-primary me-2" onclick="previewBill()">
                        <i class="bi bi-eye"></i> Preview
                    </button>
                    <button type="submit" class="btn btn-success">
                        <i class="bi bi-check-circle"></i> Create Bill
                    </button>
                </div>
            </div>

            <!-- Bill Summary -->
            <div class="col-md-4">
                <div class="card sticky-top">
                    <div class="card-header">
                        <h5 class="card-title mb-0">
                            <i class="bi bi-calculator"></i> Bill Summary
                        </h5>
                    </div>
                    <div class="card-body">
                        <table class="table table-borderless">
                            <tr>
                                <td><strong>Subtotal:</strong></td>
                                <td class="text-end"><strong id="subtotalAmount">Rs. 0.00</strong></td>
                            </tr>
                            <tr>
                                <td>Tax (0%):</td>
                                <td class="text-end" id="taxAmount">Rs. 0.00</td>
                            </tr>
                            <tr>
                                <td>Discount:</td>
                                <td class="text-end">
                                    <div class="input-group input-group-sm">
                                        <span class="input-group-text">Rs.</span>
                                        <input type="number"
                                               class="form-control"
                                               id="discountAmount"
                                               name="discountAmount"
                                               value="0"
                                               min="0"
                                               step="0.01"
                                               onchange="calculateTotals()">
                                    </div>
                                </td>
                            </tr>
                            <tr class="table-primary">
                                <td><strong>Total Amount:</strong></td>
                                <td class="text-end"><strong id="totalAmount">Rs. 0.00</strong></td>
                            </tr>
                        </table>

                        <hr>

                        <!-- Quick Add Items -->
                        <h6>Quick Add Items</h6>
                        <div class="mb-2">
                            <select class="form-select form-select-sm" id="quickAddItem">
                                <option value="">Select item to add...</option>
                                <c:forEach items="${popularItems}" var="item">
                                    <option value="${item.itemId}"
                                            data-price="${item.price}"
                                            data-name="${item.itemName}">
                                            ${item.itemName} - Rs. ${item.price}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <button type="button" class="btn btn-sm btn-outline-primary w-100" onclick="quickAddItem()">
                            <i class="bi bi-plus"></i> Quick Add
                        </button>
                    </div>
                </div>

                <!-- Customer Info -->
                <div class="card mt-3" id="customerInfo" style="display: none;">
                    <div class="card-header">
                        <h5 class="card-title mb-0">
                            <i class="bi bi-person-circle"></i> Customer Info
                        </h5>
                    </div>
                    <div class="card-body">
                        <div id="customerDetails">
                            <!-- Customer details will be populated here -->
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </form>
</div>

<jsp:include page="../common/footer.jsp" />

<script>
    let billItemCounter = 1;

    // Add new bill item row
    function addBillItem() {
        const container = document.getElementById('billItemsContainer');
        const newItem = document.createElement('div');
        newItem.className = 'bill-item row mb-3';
        newItem.id = `billItem_${billItemCounter}`;

        newItem.innerHTML = `
        <div class="col-md-4">
            <label class="form-label">Item *</label>
            <select class="form-select item-select" name="itemId[]" required onchange="updateItemPrice(${billItemCounter})">
                <option value="">Select Item</option>
                <c:forEach items="${items}" var="item">
                    <option value="${item.itemId}" data-price="${item.price}" data-stock="${item.stockQuantity}">
                        ${item.itemId} - ${item.itemName} (Rs. ${item.price})
                    </option>
                </c:forEach>
            </select>
        </div>
        <div class="col-md-2">
            <label class="form-label">Quantity *</label>
            <input type="number" class="form-control quantity-input" name="quantity[]" min="1" value="1" required onchange="calculateLineTotal(${billItemCounter})">
        </div>
        <div class="col-md-2">
            <label class="form-label">Unit Price</label>
            <input type="number" class="form-control unit-price" name="unitPrice[]" step="0.01" readonly>
        </div>
        <div class="col-md-2">
            <label class="form-label">Line Total</label>
            <input type="number" class="form-control line-total" name="lineTotal[]" step="0.01" readonly>
        </div>
        <div class="col-md-2">
            <label class="form-label">&nbsp;</label>
            <button type="button" class="btn btn-outline-danger w-100" onclick="removeBillItem(${billItemCounter})">
                <i class="bi bi-trash"></i>
            </button>
        </div>
    `;

        container.appendChild(newItem);
        billItemCounter++;
    }

    // Remove bill item row
    function removeBillItem(index) {
        const billItem = document.getElementById(`billItem_${index}`);
        if (billItem && document.querySelectorAll('.bill-item').length > 1) {
            billItem.remove();
            calculateTotals();
        } else {
            alert('At least one item is required for the bill.');
        }
    }

    // Update item price when item is selected
    function updateItemPrice(index) {
        const itemSelect = document.querySelector(`#billItem_${index} .item-select`);
        const unitPriceInput = document.querySelector(`#billItem_${index} .unit-price`);

        if (itemSelect.value) {
            const selectedOption = itemSelect.options[itemSelect.selectedIndex];
            const price = selectedOption.getAttribute('data-price');
            unitPriceInput.value = price;
            calculateLineTotal(index);
        } else {
            unitPriceInput.value = '';
            calculateLineTotal(index);
        }
    }

    // Calculate line total for a specific item
    function calculateLineTotal(index) {
        const quantityInput = document.querySelector(`#billItem_${index} .quantity-input`);
        const unitPriceInput = document.querySelector(`#billItem_${index} .unit-price`);
        const lineTotalInput = document.querySelector(`#billItem_${index} .line-total`);

        const quantity = parseFloat(quantityInput.value) || 0;
        const unitPrice = parseFloat(unitPriceInput.value) || 0;
        const lineTotal = quantity * unitPrice;

        lineTotalInput.value = lineTotal.toFixed(2);
        calculateTotals();
    }

    // Calculate bill totals
    function calculateTotals() {
        let subtotal = 0;

        // Sum all line totals
        document.querySelectorAll('.line-total').forEach(function(input) {
            subtotal += parseFloat(input.value) || 0;
        });

        const discount = parseFloat(document.getElementById('discountAmount').value) || 0;
        const tax = 0; // No tax for now
        const total = subtotal - discount + tax;

        document.getElementById('subtotalAmount').textContent = 'Rs. ' + subtotal.toFixed(2);
        document.getElementById('taxAmount').textContent = 'Rs. ' + tax.toFixed(2);
        document.getElementById('totalAmount').textContent = 'Rs. ' + total.toFixed(2);
    }

    // Quick add item functionality
    function quickAddItem() {
        const quickSelect = document.getElementById('quickAddItem');
        if (quickSelect.value) {
            const itemId = quickSelect.value;
            const itemName = quickSelect.options[quickSelect.selectedIndex].getAttribute('data-name');
            const itemPrice = quickSelect.options[quickSelect.selectedIndex].getAttribute('data-price');

            // Check if item already exists in bill
            const existingSelects = document.querySelectorAll('.item-select');
            let itemExists = false;

            existingSelects.forEach(function(select) {
                if (select.value === itemId) {
                    // Increase quantity instead of adding new row
                    const quantityInput = select.closest('.bill-item').querySelector('.quantity-input');
                    quantityInput.value = parseInt(quantityInput.value) + 1;
                    const index = select.closest('.bill-item').id.split('_')[1];
                    calculateLineTotal(index);
                    itemExists = true;
                }
            });

            if (!itemExists) {
                addBillItem();
                // Set the newly added item
                const lastItem = document.querySelector('.bill-item:last-child .item-select');
                lastItem.value = itemId;
                const lastIndex = lastItem.closest('.bill-item').id.split('_')[1];
                updateItemPrice(lastIndex);
            }

            quickSelect.value = '';
        }
    }

    // Show customer information when selected
    document.getElementById('customerAccountNumber').addEventListener('change', function() {
        const customerId = this.value;
        if (customerId) {
            // In real application, this would make an AJAX call to get customer details
            const customerInfo = document.getElementById('customerInfo');
            const customerDetails = document.getElementById('customerDetails');

            customerDetails.innerHTML = `
            <p><strong>Account:</strong> ${customerId}</p>
            <p><strong>Name:</strong> ${this.options[this.selectedIndex].text.split(' - ')[1]}</p>
            <p class="text-success"><small>Customer loaded successfully</small></p>
        `;
            customerInfo.style.display = 'block';
        } else {
            document.getElementById('customerInfo').style.display = 'none';
        }
    });

    // Auto-generate bill number
    function generateBillNumber() {
        const now = new Date();
        const timestamp = now.getFullYear().toString() +
            (now.getMonth() + 1).toString().padStart(2, '0') +
            now.getDate().toString().padStart(2, '0') +
            now.getHours().toString().padStart(2, '0') +
            now.getMinutes().toString().padStart(2, '0');
        return 'BILL' + timestamp;
    }

    // Form validation
    document.getElementById('billForm').addEventListener('submit', function(e) {
        const customerSelect = document.getElementById('customerAccountNumber');
        const paymentMethod = document.getElementById('paymentMethod');
        const billItems = document.querySelectorAll('.item-select');

        // Check required fields
        if (!customerSelect.value) {
            e.preventDefault();
            alert('Please select a customer');
            customerSelect.focus();
            return false;
        }

        if (!paymentMethod.value) {
            e.preventDefault();
            alert('Please select a payment method');
            paymentMethod.focus();
            return false;
        }

        // Check if at least one item is selected
        let hasValidItem = false;
        billItems.forEach(function(select) {
            if (select.value) {
                hasValidItem = true;
            }
        });

        if (!hasValidItem) {
            e.preventDefault();
            alert('Please add at least one item to the bill');
            return false;
        }

        // Validate quantities and stock
        let stockError = false;
        billItems.forEach(function(select) {
            if (select.value) {
                const quantity = parseInt(select.closest('.bill-item').querySelector('.quantity-input').value);
                const stock = parseInt(select.options[select.selectedIndex].getAttribute('data-stock'));

                if (quantity > stock) {
                    stockError = true;
                    alert(`Insufficient stock for ${select.options[select.selectedIndex].text}. Available: ${stock}, Requested: ${quantity}`);
                }
            }
        });

        if (stockError) {
            e.preventDefault();
            return false;
        }
    });

    // Reset form
    function resetForm() {
        if (confirm('Are you sure you want to reset the form? All data will be lost.')) {
            document.getElementById('billForm').reset();

            // Reset bill items to one empty row
            const container = document.getElementById('billItemsContainer');
            container.innerHTML = `
            <div class="bill-item row mb-3" id="billItem_0">
                <div class="col-md-4">
                    <label class="form-label">Item *</label>
                    <select class="form-select item-select" name="itemId[]" required onchange="updateItemPrice(0)">
                        <option value="">Select Item</option>
                        <c:forEach items="${items}" var="item">
                            <option value="${item.itemId}" data-price="${item.price}" data-stock="${item.stockQuantity}">
                                ${item.itemId} - ${item.itemName} (Rs. ${item.price})
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label">Quantity *</label>
                    <input type="number" class="form-control quantity-input" name="quantity[]" min="1" value="1" required onchange="calculateLineTotal(0)">
                </div>
                <div class="col-md-2">
                    <label class="form-label">Unit Price</label>
                    <input type="number" class="form-control unit-price" name="unitPrice[]" step="0.01" readonly>
                </div>
                <div class="col-md-2">
                    <label class="form-label">Line Total</label>
                    <input type="number" class="form-control line-total" name="lineTotal[]" step="0.01" readonly>
                </div>
                <div class="col-md-2">
                    <label class="form-label">&nbsp;</label>
                    <button type="button" class="btn btn-outline-danger w-100" onclick="removeBillItem(0)">
                        <i class="bi bi-trash"></i>
                    </button>
                </div>
            </div>
        `;

            billItemCounter = 1;
            calculateTotals();
            document.getElementById('customerInfo').style.display = 'none';
        }
    }

    // Preview bill
    function previewBill() {
        const customer = document.getElementById('customerAccountNumber');
        const paymentMethod = document.getElementById('paymentMethod');
        const billNumber = document.getElementById('billNumber');

        if (!customer.value || !paymentMethod.value) {
            alert('Please fill in customer and payment method first');
            return;
        }

        let preview = 'Bill Preview:\n\n';
        preview += `Bill Number: ${billNumber.value}\n`;
        preview += `Customer: ${customer.options[customer.selectedIndex].text}\n`;
        preview += `Payment Method: ${paymentMethod.value}\n\n`;
        preview += 'Items:\n';

        document.querySelectorAll('.bill-item').forEach(function(item) {
            const itemSelect = item.querySelector('.item-select');
            const quantity = item.querySelector('.quantity-input').value;
            const lineTotal = item.querySelector('.line-total').value;

            if (itemSelect.value) {
                preview += `- ${itemSelect.options[itemSelect.selectedIndex].text.split(' - ')[1]} x ${quantity} = Rs. ${lineTotal}\n`;
            }
        });

        preview += `\nTotal: Rs. ${document.getElementById('totalAmount').textContent.replace('Rs. ', '')}`;

        alert(preview);
    }

    // Initialize
    window.addEventListener('load', function() {
        // Auto-generate bill number if empty
        const billNumberInput = document.getElementById('billNumber');
        if (!billNumberInput.value) {
            billNumberInput.value = generateBillNumber();
        }

        // Set today's date
        const billDateInput = document.getElementById('billDate');
        if (!billDateInput.value) {
            const today = new Date();
            const dateString = today.getFullYear() + '-' +
                (today.getMonth() + 1).toString().padStart(2, '0') + '-' +
                today.getDate().toString().padStart(2, '0');
            billDateInput.value = dateString;
        }

        // Focus on customer selection
        document.getElementById('customerAccountNumber').focus();
    });
</script>