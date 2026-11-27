<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.lockin.util.HtmlUtil" %>
<%
    String error = (String) request.getAttribute("error");
    String notice = (String) session.getAttribute("notice");
    if (notice != null) {
        session.removeAttribute("notice");
    }
    String username = (String) request.getAttribute("username");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Sign in - LOCKIN</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<main class="auth-card">
    <h1>LOCKIN</h1>
    <p class="subtitle">Sign in to continue</p>

    <% if (error != null) { %>
    <div class="alert alert-error"><%= HtmlUtil.escape(error) %></div>
    <% } %>
    <% if (notice != null) { %>
    <div class="alert alert-success"><%= HtmlUtil.escape(notice) %></div>
    <% } %>

    <form method="post" action="${pageContext.request.contextPath}/login">
        <label for="username">Username</label>
        <input id="username" name="username" value="<%= HtmlUtil.escape(username) %>" autocomplete="username" required>

        <label for="password">Password</label>
        <input id="password" type="password" name="password" autocomplete="current-password" required>

        <button type="submit">Sign in</button>
    </form>

    <p class="footer-link">New here? <a href="${pageContext.request.contextPath}/register">Create an account</a></p>
</main>
</body>
</html>
