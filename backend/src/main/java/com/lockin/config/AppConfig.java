package com.lockin.config;

public final class AppConfig {

    private static final String DB_URL = read("LOCKIN_DB_URL", "jdbc:postgresql://localhost:5432/lockin");
    private static final String DB_USER = read("LOCKIN_DB_USER", "lockin");
    private static final String DB_PASSWORD = read("LOCKIN_DB_PASSWORD", "lockin");
    private static final int DB_POOL_SIZE = readInt("LOCKIN_DB_POOL_SIZE", 10);
    private static final int TOKEN_TTL_HOURS = readInt("LOCKIN_TOKEN_TTL_HOURS", 24);

    private AppConfig() {
    }

    public static String getDbUrl() {
        return DB_URL;
    }

    public static String getDbUser() {
        return DB_USER;
    }

    public static String getDbPassword() {
        return DB_PASSWORD;
    }

    public static int getDbPoolSize() {
        return DB_POOL_SIZE;
    }

    public static int getTokenTtlHours() {
        return TOKEN_TTL_HOURS;
    }

    private static String read(String key, String fallback) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            value = System.getProperty(key);
        }
        return (value == null || value.isBlank()) ? fallback : value;
    }

    private static int readInt(String key, int fallback) {
        try {
            return Integer.parseInt(read(key, String.valueOf(fallback)));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
