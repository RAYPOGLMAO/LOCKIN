package com.lockin.dao;

import com.lockin.model.Habit;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface HabitDAO {

    List<Habit> findByUserId(long userId) throws SQLException;

    Optional<Habit> findByIdAndUserId(long id, long userId) throws SQLException;

    Habit create(Habit habit) throws SQLException;

    boolean update(Habit habit) throws SQLException;

    boolean delete(long id, long userId) throws SQLException;
}
