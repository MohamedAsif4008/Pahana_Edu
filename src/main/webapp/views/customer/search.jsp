<%--
  Created by IntelliJ IDEA.
  User: MOHAMED
  Date: 16/08/2025
  Time: 12:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Search Customers" />
<jsp:include page="../common/header.jsp" />

<!-- Page Content -->
<div class="col-md-10 main-content p-4">
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h3">
      <i class="bi bi-search"></i> Search Customers
    </h1>
    <div>
      <a href="${pageContext.request.contextPath}/customers?action=create" class="btn btn-primary me-2">
        <i class="bi bi-person-plus"></i> Add Customer
      </a>
      <a href="${pageContext.request.contextPath}/customers" class="btn btn-secondary">
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
      <form action="${pageContext.request.contextPath}/customers" method="get" id="searchForm">
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
                   placeholder="Name, account number, phone, or email">
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

          <!-- City Filter -->
          <div class="col-md-3 mb-3">
            <label for="city" class="form-label">
              <i class="bi bi-building"></i> City
            </label>
            <select class="form-select" id="city" name="city">
              <option value="">All Cities</option>
              <option value="Colombo" ${param.city == 'Colombo' ? 'selected' : ''}>Colombo</option>
              <option value="Kandy" ${param.city == 'Kandy' ? 'selected' : ''}>Kandy</option>
              <option value="Galle" ${param.city == 'Galle' ? 'selected' : ''}>Galle</option>
              <option value="Jaffna" ${param.city == 'Jaffna' ? 'selected' : ''}>Jaffna</option>
              <option value="Negombo" ${param.city == 'Negombo' ? 'selected' : ''}>Negombo</option>
            </select>
          </div>

          <!-- Units Range -->
          <div class="col-md-6 mb-3">
            <label class="form-label">
              <i class="bi bi-graph-up"></i> Units Consumed Range
            </label>
            <div class="row">
              <div class="col-6">
                <input type="number"
                       class="form-control"
                       name="minUnits"
                       value="${param.minUnits}"
                       placeholder="Min units"
                       min="0">
              </div>
              <div class="col-6">
                <input type="number"
                       class="form-control"
                       name="maxUnits"
                       value="${param.maxUnits}"
                       placeholder="Max units"
                       min="0">
              </div>
            </div>
          </div>

          <!-- Sort Options -->
          <div class="col-md-6 mb-3">
            <label for="sortBy" class="form-label">
              <i class="bi bi-sort-alpha-down"></i> Sort By
            </label>
            <div class="row">
              <div class="col-8">
                <select class="form-select" id="sortBy" name="sortBy">
                  <option value="fullName" ${param.sortBy == 'fullName' ? 'selected' : ''}>Name</option>
                  <option value="accountNumber" ${param.sortBy == 'accountNumber' ? 'selected' : ''}>Account Number</option>
                  <option value="createdDate" ${param.sortBy == 'createdDate' ? 'selected' : ''}>Created Date</option>
                  <option value="unitsConsumed" ${param.sortBy == 'unitsConsumed' ? 'selected' : ''}>Units Consumed</option>
                </select>
              </div>
              <div class="col-4">
                <select class="form-select" name="sortOrder">
                  <option value="asc" ${param.sortOrder == 'asc' ? 'selected' : ''}>Ascending</option>
                  <option value="desc" ${param.sortOrder == 'desc' ? 'selected' : ''}>Descending</option>
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
              <div>
                <button type="button" class="btn btn-outline-info me-2" onclick="saveSearch()">
                  <i class="bi bi-bookmark"></i> Save Search
                </button>
                <button type="submit" class="btn btn-primary">
                  <i class="bi bi-search"></i> Search Customers
                </button>
              </div>
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
        <span class="badge bg-primary">${searchResultCount} customers found</span>
      </div>
      <div class="card-body">
        <c:choose>
          <c:when test="${not empty customers}">
            <!-- Results Table -->
            <div class="table-responsive">
              <table class="table table-hover">
                <thead class="table-light">
                <tr>
                  <th>Account Number</th>
                  <th>Name</th>
                  <th>Phone</th>
                  <th>City</th>
                  <th>Units</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${customers}" var="customer">
                  <tr>
                    <td>
                      <strong>${customer.accountNumber}</strong>
                    </td>
                    <td>
                      <div>
                        <strong>${customer.fullName}</strong>
                        <c:if test="${not empty customer.email}">
                          <br><small class="text-muted">${customer.email}</small>
                        </c:if>
                      </div>
                    </td>
                    <td>${customer.phoneNumber}</td>
                    <td>${customer.city}</td>
                    <td>
                      <span class="badge bg-info">${customer.unitsConsumed}</span>
                    </td>
                    <td>
                                                <span class="badge bg-${customer.active ? 'success' : 'danger'}">
                                                    ${customer.active ? 'Active' : 'Inactive'}
                                                </span>
                    </td>
                    <td>
                      <div class="btn-group btn-group-sm">
                        <a href="${pageContext.request.contextPath}/customers?action=view&id=${customer.accountNumber}"
                           class="btn btn-outline-info" title="View">
                          <i class="bi bi-eye"></i>
                        </a>
                        <a href="${pageContext.request.contextPath}/customers?action=edit&id=${customer.accountNumber}"
                           class="btn btn-outline-warning" title="Edit">
                          <i class="bi bi-pencil"></i>
                        </a>
                        <a href="${pageContext.request.contextPath}/bills?action=create&customerId=${customer.accountNumber}"
                           class="btn btn-outline-success" title="Create Bill">
                          <i class="bi bi-receipt"></i>
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
                <button class="btn btn-outline-info btn-sm" onclick="exportResults('pdf')">
                  <i class="bi bi-file-pdf"></i> Export PDF
                </button>
              </div>
              <div>
                <small class="text-muted">
                  Showing ${customers.size()} of ${searchResultCount} results
                </small>
              </div>
            </div>
          </c:when>

          <c:otherwise>
            <!-- No Results -->
            <div class="text-center py-5">
              <i class="bi bi-search text-muted" style="font-size: 3rem;"></i>
              <h5 class="text-muted mt-3">No customers found</h5>
              <p class="text-muted">Try adjusting your search criteria or</p>
              <div>
                <button type="button" class="btn btn-outline-primary me-2" onclick="clearSearch()">
                  <i class="bi bi-arrow-clockwise"></i> Clear Search
                </button>
                <a href="${pageContext.request.contextPath}/customers?action=create" class="btn btn-primary">
                  <i class="bi bi-person-plus"></i> Add New Customer
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
              <li>Use partial names or account numbers</li>
              <li>Search by phone number or email</li>
              <li>Filter by city or status</li>
              <li>Use unit ranges for billing analysis</li>
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
              <button class="btn btn-outline-primary btn-sm" onclick="quickSearch('active')">
                <i class="bi bi-check-circle"></i> Active Customers
              </button>
              <button class="btn btn-outline-warning btn-sm" onclick="quickSearch('inactive')">
                <i class="bi bi-x-circle"></i> Inactive Customers
              </button>
              <button class="btn btn-outline-info btn-sm" onclick="quickSearch('high-usage')">
                <i class="bi bi-graph-up"></i> High Usage (50+ units)
              </button>
              <button class="btn btn-outline-success btn-sm" onclick="quickSearch('colombo')">
                <i class="bi bi-building"></i> Colombo Customers
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
    window.location.href = '${pageContext.request.contextPath}/customers?action=search';
  }

  // Quick search functions
  function quickSearch(type) {
    const form = document.getElementById('searchForm');
    clearSearch();

    switch(type) {
      case 'active':
        document.getElementById('status').value = 'active';
        break;
      case 'inactive':
        document.getElementById('status').value = 'inactive';
        break;
      case 'high-usage':
        document.querySelector('input[name="minUnits"]').value = '50';
        break;
      case 'colombo':
        document.getElementById('city').value = 'Colombo';
        break;
    }

    form.submit();
  }

  // Save search functionality
  function saveSearch() {
    const formData = new FormData(document.getElementById('searchForm'));
    const searchParams = new URLSearchParams(formData);
    const searchUrl = window.location.pathname + '?' + searchParams.toString();

    // Simple bookmark functionality
    if (confirm('Save this search as a bookmark?')) {
      // In a real application, this would save to user preferences
      localStorage.setItem('savedCustomerSearch', searchUrl);
      alert('Search saved! You can access it from your bookmarks.');
    }
  }

  // Export results
  function exportResults(format) {
    const form = document.getElementById('searchForm');
    const exportForm = form.cloneNode(true);
    exportForm.action = '${pageContext.request.contextPath}/customers';

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

  // Auto-suggest functionality for search term
  document.getElementById('searchTerm').addEventListener('input', function() {
    const searchTerm = this.value;
    if (searchTerm.length >= 2) {
      // In a real application, this would make an AJAX call for suggestions
      // For now, we'll just show a simple indicator
      this.style.backgroundColor = '#e3f2fd';
    } else {
      this.style.backgroundColor = '';
    }
  });

  // Form validation
  document.getElementById('searchForm').addEventListener('submit', function(e) {
    const minUnits = document.querySelector('input[name="minUnits"]').value;
    const maxUnits = document.querySelector('input[name="maxUnits"]').value;

    if (minUnits && maxUnits && parseInt(minUnits) > parseInt(maxUnits)) {
      e.preventDefault();
      alert('Minimum units cannot be greater than maximum units');
      return false;
    }
  });

  // Focus on search term when page loads
  window.addEventListener('load', function() {
    document.getElementById('searchTerm').focus();
  });
</script>
