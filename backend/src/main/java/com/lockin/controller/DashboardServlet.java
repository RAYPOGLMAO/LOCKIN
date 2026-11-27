package com.lockin.controller;

import com.lockin.exception.ValidationException;
import com.lockin.model.Habit;
import com.lockin.model.User;
import com.lockin.service.HabitService;
import com.lockin.util.Constants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class DashboardServlet extends HttpServlet {

    private final HabitService habitService = new HabitService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        request.setAttribute("user", user);
        request.setAttribute("habits", habitService.list(user.getId()));
        request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        String action = request.getParameter("action");

        try {
            if ("add".equals(action)) {
                habitService.create(user.getId(), request.getParameter("name"), request.getParameter("description"));
            } else if ("toggle".equals(action)) {
                long id = parseId(request.getParameter("id"));
                Habit habit = habitService.get(user.getId(), id);
                habitService.update(user.getId(), id, null, null, !habit.isCompleted());
            } else if ("delete".equals(action)) {
                habitService.delete(user.getId(), parseId(request.getParameter("id")));
            } else {
                throw new ValidationException("Unknown action");
            }
        } catch (RuntimeException e) {
            request.setAttribute("error", e.getMessage());
            doGet(request, response);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }

    private static long parseId(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid habit id");
        }
    }
}
