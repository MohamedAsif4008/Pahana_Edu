<%--
  Created by IntelliJ IDEA.
  User: MOHAMED
  Date: 16/08/2025
  Time: 11:00
  Landing page that redirects to appropriate location
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Check if user is already logged in
    Object currentUser = session.getAttribute("currentUser");

    if (currentUser != null) {
        // User is logged in, redirect to dashboard
        response.sendRedirect(request.getContextPath() + "/dashboard");
    } else {
        // User is not logged in, redirect to login page
        response.sendRedirect(request.getContextPath() + "/login");
    }
%>