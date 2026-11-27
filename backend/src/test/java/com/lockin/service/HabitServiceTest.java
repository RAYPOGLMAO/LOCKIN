package com.lockin.service;

import com.lockin.dao.HabitDAO;
import com.lockin.exception.NotFoundException;
import com.lockin.exception.ValidationException;
import com.lockin.model.Habit;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

class HabitServiceTest {

    @Test
    void blankNameIsRejected() {
        HabitService service = new HabitService(new StubHabitDAO());
        assertThrows(ValidationException.class, () -> service.create(1L, "   ", null));
    }

    @Test
    void missingHabitIsReportedAsNotFound() {
        HabitService service = new HabitService(new StubHabitDAO());
        assertThrows(NotFoundException.class, () -> service.get(1L, 99L));
    }

    private static class StubHabitDAO implements HabitDAO {
        @Override
        public List<Habit> findByUserId(long userId) {
            return List.of();
        }

        @Override
        public Optional<Habit> findByIdAndUserId(long id, long userId) {
            return Optional.empty();
        }

        @Override
        public Habit create(Habit habit) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean update(Habit habit) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean delete(long id, long userId) throws SQLException {
            throw new UnsupportedOperationException();
        }
    }
}
