package com.lockin.api;

import com.google.gson.JsonParseException;
import com.lockin.exception.AuthenticationException;
import com.lockin.exception.ConflictException;
import com.lockin.exception.DataAccessException;
import com.lockin.exception.NotFoundException;
import com.lockin.exception.ValidationException;
import com.lockin.model.ApiResponse;
import com.lockin.util.JsonUtil;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public abstract class BaseApiServlet extends HttpServlet {

    protected <T> T parseBody(HttpServletRequest request, Class<T> type) throws IOException {
        String body = request.getReader().lines()
                .reduce("", (line, next) -> line.isEmpty() ? next : line + "\n" + next);
        if (body.isBlank()) {
            throw new ValidationException("Request body is required");
        }
        try {
            T value = JsonUtil.fromJson(body, type);
            if (value == null) {
                throw new ValidationException("Request body is required");
            }
            return value;
        } catch (JsonParseException e) {
            throw new ValidationException("Malformed JSON body");
        }
    }

    protected void send(HttpServletResponse response, int status, Object body) throws IOException {
        JsonUtil.writeJson(response, status, body);
    }

    protected void sendError(HttpServletResponse response, int status, String message) throws IOException {
        send(response, status, ApiResponse.error(message));
    }

    protected void handleException(HttpServletResponse response, Exception exception) throws IOException {
        if (exception instanceof ValidationException) {
            sendError(response, 400, exception.getMessage());
        } else if (exception instanceof AuthenticationException) {
            sendError(response, 401, exception.getMessage());
        } else if (exception instanceof ConflictException) {
            sendError(response, 409, exception.getMessage());
        } else if (exception instanceof NotFoundException) {
            sendError(response, 404, exception.getMessage());
        } else if (exception instanceof DataAccessException) {
            log("Database error", exception);
            sendError(response, 500, "Internal server error");
        } else {
            log("Unexpected error", exception);
            sendError(response, 500, "Internal server error");
        }
    }

}
