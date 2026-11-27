<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.lockin.model.Habit" %>
<%@ page import="com.lockin.model.User" %>
<%@ page import="com.lockin.util.HtmlUtil" %>
<%
    User user = (User) request.getAttribute("user");
    List<Habit> habits = (List<Habit>) request.getAttribute("habits");
    String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Dashboard - LOCKIN</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<header class="topbar">
    <a class="brand" href="${pageContext.request.contextPath}/dashboard">LOCKIN</a>
    <div class="account">
        <span><%= HtmlUtil.escape(user.getUsername()) %></span>
        <a href="${pageContext.request.contextPath}/logout">Sign out</a>
    </div>
</header>

<main class="dashboard">
    <section class="panel">
        <h2>Add a habit</h2>
        <form method="post" action="${pageContext.request.contextPath}/dashboard" class="add-form">
            <input type="hidden" name="action" value="add">
            <input name="name" placeholder="e.g. Study for 2 hours" maxlength="100" required>
            <input name="description" placeholder="Optional note" maxlength="255">
            <button type="submit">Add</button>
        </form>
    </section>

    <% if (error != null) { %>
    <div class="alert alert-error"><%= HtmlUtil.escape(error) %></div>
    <% } %>

    <section class="panel">
        <h2>Your habits</h2>
        <% if (habits == null || habits.isEmpty()) { %>
        <p class="empty">No habits yet. Add your first one above.</p>
        <% } else { %>
        <ul class="habit-list">
            <% for (Habit habit : habits) { %>
            <li class="habit <%= habit.isCompleted() ? "completed" : "" %>">
                <div class="habit-info">
                    <strong><%= HtmlUtil.escape(habit.getName()) %></strong>
                    <% if (habit.getDescription() != null) { %>
                    <span><%= HtmlUtil.escape(habit.getDescription()) %></span>
                    <% } %>
                </div>
                <div class="habit-actions">
                    <form method="post" action="${pageContext.request.contextPath}/dashboard">
                        <input type="hidden" name="action" value="toggle">
                        <input type="hidden" name="id" value="<%= habit.getId() %>">
                        <button type="submit" class="secondary"><%= habit.isCompleted() ? "Undo" : "Done" %></button>
                    </form>
                    <form method="post" action="${pageContext.request.contextPath}/dashboard">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="id" value="<%= habit.getId() %>">
                        <button type="submit" class="danger">Delete</button>
                    </form>
                </div>
            </li>
            <% } %>
        </ul>
        <% } %>
    </section>
</main>
</body>
</html>
