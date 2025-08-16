<%--
  Created by IntelliJ IDEA.
  User: MOHAMED
  Date: 16/08/2025
  Time: 13:30
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Search Bills" />
<jsp:include page="../common/header.jsp" />

<!-- Page Content -->
<div class="col-md-10 main-content p-4">
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h3">
      <i class="bi bi-search"></i> Search Bills
    </h1>
    <div>
      <a href="${pageContext.request.contextPath}/bills?action=create" class="btn btn-primary me-2">
        <i class="bi bi-plus-circle"></i> New Bill
      </a>
      <a href="${pageContext.request.contextPath}/bills" class="btn btn-secondary">
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
      <form action="${pageContext.request.contextPath}/bills" method="get" id="searchForm">
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
                   placeholder="Bill number, customer name, or account number">
          </div>

          <!-- Status Filter -->
          <div class="col-md-3 mb-3">
            <label for="status" class="form-label">
              <i class="bi bi-toggle-on"></i> Status
            </label>
            <select class="form-select" id="status" name="status">
              <option value="">All Statuses</option>
              <option value="PAID" ${param.status == 'PAID' ? 'selected' : ''}>Paid</option>
              <option value="PENDING" ${param.status == 'PENDING' ? 'selected' : ''}>Pending</option>
              <option value="CANCELLED" ${param.status == 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
            </select>
          </div>

          <!-- Payment Method Filter -->
          <div class="col-md-3 mb-3">
            <label for="paymentMethod" class="form-label">
              <i class="bi bi-credit-card"></i> Payment Method
            </label>
            <select class="form-select" id="paymentMethod" name="paymentMethod">
              <option value="">All Methods</option>
              <option value="CASH" ${param.paymentMethod == 'CASH' ? 'selected' : ''}>Cash</option>
              <option value="CARD" ${param.paymentMethod == 'CARD' ? 'selected' : ''}>Card</option>
            </select>
          </div>

          <!-- Date Range -->
          <div class="col-md-6 mb-3">
            <label class="form-label">
              <i class="bi bi-calendar-range"></i> Date Range
            </label>
            <div class="row">
              <div class="col-6">
                <input type="date"
                       class="form-control"
                       name="startDate"
                       value="${param.startDate}"
                       placeholder="Start date">
              </div>
              <div class="col-6">
                <input type="date"
                       class="form-control"
                       name="endDate"
                       value="${param.endDate}"
                       placeholder="End date">
              </div>
            </div>
          </div>

          <!-- Amount Range -->
          <div class="col-md-6 mb-3">
            <label class="form-label">
              <i class="bi bi-currency-dollar"></i> Amount Range (Rs.)
            </label>
            <div class="row">
              <div class="col-6">
                <input type="number"
                       class="form-control"
                       name="minAmount"
                       value="${param.minAmount}"
                       placeholder="Min amount"
                       min="0"
                       step="0.01">
              </div>
              <div class="col-6">
                <input type="number"
                       class="form-control"
                       name="maxAmount"
                       value="${param.maxAmount}"
                       placeholder="Max amount"
                       min="0"
                       step="0.01">
              </div>
            </div>
          </div>

          <!-- Customer Filter -->
          <div class="col-md-6 mb-3">
            <label for="customer" class="form-label">
              <i class="bi bi-person"></i> Customer
            </label>
            <select class="form-select" id="customer" name="customer">
              <option value="">All Customers</option>
              <c:forEach items="${customers}" var="customer">
                <option value="${customer.accountNumber}" ${param.customer == customer.accountNumber ? 'selected' : ''}>
                    ${customer.accountNumber} - ${customer.fullName}
                </option>
              </c:forEach>
            </select>
          </div>

          <!-- Sort Options -->
          <div class="col-md-6 mb-3">
            <label for="sortBy" class="form-label">
              <i class="bi bi-sort-alpha-down"></i> Sort By
            </label>
            <div class="row">
              <div class="col-8">
                <select class="form-select" id="sortBy" name="sortBy">
                  <option value="billDate" ${param.sortBy == 'billDate' ? 'selected' : ''}>Bill Date</option>
                  <option value="billNumber" ${param.sortBy == 'billNumber' ? 'selected' : ''}>Bill Number</option>
                  <option value="totalAmount" ${param.sortBy == 'totalAmount' ? 'selected' : ''}>Amount</option>
                  <option value="customerName" ${param.sortBy == 'customerName' ? 'selected' : ''}>Customer Name</option>
                </select>
              </div>
              <div class="col-4">
                <select class="form-select" name="sortOrder">
                  <option value="desc" ${param.sortOrder == 'desc' ? 'selected' : ''}>Newest First</option>
                  <option value="asc" ${param.sortOrder == 'asc' ? 'selected' : ''}>Oldest First</option>
                </select>
              </div>
            </div>
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
                <i class="bi bi-search"></i> Search Bills
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
        <span class="badge bg-primary">${searchResultCount} bills found</span>
      </div>
      <div class="card-body">
        <c:choose>
          <c:when test="${not empty bills}">
            <!-- Results Table -->
            <div class="table-responsive">
              <table class="table table-hover">
                <thead class="table-light">
                <tr>
                  <th>Bill Number</th>
                  <th>Customer</th>
                  <th>Amount</th>
                  <th>Payment</th>
                  <th>Date</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${bills}" var="bill">
                  <tr>
                    <td><strong>${bill.billNumber}</strong></td>
                    <td>
                      <div>
                        <strong>${bill.customer.fullName}</strong>
                        <br><small class="text-muted">${bill.customer.accountNumber}</small>
                      </div>
                    </td>
                    <td>
                      <strong class="text-success">Rs. <fmt:formatNumber value="${bill.totalAmount}" pattern="#,##0.00"/></strong>
                    </td>
                    <td>
                                                <span class="badge bg-${bill.paymentMethod == 'CASH' ? 'success' : 'primary'}">
                                                    ${bill.paymentMethod}
                                                </span>
                    </td>
                    <td>
                      <fmt:formatDate value="${bill.billDate}" pattern="MMM dd, yyyy"/>
                    </td>
                    <td>
                                                <span class="badge bg-${bill.status == 'PAID' ? 'success' : bill.status == 'PENDING' ? 'warning' : 'danger'}">
                                                    ${bill.status}
                                                </span>
                    </td>
                    <td>
                      <div class="btn-group btn-group-sm">
                        <a href="${pageContext.request.contextPath}/bills?action=view&id=${bill.billNumber}"
                           class="btn btn-outline-info" title="View">
                          <i class="bi bi-eye"></i>
                        </a>
                        <a href="${pageContext.request.contextPath}/bills?action=print&id=${bill.billNumber}"
                           class="btn btn-outline-success" title="Print" target="_blank">
                          <i class="bi bi-printer"></i>
                        </a>
                      </div>
                    </td>
                  </tr>
                </c:forEach>
                </tbody>
              </table>
            </div>

            <!-- Search Summary -->
            <div class="mt-3 d-flex justify-content-between align-items-center">
              <div>
                <button class="btn btn-outline-success btn-sm" onclick="exportResults('csv')">
                  <i class="bi bi-download"></i> Export CSV
                </button>
                <c:if test="${searchResultCount > 0}">
                                    <span class="ms-3 text-muted">
                                        Total Amount: Rs. <fmt:formatNumber value="${totalSearchAmount}" pattern="#,##0.00"/>
                                    </span>
                </c:if>
              </div>
              <div>
                <small class="text-muted">
                  Showing ${bills.size()} of ${searchResultCount} results
                </small>
              </div>
            </div>
          </c:when>

          <c:otherwise>
            <!-- No Results -->
            <div class="text-center py-5">
              <i class="bi bi-search text-muted" style="font-size: 3rem;"></i>
              <h5 class="text-muted mt-3">No bills found</h5>
              <p class="text-muted">Try adjusting your search criteria or</p>
              <div>
                <button type="button" class="btn btn-outline-primary me-2" onclick="clearSearch()">
                  <i class="bi bi-arrow-clockwise"></i> Clear Search
                </button>
                <a href="${pageContext.request.contextPath}/bills?action=create" class="btn btn-primary">
                  <i class="bi bi-plus-circle"></i> Create New Bill
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
              <li>Use bill numbers for exact matches</li>
              <li>Search by customer name or account number</li>
              <li>Filter by payment method or status</li>
              <li>Use date ranges for specific periods</li>
              <li>Set amount ranges for financial analysis</li>
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
              <button class="btn btn-outline-primary btn-sm" onclick="quickSearch('today')">
                <i class="bi bi-calendar-day"></i> Today's Bills
              </button>
              <button class="btn btn-outline-success btn-sm" onclick="quickSearch('paid')">
                <i class="bi bi-check-circle"></i> Paid Bills
              </button>
              <button class="btn btn-outline-warning btn-sm" onclick="quickSearch('pending')">
                <i class="bi bi-clock"></i> Pending Bills
              </button>
              <button class="btn btn-outline-info btn-sm" onclick="quickSearch('high-amount')">
                <i class="bi bi-currency-dollar"></i> High Value (Rs. 5000+)
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
    window.location.href = '${pageContext.request.contextPath}/bills?action=search';
  }

  // Quick search functions
  function quickSearch(type) {
    const form = document.getElementById('searchForm');
    clearSearch();

    const today = new Date();
    const dateString = today.getFullYear() + '-' +
            (today.getMonth() + 1).toString().padStart(2, '0') + '-' +
            today.getDate().toString().padStart(2, '0');

    switch(type) {
      case 'today':
        document.querySelector('input[name="startDate"]').value = dateString;
        document.querySelector('input[name="endDate"]').value = dateString;
        break;
      case 'paid':
        document.getElementById('status').value = 'PAID';
        break;
      case 'pending':
        document.getElementById('status').value = 'PENDING';
        break;
      case 'high-amount':
        document.querySelector('input[name="minAmount"]').value = '5000';
        break;
    }

    form.submit();
  }

  // Export results
  function exportResults(format) {
    const form = document.getElementById('searchForm');
    const exportForm = form.cloneNode(true);
    exportForm.action = '${pageContext.request.contextPath}/bills';

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
    const startDate = document.querySelector('input[name="startDate"]').value;
    const endDate = document.querySelector('input[name="endDate"]').value;
    const minAmount = document.querySelector('input[name="minAmount"]').value;
    const maxAmount = document.querySelector('input[name="maxAmount"]').value;

    // Date validation
    if (startDate && endDate && new Date(startDate) > new Date(endDate)) {
      e.preventDefault();
      alert('Start date cannot be later than end date');
      return false;
    }

    // Amount validation
    if (minAmount && maxAmount && parseFloat(minAmount) > parseFloat(maxAmount)) {
      e.preventDefault();
      alert('Minimum amount cannot be greater than maximum amount');
      return false;
    }
  });

  // Focus on search term when page loads
  window.addEventListener('load', function() {
    document.getElementById('searchTerm').focus();
  });
</script>