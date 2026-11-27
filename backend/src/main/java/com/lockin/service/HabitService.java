package com.lockin.service;

import com.lockin.dao.HabitDAO;
import com.lockin.dao.impl.HabitDAOImpl;
import com.lockin.exception.DataAccessException;
import com.lockin.exception.NotFoundException;
import com.lockin.exception.ValidationException;
import com.lockin.model.Habit;

import java.sql.SQLException;
import java.util.List;

public class HabitService {

    private final HabitDAO habitDAO;

    public HabitService() {
        this(new HabitDAOImpl());
    }

    public HabitService(HabitDAO habitDAO) {
        this.habitDAO = habitDAO;
    }

    public List<Habit> list(long userId) {
        try {
            return habitDAO.findByUserId(userId);
        } catch (SQLException e) {
            throw new DataAccessException("Could not list habits", e);
        }
    }

    public Habit get(long userId, long habitId) {
        try {
            return habitDAO.findByIdAndUserId(habitId, userId)
                    .orElseThrow(() -> new NotFoundException("Habit not found"));
        } catch (SQLException e) {
            throw new DataAccessException("Could not load habit", e);
        }
    }

    public Habit create(long userId, String name, String description) {
        String validName = validateName(name);
        Habit habit = new Habit(userId, validName, normalize(description));
        try {
            return habitDAO.create(habit);
        } catch (SQLException e) {
            throw new DataAccessException("Could not create habit", e);
        }
    }

    public Habit update(long userId, long habitId, String name, String description, Boolean completed) {
        Habit habit = get(userId, habitId);
        if (name != null) {
            habit.setName(validateName(name));
        }
        if (description != null) {
            habit.setDescription(normalize(description));
        }
        if (completed != null) {
            habit.setCompleted(completed);
        }
        try {
            if (!habitDAO.update(habit)) {
                throw new NotFoundException("Habit not found");
            }
            return habit;
        } catch (SQLException e) {
            throw new DataAccessException("Could not update habit", e);
        }
    }

    public void delete(long userId, long habitId) {
        try {
            if (!habitDAO.delete(habitId, userId)) {
                throw new NotFoundException("Habit not found");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not delete habit", e);
        }
    }

    private static String validateName(String name) {
        String normalized = normalize(name);
        if (normalized == null || normalized.length() > 100) {
            throw new ValidationException("Habit name is required and must be at most 100 characters");
        }
        return normalized;
    }

    private static String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
