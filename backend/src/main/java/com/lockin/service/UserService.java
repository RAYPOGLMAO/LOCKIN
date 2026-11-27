package com.lockin.service;

import com.lockin.config.AppConfig;
import com.lockin.dao.UserDAO;
import com.lockin.dao.impl.UserDAOImpl;
import com.lockin.exception.AuthenticationException;
import com.lockin.exception.ConflictException;
import com.lockin.exception.DataAccessException;
import com.lockin.exception.ValidationException;
import com.lockin.model.User;
import com.lockin.util.PasswordUtil;
import com.lockin.util.TokenUtil;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

public class UserService {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]{3,30}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final UserDAO userDAO;

    public UserService() {
        this(new UserDAOImpl());
    }

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User register(String username, String email, String password) {
        String normalizedUsername = normalize(username);
        String normalizedEmail = normalize(email);

        if (normalizedUsername == null || !USERNAME_PATTERN.matcher(normalizedUsername).matches()) {
            throw new ValidationException("Username must be 3-30 letters, digits or underscores");
        }
        if (normalizedEmail == null || !EMAIL_PATTERN.matcher(normalizedEmail).matches()) {
            throw new ValidationException("A valid email address is required");
        }
        if (password == null || password.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters");
        }

        try {
            if (userDAO.findByUsername(normalizedUsername).isPresent()) {
                throw new ConflictException("Username is already taken");
            }
            if (userDAO.findByEmail(normalizedEmail).isPresent()) {
                throw new ConflictException("Email is already registered");
            }
            return userDAO.create(new User(normalizedUsername, normalizedEmail, PasswordUtil.hash(password)));
        } catch (SQLException e) {
            throw new DataAccessException("Could not register user", e);
        }
    }

    public User login(String identifier, String password) {
        String normalizedIdentifier = normalize(identifier);
        if (normalizedIdentifier == null || password == null || password.isBlank()) {
            throw new AuthenticationException("Invalid username or password");
        }

        try {
            Optional<User> found = userDAO.findByUsername(normalizedIdentifier);
            if (found.isEmpty()) {
                found = userDAO.findByEmail(normalizedIdentifier);
            }
            if (found.isEmpty() || !PasswordUtil.verify(password, found.get().getPasswordHash())) {
                throw new AuthenticationException("Invalid username or password");
            }

            User user = found.get();
            String token = TokenUtil.generate();
            LocalDateTime expiresAt = LocalDateTime.now().plusHours(AppConfig.getTokenTtlHours());
            userDAO.updateToken(user.getId(), token, expiresAt);
            user.setAuthToken(token);
            user.setTokenExpiresAt(expiresAt);
            return user;
        } catch (SQLException e) {
            throw new DataAccessException("Could not log in", e);
        }
    }

    public Optional<User> validateToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        try {
            Optional<User> found = userDAO.findByToken(token);
            if (found.isEmpty()) {
                return Optional.empty();
            }
            LocalDateTime expiresAt = found.get().getTokenExpiresAt();
            if (expiresAt != null && !expiresAt.isAfter(LocalDateTime.now())) {
                return Optional.empty();
            }
            return found;
        } catch (SQLException e) {
            throw new DataAccessException("Could not validate token", e);
        }
    }

    public void logout(long userId) {
        try {
            userDAO.clearToken(userId);
        } catch (SQLException e) {
            throw new DataAccessException("Could not log out", e);
        }
    }

    private static String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
