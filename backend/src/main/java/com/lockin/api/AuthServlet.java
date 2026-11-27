package com.lockin.api;

import com.lockin.exception.AuthenticationException;
import com.lockin.model.ApiResponse;
import com.lockin.model.AuthRequest;
import com.lockin.model.AuthResponse;
import com.lockin.model.PublicUser;
import com.lockin.model.User;
import com.lockin.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

public class AuthServlet extends BaseApiServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getPathInfo();
        if (!"/register".equals(path) && !"/login".equals(path)) {
            sendError(response, 404, "Unknown endpoint");
            return;
        }

        try {
            AuthRequest body = parseBody(request, AuthRequest.class);
            if ("/register".equals(path)) {
                User user = userService.register(body.getUsername(), body.getEmail(), body.getPassword());
                send(response, 201, ApiResponse.ok("Account created", PublicUser.from(user)));
            } else {
                String identifier = body.getUsername() != null ? body.getUsername() : body.getEmail();
                User user = userService.login(identifier, body.getPassword());
                send(response, 200, ApiResponse.ok(new AuthResponse(user.getAuthToken(), PublicUser.from(user))));
            }
        } catch (Exception e) {
            handleException(response, e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!"/me".equals(request.getPathInfo())) {
            sendError(response, 404, "Unknown endpoint");
            return;
        }

        try {
            Optional<User> user = userService.validateToken(extractToken(request));
            if (user.isEmpty()) {
                throw new AuthenticationException("Invalid or expired token");
            }
            send(response, 200, ApiResponse.ok(PublicUser.from(user.get())));
        } catch (Exception e) {
            handleException(response, e);
        }
    }

    private static String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7).trim();
        }
        return null;
    }
}
