package com.lockin.dao;

import com.lockin.model.User;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

public interface UserDAO {

    User create(User user) throws SQLException;

    Optional<User> findById(long id) throws SQLException;

    Optional<User> findByUsername(String username) throws SQLException;

    Optional<User> findByEmail(String email) throws SQLException;

    Optional<User> findByToken(String token) throws SQLException;

    boolean updateToken(long userId, String token, LocalDateTime expiresAt) throws SQLException;

    boolean clearToken(long userId) throws SQLException;
}
