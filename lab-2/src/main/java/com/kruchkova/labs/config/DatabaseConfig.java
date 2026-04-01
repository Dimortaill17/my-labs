package com.kruchkova.labs.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    private final HikariDataSource dataSource;

    public DatabaseConfig() {
        String jdbcUrl = System.getenv("DB_URL");
        String username = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");

        if (jdbcUrl == null) {
            jdbcUrl = "jdbc:postgresql://localhost:5433/authordb";
            log.warn("DB_URL не задан, используем значение по умолчанию: {}", jdbcUrl);
        }
        if (username == null) {
            username = "author_user";
            log.warn("DB_USERNAME не задан, используем значение по умолчанию: {}", username);
        }
        if (password == null) {
            password = "author123";
            log.warn("DB_PASSWORD не задан, используем значение по умолчанию");
        }

        try {
            Class.forName("org.postgresql.Driver");
            log.info("PostgreSQL JDBC Driver зарегистрирован");

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(username);
            config.setPassword(password);

            config.setMaximumPoolSize(10);
            config.setMinimumIdle(5);
            config.setIdleTimeout(300000);
            config.setConnectionTimeout(30000);
            config.setMaxLifetime(1800000);

            dataSource = new HikariDataSource(config);
            log.info("HikariCP connection pool инициализирован");

        } catch (ClassNotFoundException e) {
            log.error("PostgreSQL JDBC Driver не найден", e);
            throw new RuntimeException("Driver not found", e);
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            log.info("HikariCP connection pool закрыт");
        }
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }
}