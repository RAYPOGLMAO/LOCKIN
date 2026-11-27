<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.lockin.util.HtmlUtil" %>
<%
    String error = (String) request.getAttribute("error");
    String username = (String) request.getAttribute("username");
    String email = (String) request.getAttribute("email");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Create account - LOCKIN</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<main class="auth-card">
    <h1>LOCKIN</h1>
    <p class="subtitle">Create your account</p>

    <% if (error != null) { %>
    <div class="alert alert-error"><%= HtmlUtil.escape(error) %></div>
    <% } %>

    <form method="post" action="${pageContext.request.contextPath}/register">
        <label for="username">Username</label>
        <input id="username" name="username" value="<%= HtmlUtil.escape(username) %>" autocomplete="username" required>

        <label for="email">Email</label>
        <input id="email" type="email" name="email" value="<%= HtmlUtil.escape(email) %>" autocomplete="email" required>

        <label for="password">Password</label>
        <input id="password" type="password" name="password" autocomplete="new-password" minlength="8" required>

        <button type="submit">Create account</button>
    </form>

    <p class="footer-link">Already have an account? <a href="${pageContext.request.contextPath}/login">Sign in</a></p>
</main>
</body>
</html>
