package com.lockin.api;

import com.lockin.exception.ValidationException;
import com.lockin.model.ApiResponse;
import com.lockin.model.Habit;
import com.lockin.model.HabitRequest;
import com.lockin.model.User;
import com.lockin.service.HabitService;
import com.lockin.util.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class HabitApiServlet extends BaseApiServlet {

    private final HabitService habitService = new HabitService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            User user = currentUser(request);
            String path = request.getPathInfo();
            if (path == null || "/".equals(path)) {
                List<Habit> habits = habitService.list(user.getId());
                send(response, 200, ApiResponse.ok(habits));
            } else {
                Habit habit = habitService.get(user.getId(), parseId(path));
                send(response, 200, ApiResponse.ok(habit));
            }
        } catch (Exception e) {
            handleException(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            User user = currentUser(request);
            HabitRequest body = parseBody(request, HabitRequest.class);
            Habit habit = habitService.create(user.getId(), body.getName(), body.getDescription());
            send(response, 201, ApiResponse.ok("Habit created", habit));
        } catch (Exception e) {
            handleException(response, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            User user = currentUser(request);
            long id = parseId(request.getPathInfo());
            HabitRequest body = parseBody(request, HabitRequest.class);
            Habit habit = habitService.update(user.getId(), id, body.getName(), body.getDescription(), body.getCompleted());
            send(response, 200, ApiResponse.ok("Habit updated", habit));
        } catch (Exception e) {
            handleException(response, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            User user = currentUser(request);
            habitService.delete(user.getId(), parseId(request.getPathInfo()));
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception e) {
            handleException(response, e);
        }
    }

    private static User currentUser(HttpServletRequest request) {
        User user = (User) request.getAttribute(Constants.CURRENT_USER);
        if (user == null) {
            throw new ValidationException("Authentication required");
        }
        return user;
    }

    private static long parseId(String pathInfo) {
        if (pathInfo == null || pathInfo.length() < 2) {
            throw new ValidationException("Habit id is required");
        }
        try {
            long id = Long.parseLong(pathInfo.substring(1));
            if (id <= 0) {
                throw new ValidationException("Invalid habit id");
            }
            return id;
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid habit id");
        }
    }
}
