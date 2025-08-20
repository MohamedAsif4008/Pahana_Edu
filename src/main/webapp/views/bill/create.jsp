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
                                                    ${customer.accountNumber} - ${customer.name}
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
                                    <select class="form-select item-select" name="itemId[]" required>
                                        <option value="">Select Item</option>
                                        <c:forEach items="${items}" var="item">
                                            <option value="${item.itemId}"
                                                    data-price="${item.price}"
                                                    data-stock="${item.stockQuantity}"
                                                ${param.itemId == item.itemId ? 'selected' : ''}>
                                                    ${item.itemId} - ${item.name} (Rs. ${item.price})
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
                                           required>
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
                                               step="0.01">
                                    </div>
                                </td>
                            </tr>
                            <tr class="table-primary">
                                <td><strong>Total Amount:</strong></td>
                                <td class="text-end"><strong id="totalAmount">Rs. 0.00</strong></td>
                            </tr>
                        </table>

                        <hr>

                      
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
            <select class="form-select item-select" name="itemId[]" required>
                <option value="">Select Item</option>
                <c:forEach items="${items}" var="item">
                    <option value="${item.itemId}" data-price="${item.price}" data-stock="${item.stockQuantity}">
                        ${item.itemId} - ${item.name} (Rs. ${item.price})
                    </option>
                </c:forEach>
            </select>
        </div>
        <div class="col-md-2">
            <label class="form-label">Quantity *</label>
            <input type="number" class="form-control quantity-input" name="quantity[]" min="1" value="1" required>
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
        
        // Add event listeners to the newly created item using reliable approach
        const newItemSelect = newItem.querySelector('.item-select');
        const newQuantityInput = newItem.querySelector('.quantity-input');
        
        if (newItemSelect) {
            newItemSelect.addEventListener('change', function() {
                console.log('New item select changed for index: ' + billItemCounter);
                updateItemPrice(billItemCounter);
            });
        }
        
        if (newQuantityInput) {
            newQuantityInput.addEventListener('change', function() {
                console.log('New quantity input changed for index: ' + billItemCounter);
                calculateLineTotal(billItemCounter);
            });
        }
        
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
        console.log('=== updateItemPrice() called ===');
        console.log('  Index: ' + index);
        console.log('  Timestamp: ' + new Date().toISOString());
        
        // Debug: Check all bill items and their IDs
        const allBillItems = document.querySelectorAll('.bill-item');
        console.log('  All bill items found:', allBillItems.length);
        allBillItems.forEach(function(item, i) {
            console.log(`  Bill item ${i}: ID="${item.id}", classes="${item.className}"`);
        });
        
        // Try multiple approaches to find the bill item
        let billItem = document.getElementById(`billItem_${index}`);
        console.log('  getElementById result:', billItem);
        
        if (!billItem) {
            // Try alternative approach
            billItem = document.querySelector(`[id="billItem_${index}"]`);
            console.log('  querySelector result:', billItem);
        }
        
        if (!billItem) {
            // Try finding by index in the collection
            const billItems = document.querySelectorAll('.bill-item');
            if (billItems[index]) {
                billItem = billItems[index];
                console.log('  Found by index in collection:', billItem);
            }
        }
        
        console.log('  Final bill item found: ' + (billItem !== null));
        
        if (!billItem) {
            console.error('  Bill item not found for index: ' + index);
            console.log('  Available bill items:', document.querySelectorAll('.bill-item').length);
            console.log('  Bill item IDs:', Array.from(document.querySelectorAll('.bill-item')).map(el => el.id));
            return;
        }
        
        const itemSelect = billItem.querySelector('.item-select');
        const unitPriceInput = billItem.querySelector('.unit-price');
        
        console.log('  Item select found: ' + (itemSelect !== null));
        console.log('  Unit price input found: ' + (unitPriceInput !== null));
        
        if (itemSelect) {
            console.log('  Item select element:', itemSelect);
            console.log('  Item select value: "' + itemSelect.value + '"');
            console.log('  Item select selectedIndex: ' + itemSelect.selectedIndex);
            console.log('  Item select options length: ' + itemSelect.options.length);
            
            if (itemSelect.selectedIndex > 0) {
                const selectedOption = itemSelect.options[itemSelect.selectedIndex];
                console.log('  Selected option element:', selectedOption);
                console.log('  Selected option text: "' + selectedOption.text + '"');
                console.log('  Selected option value: "' + selectedOption.value + '"');
                
                const price = selectedOption.getAttribute('data-price');
                console.log('  Price from data attribute: "' + price + '"');
                
                if (unitPriceInput) {
                    unitPriceInput.value = price;
                    console.log('  Unit price set to: "' + unitPriceInput.value + '"');
                    
                    // Force trigger the change event
                    const event = new Event('change', { bubbles: true });
                    unitPriceInput.dispatchEvent(event);
                    
                    calculateLineTotal(index);
                } else {
                    console.error('  Unit price input not found!');
                }
            } else {
                console.log('  No item selected (selectedIndex <= 0)');
                if (unitPriceInput) {
                    unitPriceInput.value = '';
                    console.log('  Unit price cleared');
                    calculateLineTotal(index);
                }
            }
        } else {
            console.error('  Item select not found!');
        }
    }

    // Calculate line total for a specific item
    function calculateLineTotal(index) {
        console.log('=== calculateLineTotal() called ===');
        console.log('  Index: ' + index);
        console.log('  Timestamp: ' + new Date().toISOString());
        
        // Try multiple approaches to find the bill item
        let billItem = document.getElementById(`billItem_${index}`);
        console.log('  getElementById result:', billItem);
        
        if (!billItem) {
            // Try alternative approach
            billItem = document.querySelector(`[id="billItem_${index}"]`);
            console.log('  querySelector result:', billItem);
        }
        
        if (!billItem) {
            // Try finding by index in the collection
            const billItems = document.querySelectorAll('.bill-item');
            if (billItems[index]) {
                billItem = billItems[index];
                console.log('  Found by index in collection:', billItem);
            }
        }
        
        console.log('  Final bill item found: ' + (billItem !== null));
        
        if (!billItem) {
            console.error('  Bill item not found for index: ' + index);
            return;
        }
        
        const quantityInput = billItem.querySelector('.quantity-input');
        const unitPriceInput = billItem.querySelector('.unit-price');
        const lineTotalInput = billItem.querySelector('.line-total');
        
        console.log('  Quantity input found: ' + (quantityInput !== null));
        console.log('  Unit price input found: ' + (unitPriceInput !== null));
        console.log('  Line total input found: ' + (lineTotalInput !== null));

        if (quantityInput && unitPriceInput && lineTotalInput) {
            const quantity = parseFloat(quantityInput.value) || 0;
            const unitPrice = parseFloat(unitPriceInput.value) || 0;
            const lineTotal = quantity * unitPrice;
            
            console.log('  Quantity: ' + quantity);
            console.log('  Unit Price: ' + unitPrice);
            console.log('  Line Total: ' + lineTotal);

            lineTotalInput.value = lineTotal.toFixed(2);
            console.log('  Line total set to: "' + lineTotalInput.value + '"');
            
            // Force trigger the change event
            const event = new Event('change', { bubbles: true });
            lineTotalInput.dispatchEvent(event);
            
            calculateTotals();
        } else {
            console.error('  Could not find required elements for line total calculation');
            console.log('  Quantity input:', quantityInput);
            console.log('  Unit price input:', unitPriceInput);
            console.log('  Line total input:', lineTotalInput);
        }
    }

    // Calculate bill totals
    function calculateTotals() {
        console.log('=== calculateTotals() called ===');
        console.log('  Timestamp: ' + new Date().toISOString());
        
        let subtotal = 0;
        const lineTotalInputs = document.querySelectorAll('.line-total');
        
        console.log('  Found ' + lineTotalInputs.length + ' line total inputs');

        // Sum all line totals
        lineTotalInputs.forEach(function(input, index) {
            const value = parseFloat(input.value) || 0;
            console.log('  Line total ' + index + ': ' + value);
            subtotal += value;
        });

        const discount = parseFloat(document.getElementById('discountAmount').value) || 0;
        const tax = 0; // No tax for now
        const total = subtotal - discount + tax;

        console.log('  Subtotal: ' + subtotal);
        console.log('  Discount: ' + discount);
        console.log('  Tax: ' + tax);
        console.log('  Total: ' + total);

        document.getElementById('subtotalAmount').textContent = 'Rs. ' + subtotal.toFixed(2);
        document.getElementById('taxAmount').textContent = 'Rs. ' + tax.toFixed(2);
        document.getElementById('totalAmount').textContent = 'Rs. ' + total.toFixed(2);
        
        console.log('  Summary updated');
    }

    // Quick add item functionality
    function quickAddItem() {
        const quickSelect = document.getElementById('quickAddItem');
        if (quickSelect.value) {
            const itemId = quickSelect.value;
            const name = quickSelect.options[quickSelect.selectedIndex].getAttribute('data-name');
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
            <p><strong>Name:</strong> \${this.options[this.selectedIndex].text.split(' - ')[1]}</p>
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
                    <select class="form-select item-select" name="itemId[]" required>
                        <option value="">Select Item</option>
                        <c:forEach items="${items}" var="item">
                            <option value="${item.itemId}" data-price="${item.price}" data-stock="${item.stockQuantity}">
                                ${item.itemId} - ${item.name} (Rs. ${item.price})
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label">Quantity *</label>
                    <input type="number" class="form-control quantity-input" name="quantity[]" min="1" value="1" required>
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

            // Re-attach event listeners to the reset first item
            const firstItemSelect = document.querySelector('#billItem_0 .item-select');
            const firstQuantityInput = document.querySelector('#billItem_0 .quantity-input');
            
            if (firstItemSelect) {
                firstItemSelect.addEventListener('change', function() {
                    updateItemPrice(0);
                });
            }
            
            if (firstQuantityInput) {
                firstQuantityInput.addEventListener('change', function() {
                    calculateLineTotal(0);
                });
            }

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
        console.log('=== Page loaded, initializing... ===');
        console.log('  Timestamp: ' + new Date().toISOString());
        
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

        // Debug: Check all bill items
        const allBillItems = document.querySelectorAll('.bill-item');
        console.log('  Found ' + allBillItems.length + ' bill items on page load');
        allBillItems.forEach(function(item, index) {
            console.log('  Bill item ' + index + ' ID: ' + item.id);
            console.log('  Bill item ' + index + ' classes: ' + item.className);
        });

        // Use event-based approach for the first item
        const firstItemSelect = document.querySelector('#billItem_0 .item-select');
        const firstQuantityInput = document.querySelector('#billItem_0 .quantity-input');
        
        console.log('First item select found:', firstItemSelect);
        console.log('First quantity input found:', firstQuantityInput);
        
        if (firstItemSelect) {
            console.log('  First item select element:', firstItemSelect);
            
            // Remove any existing event listeners
            firstItemSelect.removeAttribute('onchange');
            
            // Add new event listener using event-based approach
            firstItemSelect.addEventListener('change', function(e) {
                console.log('=== First item select change event triggered ===');
                console.log('  Event:', e);
                console.log('  Target:', e.target);
                console.log('  Value:', e.target.value);
                console.log('  Selected index:', e.target.selectedIndex);
                updateItemPriceFromEvent(e);
            });
            console.log('First item select event handler attached');
        } else {
            console.error('  First item select not found!');
        }

        if (firstQuantityInput) {
            console.log('  First quantity input element:', firstQuantityInput);
            
            // Remove any existing event listeners
            firstQuantityInput.removeAttribute('onchange');
            
            // Add new event listener using event-based approach
            firstQuantityInput.addEventListener('change', function(e) {
                console.log('=== First quantity input change event triggered ===');
                console.log('  Event:', e);
                console.log('  Target:', e.target);
                console.log('  Value:', e.target.value);
                calculateLineTotalFromEvent(e);
            });
            console.log('First quantity input event handler attached');
        } else {
            console.error('  First quantity input not found!');
        }

        // Fix discount input event handling
        const discountInput = document.getElementById('discountAmount');
        if (discountInput) {
            console.log('  Discount input found:', discountInput);
            
            // Remove inline onchange attribute
            discountInput.removeAttribute('onchange');
            
            // Add proper event listeners for discount
            discountInput.addEventListener('change', function(e) {
                console.log('=== Discount input change event triggered ===');
                console.log('  Event:', e);
                console.log('  Target:', e.target);
                console.log('  Value:', e.target.value);
                calculateTotals();
            });
            
            discountInput.addEventListener('input', function(e) {
                console.log('=== Discount input input event triggered ===');
                console.log('  Event:', e);
                console.log('  Target:', e.target);
                console.log('  Value:', e.target.value);
                calculateTotals();
            });
            
            discountInput.addEventListener('blur', function(e) {
                console.log('=== Discount input blur event triggered ===');
                console.log('  Event:', e);
                console.log('  Target:', e.target);
                console.log('  Value:', e.target.value);
                calculateTotals();
            });
            
            console.log('Discount input event handlers attached');
        } else {
            console.error('  Discount input not found!');
        }

        // Focus on customer selection
        document.getElementById('customerAccountNumber').focus();
        console.log('=== Initialization complete ===');
    });
</script>

<script>
// Add this at the end of your script

// Test function - call this from browser console
function testFirstItem() {
    console.log('=== Manual test of first item ===');
    
    const firstItemSelect = document.querySelector('#billItem_0 .item-select');
    const firstQuantityInput = document.querySelector('#billItem_0 .quantity-input');
    
    console.log('First item select:', firstItemSelect);
    console.log('First quantity input:', firstQuantityInput);
    
    if (firstItemSelect && firstItemSelect.options.length > 1) {
        console.log('Setting first item to first available option...');
        firstItemSelect.selectedIndex = 1; // Select first actual item (index 0 is "Select Item")
        
        // Trigger change event
        const event = new Event('change', { bubbles: true });
        firstItemSelect.dispatchEvent(event);
        
        console.log('Change event dispatched');
    } else {
        console.error('Cannot test - no items available or elements not found');
    }
}

// Also add a function to check all data attributes
function checkItemData() {
    console.log('=== Checking item data attributes ===');
    
    const firstItemSelect = document.querySelector('#billItem_0 .item-select');
    if (firstItemSelect) {
        console.log('First item select options:');
        for (let i = 0; i < firstItemSelect.options.length; i++) {
            const option = firstItemSelect.options[i];
            console.log(`  Option ${i}: "${option.text}" - value: "${option.value}" - data-price: "${option.getAttribute('data-price')}"`);
        }
    } else {
        console.error('First item select not found');
    }
}
</script>

<script>
// Add this test function at the end of your script
function debugBillItems() {
    console.log('=== Debug Bill Items ===');
    
    // Check all bill items
    const allBillItems = document.querySelectorAll('.bill-item');
    console.log('Total bill items found:', allBillItems.length);
    
    allBillItems.forEach(function(item, index) {
        console.log(`Bill item ${index}:`);
        console.log('  ID:', item.id);
        console.log('  Classes:', item.className);
        console.log('  Element:', item);
        
        // Check if getElementById works
        const byId = document.getElementById(item.id);
        console.log('  getElementById result:', byId);
        
        // Check child elements
        const itemSelect = item.querySelector('.item-select');
        const quantityInput = item.querySelector('.quantity-input');
        const unitPriceInput = item.querySelector('.unit-price');
        const lineTotalInput = item.querySelector('.line-total');
        
        console.log('  Item select:', itemSelect);
        console.log('  Quantity input:', quantityInput);
        console.log('  Unit price input:', unitPriceInput);
        console.log('  Line total input:', lineTotalInput);
    });
    
    // Try to find by ID specifically
    const billItem0 = document.getElementById('billItem_0');
    console.log('getElementById("billItem_0"):', billItem0);
    
    // Try alternative selectors
    const billItem0Alt = document.querySelector('[id="billItem_0"]');
    console.log('querySelector("[id=\\"billItem_0\\"]"):', billItem0Alt);
    
    const billItem0Class = document.querySelector('.bill-item');
    console.log('querySelector(".bill-item"):', billItem0Class);
}
</script>

<script>
// Alternative approach: Use event target instead of index
function updateItemPriceFromEvent(event) {
    console.log('=== updateItemPriceFromEvent() called ===');
    console.log('  Event target:', event.target);
    
    const itemSelect = event.target;
    const billItem = itemSelect.closest('.bill-item');
    const unitPriceInput = billItem.querySelector('.unit-price');
    
    console.log('  Bill item found:', billItem);
    console.log('  Unit price input found:', unitPriceInput);
    
    if (itemSelect && unitPriceInput) {
        console.log('  Selected value: "' + itemSelect.value + '"');
        console.log('  Selected index: ' + itemSelect.selectedIndex);
        
        if (itemSelect.selectedIndex > 0) {
            const selectedOption = itemSelect.options[itemSelect.selectedIndex];
            const price = selectedOption.getAttribute('data-price');
            console.log('  Price from data attribute: "' + price + '"');
            
            unitPriceInput.value = price;
            console.log('  Unit price set to: "' + unitPriceInput.value + '"');
            
            // Find the index for calculateLineTotal
            const billItems = document.querySelectorAll('.bill-item');
            const index = Array.from(billItems).indexOf(billItem);
            console.log('  Calculated index: ' + index);
            
            calculateLineTotal(index);
        } else {
            unitPriceInput.value = '';
            console.log('  Unit price cleared');
            
            const billItems = document.querySelectorAll('.bill-item');
            const index = Array.from(billItems).indexOf(billItem);
            calculateLineTotal(index);
        }
    }
}

function calculateLineTotalFromEvent(event) {
    console.log('=== calculateLineTotalFromEvent() called ===');
    console.log('  Event target:', event.target);
    
    const quantityInput = event.target;
    const billItem = quantityInput.closest('.bill-item');
    const unitPriceInput = billItem.querySelector('.unit-price');
    const lineTotalInput = billItem.querySelector('.line-total');
    
    console.log('  Bill item found:', billItem);
    console.log('  Unit price input found:', unitPriceInput);
    console.log('  Line total input found:', lineTotalInput);
    
    if (quantityInput && unitPriceInput && lineTotalInput) {
        const quantity = parseFloat(quantityInput.value) || 0;
        const unitPrice = parseFloat(unitPriceInput.value) || 0;
        const lineTotal = quantity * unitPrice;
        
        console.log('  Quantity: ' + quantity);
        console.log('  Unit Price: ' + unitPrice);
        console.log('  Line Total: ' + lineTotal);
        
        lineTotalInput.value = lineTotal.toFixed(2);
        console.log('  Line total set to: "' + lineTotalInput.value + '"');
        
        calculateTotals();
    }
}
</script>

<script>
// Add this test function at the end of your script
function testDiscount() {
    console.log('=== Testing Discount Calculation ===');
    
    const discountInput = document.getElementById('discountAmount');
    const subtotalElement = document.getElementById('subtotalAmount');
    const totalElement = document.getElementById('totalAmount');
    
    console.log('Discount input:', discountInput);
    console.log('Subtotal element:', subtotalElement);
    console.log('Total element:', totalElement);
    
    if (discountInput) {
        console.log('Current discount value:', discountInput.value);
        console.log('Current subtotal text:', subtotalElement.textContent);
        console.log('Current total text:', totalElement.textContent);
        
        // Test setting a discount
        discountInput.value = '100';
        console.log('Set discount to 100');
        
        // Trigger the calculation
        calculateTotals();
        
        console.log('After calculation:');
        console.log('  Discount value:', discountInput.value);
        console.log('  Subtotal text:', subtotalElement.textContent);
        console.log('  Total text:', totalElement.textContent);
    } else {
        console.error('Discount input not found');
    }
}
</script>