<%--
  Created by IntelliJ IDEA.
  User: MOHAMED
  Date: 16/08/2025
  Time: 11:10
  To change this template use File | Settings | File Templates.
--%>
<!-- Page Content Ends Here -->
</div>
</div>
</div>

<!-- Footer -->
<footer class="bg-light text-center py-3 mt-5">
    <div class="container">
        <p class="text-muted mb-0">
            &copy; 2024 Pahana Edu Billing System.
            <span class="text-primary">Advanced Programming Project</span>
        </p>
    </div>
</footer>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<!-- Common JavaScript Functions -->
<script>
    // Auto-hide alerts after 5 seconds
    setTimeout(function() {
        var alerts = document.querySelectorAll('.alert');
        alerts.forEach(function(alert) {
            var bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        });
    }, 5000);

    // Confirmation for delete actions
    function confirmDelete(itemName) {
        return confirm('Are you sure you want to delete ' + itemName + '?');
    }

    // Simple form validation
    function validateForm(formId) {
        var form = document.getElementById(formId);
        var inputs = form.querySelectorAll('input[required], select[required]');
        var isValid = true;

        inputs.forEach(function(input) {
            if (!input.value.trim()) {
                input.classList.add('is-invalid');
                isValid = false;
            } else {
                input.classList.remove('is-invalid');
            }
        });

        return isValid;
    }

    // Search functionality
    function performSearch(searchUrl, searchTerm) {
        if (searchTerm.length < 2) return;

        fetch(searchUrl + '?action=ajax&q=' + encodeURIComponent(searchTerm))
            .then(response => response.json())
            .then(data => {
                // Handle search results (to be implemented per page)
                console.log('Search results:', data);
            })
            .catch(error => {
                console.error('Search error:', error);
            });
    }

    // Format currency
    function formatCurrency(amount) {
        return 'Rs. ' + parseFloat(amount).toFixed(2);
    }
</script>

<!-- Page-specific scripts can be added here -->
<c:if test="${not empty pageScript}">
    <script src="${pageContext.request.contextPath}/js/${pageScript}"></script>
</c:if>

</body>
</html>
