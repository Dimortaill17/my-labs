package com.kruchkova.labs;

import com.kruchkova.labs.config.DatabaseConfig;
import com.kruchkova.labs.entity.AuthorEntity;
import com.kruchkova.labs.exception.RepositoryException;
import com.kruchkova.labs.migration.DatabaseMigrator;
import com.kruchkova.labs.repository.AuthorRepository;
import com.kruchkova.labs.repository.impl.AuthorRepositoryJdbi;
import com.kruchkova.labs.util.DatabaseInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Запуск приложения 'Книжный каталог: Писатели'");

        try (DatabaseConfig databaseConfig = new DatabaseConfig()) {

            DatabaseMigrator migrator = new DatabaseMigrator(databaseConfig);
            DatabaseInitializer initializer = new DatabaseInitializer(migrator);
            initializer.createTableIfNotExists();

            AuthorRepository repo = new AuthorRepositoryJdbi(databaseConfig);

            demonstrateRepository(repo);

        } catch (RepositoryException e) {
            log.error("Ошибка репозитория: {}", e.getMessage());
            if (e.getCause() != null) {
                log.error("Причина: {}", e.getCause().getMessage());
            }
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка", e);
            throw new RuntimeException(e);
        }

        log.info("Приложение завершено");
    }

    private static void demonstrateRepository(AuthorRepository repo) {
        log.info("=== Демонстрация CRUD операций ===\n");

        // 1. CREATE
        // Автор 1
        log.info("1. CREATE: Создание автора");
        AuthorEntity author = new AuthorEntity("Лев Николаевич Толстой", 1828);
        int id = repo.save(author);
        log.info("   Создан: {} (id={})\n", author.getName(), id);

        // Автор 2
        AuthorEntity author2 = new AuthorEntity("Федор Михайлович Достоевский", 1821);
        int id2 = repo.save(author2);
        log.info("   Создан: {} (id={})\n", author2.getName(), id2);

        // Автор 3
        AuthorEntity author3 = new AuthorEntity("Антон Павлович Чехов", 1860);
        int id3 = repo.save(author3);
        log.info("   Создан: {} (id={})\n", author3.getName(), id3);

        // 2. READ by ID
        log.info("2. READ: Поиск по ID");
        Optional<AuthorEntity> foundOpt = repo.findById(id);
        log.info("   Найден по id={}: {}\n", id, foundOpt.orElse(null));

        // 3. READ by Field
        log.info("3. READ: Поиск по имени");
        AuthorEntity foundByName = repo.findByField("Лев Николаевич Толстой");
        log.info("   Найден по имени: {}\n", foundByName != null ? foundByName : "null");

        // 4. READ ALL
        log.info("4. READ ALL: Все авторы");
        List<AuthorEntity> all = repo.findAll();
        log.info("   Всего авторов: {}", all.size());
        all.forEach(a -> log.info("   - {}", a));
        log.info("");

        // 5. UPDATE
        log.info("5. UPDATE: Обновление");
        author.setBirthYear(1800);
        boolean updated = repo.update(author);
        log.info("   Обновление: {}", updated);
        boolean secondUpdate = repo.update(author);
        log.info("   Идемпотентность (повтор): {}\n", secondUpdate);

        // 6. DELETE
        log.info("6. DELETE: Удаление");
        repo.deleteById(id);
        log.info("   Удалён автор с id={}", id);
        repo.deleteById(id);  // идемпотентность
        log.info("   Идемпотентность (повтор): выполнено без ошибки\n");

        // 7. ФИНАЛЬНОЕ СОСТОЯНИЕ
        log.info("7. ФИНАЛЬНОЕ СОСТОЯНИЕ");
        log.info("   Осталось авторов: {}", repo.findAll().size());
    }
}