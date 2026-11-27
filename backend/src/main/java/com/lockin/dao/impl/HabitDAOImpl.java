package com.lockin.dao.impl;

import com.lockin.config.DatabaseManager;
import com.lockin.dao.HabitDAO;
import com.lockin.model.Habit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HabitDAOImpl implements HabitDAO {

    private static final String COLUMNS = "id, user_id, name, description, completed, created_at";

    @Override
    public List<Habit> findByUserId(long userId) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM habit WHERE user_id = ? ORDER BY created_at DESC, id DESC";
        List<Habit> habits = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    habits.add(map(rs));
                }
            }
        }
        return habits;
    }

    @Override
    public Optional<Habit> findByIdAndUserId(long id, long userId) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM habit WHERE id = ? AND user_id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.setLong(2, userId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public Habit create(Habit habit) throws SQLException {
        String sql = "INSERT INTO habit (user_id, name, description, completed) VALUES (?, ?, ?, ?) RETURNING " + COLUMNS;
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, habit.getUserId());
            statement.setString(2, habit.getName());
            statement.setString(3, habit.getDescription());
            statement.setBoolean(4, habit.isCompleted());
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        throw new SQLException("Unable to create habit");
    }

    @Override
    public boolean update(Habit habit) throws SQLException {
        String sql = "UPDATE habit SET name = ?, description = ?, completed = ? WHERE id = ? AND user_id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, habit.getName());
            statement.setString(2, habit.getDescription());
            statement.setBoolean(3, habit.isCompleted());
            statement.setLong(4, habit.getId());
            statement.setLong(5, habit.getUserId());
            return statement.executeUpdate() == 1;
        }
    }

    @Override
    public boolean delete(long id, long userId) throws SQLException {
        String sql = "DELETE FROM habit WHERE id = ? AND user_id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.setLong(2, userId);
            return statement.executeUpdate() == 1;
        }
    }

    private static Habit map(ResultSet rs) throws SQLException {
        Habit habit = new Habit();
        habit.setId(rs.getLong("id"));
        habit.setUserId(rs.getLong("user_id"));
        habit.setName(rs.getString("name"));
        habit.setDescription(rs.getString("description"));
        habit.setCompleted(rs.getBoolean("completed"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        habit.setCreatedAt(createdAt == null ? null : createdAt.toLocalDateTime());
        return habit;
    }
}
