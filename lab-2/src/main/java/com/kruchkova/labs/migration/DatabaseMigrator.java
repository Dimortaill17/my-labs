package com.kruchkova.labs.migration;

import com.kruchkova.labs.config.DatabaseConfig;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

public class DatabaseMigrator {
    private static final Logger log = LoggerFactory.getLogger(DatabaseMigrator.class);

    private final DatabaseConfig databaseConfig;

    public DatabaseMigrator(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public void runMigrations() {
        log.info("Запуск Liquibase миграций");

        try (Connection connection = databaseConfig.getConnection()) {
            Liquibase liquibase = new Liquibase(
                    "db/changelog/db.changelog-master.xml",
                    new ClassLoaderResourceAccessor(),
                    new JdbcConnection(connection)
            );

            liquibase.update();
            log.info("Liquibase миграции успешно применены");

        } catch (Exception e) {
            log.error("Ошибка применения миграций БД", e);
            throw new RuntimeException("Ошибка при выполнении миграций", e);
        }
    }
}