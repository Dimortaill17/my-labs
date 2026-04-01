package com.kruchkova.labs.util;

import com.kruchkova.labs.migration.DatabaseMigrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseInitializer {
    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    private final DatabaseMigrator databaseMigrator;

    public DatabaseInitializer(DatabaseMigrator databaseMigrator) {
        this.databaseMigrator = databaseMigrator;
    }

    public void createTableIfNotExists() {
        databaseMigrator.runMigrations();
        log.info("Таблица 'author' успешно создана или уже существует");
    }
}