package com.lockin.dao.impl;

import com.lockin.config.DatabaseManager;
import com.lockin.dao.UserDAO;
import com.lockin.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

public class UserDAOImpl implements UserDAO {

    private static final String COLUMNS =
            "id, username, email, password_hash, auth_token, token_expires_at, created_at";

    @Override
    public User create(User user) throws SQLException {
        String sql = "INSERT INTO app_user (username, email, password_hash) VALUES (?, ?, ?) RETURNING " + COLUMNS;
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPasswordHash());
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        throw new SQLException("Unable to create user");
    }

    @Override
    public Optional<User> findById(long id) throws SQLException {
        return findOne("SELECT " + COLUMNS + " FROM app_user WHERE id = ?", id);
    }

    @Override
    public Optional<User> findByUsername(String username) throws SQLException {
        return findOne("SELECT " + COLUMNS + " FROM app_user WHERE username = ?", username);
    }

    @Override
    public Optional<User> findByEmail(String email) throws SQLException {
        return findOne("SELECT " + COLUMNS + " FROM app_user WHERE email = ?", email);
    }

    @Override
    public Optional<User> findByToken(String token) throws SQLException {
        return findOne("SELECT " + COLUMNS + " FROM app_user WHERE auth_token = ?", token);
    }

    @Override
    public boolean updateToken(long userId, String token, LocalDateTime expiresAt) throws SQLException {
        String sql = "UPDATE app_user SET auth_token = ?, token_expires_at = ? WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, token);
            statement.setTimestamp(2, Timestamp.valueOf(expiresAt));
            statement.setLong(3, userId);
            return statement.executeUpdate() == 1;
        }
    }

    @Override
    public boolean clearToken(long userId) throws SQLException {
        String sql = "UPDATE app_user SET auth_token = NULL, token_expires_at = NULL WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            return statement.executeUpdate() == 1;
        }
    }

    private Optional<User> findOne(String sql, Object parameter) throws SQLException {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, parameter);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    private static User map(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setAuthToken(rs.getString("auth_token"));
        Timestamp expiresAt = rs.getTimestamp("token_expires_at");
        user.setTokenExpiresAt(expiresAt == null ? null : expiresAt.toLocalDateTime());
        Timestamp createdAt = rs.getTimestamp("created_at");
        user.setCreatedAt(createdAt == null ? null : createdAt.toLocalDateTime());
        return user;
    }
}
