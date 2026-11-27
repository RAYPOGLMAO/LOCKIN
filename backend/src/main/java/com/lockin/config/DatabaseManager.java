package com.lockin.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public final class DatabaseManager {

    private static final DataSource DATA_SOURCE = createDataSource();

    private DatabaseManager() {
    }

    public static Connection getConnection() throws SQLException {
        return DATA_SOURCE.getConnection();
    }

    public static DataSource getDataSource() {
        return DATA_SOURCE;
    }

    private static DataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(AppConfig.getDbUrl());
        config.setUsername(AppConfig.getDbUser());
        config.setPassword(AppConfig.getDbPassword());
        config.setMaximumPoolSize(AppConfig.getDbPoolSize());
        config.setMinimumIdle(1);
        config.setPoolName("lockin-pool");
        config.setAutoCommit(true);
        return new HikariDataSource(config);
    }
}
