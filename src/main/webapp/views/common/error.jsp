<%--
  Created by IntelliJ IDEA.
  User: MOHAMED
  Date: 16/08/2025
  Time: 11:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isErrorPage="true" %>
<c:set var="pageTitle" value="Error" />
<jsp:include page="header.jsp" />

<div class="container-fluid">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="text-center">
                <div class="error-icon mb-4">
                    <i class="bi bi-exclamation-triangle-fill text-danger" style="font-size: 4rem;"></i>
                </div>

                <h1 class="display-4 text-danger">Oops!</h1>
                <h2 class="h4 mb-4">Something went wrong</h2>

                <div class="alert alert-danger" role="alert">
                    <strong>Error Details:</strong><br>
                    <c:choose>
                        <c:when test="${not empty errorMessage}">
                            ${errorMessage}
                        </c:when>
                        <c:when test="${not empty exception}">
                            ${exception.message}
                        </c:when>
                        <c:otherwise>
                            An unexpected error occurred. Please try again later.
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="mt-4">
                    <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary">
                        <i class="bi bi-house"></i> Go to Dashboard
                    </a>
                    <button onclick="history.back()" class="btn btn-secondary">
                        <i class="bi bi-arrow-left"></i> Go Back
                    </button>
                </div>

                <div class="mt-4">
                    <small class="text-muted">
                        If this problem persists, please contact the system administrator.
                    </small>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp" />
