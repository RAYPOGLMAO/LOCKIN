package com.lockin.filter;

import com.lockin.exception.DataAccessException;
import com.lockin.model.ApiResponse;
import com.lockin.service.UserService;
import com.lockin.util.Constants;
import com.lockin.util.JsonUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

public class ApiAuthFilter implements Filter {

    private final UserService userService = new UserService();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String header = httpRequest.getHeader("Authorization");
        String token = (header != null && header.startsWith("Bearer ")) ? header.substring(7).trim() : null;

        try {
            Optional<com.lockin.model.User> user = userService.validateToken(token);
            if (user.isEmpty()) {
                JsonUtil.writeJson(httpResponse, 401, ApiResponse.error("Invalid or expired token"));
                return;
            }
            httpRequest.setAttribute(Constants.CURRENT_USER, user.get());
            chain.doFilter(request, response);
        } catch (DataAccessException e) {
            JsonUtil.writeJson(httpResponse, 500, ApiResponse.error("Internal server error"));
        }
    }
}
