package com.kruchkova.labs;

import com.kruchkova.labs.config.DatabaseConfig;
import com.kruchkova.labs.entity.AuthorEntity;
import com.kruchkova.labs.exception.RepositoryException;
import com.kruchkova.labs.exception.ServiceException;
import com.kruchkova.labs.migration.DatabaseMigrator;
import com.kruchkova.labs.repository.AuthorRepository;
import com.kruchkova.labs.repository.impl.AuthorRepositoryJdbi;
import com.kruchkova.labs.service.AuthorService;
import com.kruchkova.labs.service.impl.AuthorServiceImpl;
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

            // Запуск миграций Liquibase
            DatabaseMigrator migrator = new DatabaseMigrator(databaseConfig);
            DatabaseInitializer initializer = new DatabaseInitializer(migrator);
            initializer.createTableIfNotExists();

            // Создание репозитория
            AuthorRepository repo = new AuthorRepositoryJdbi(databaseConfig);

            //Создание сервиса и внедрение в него репозитория
            AuthorService service = new AuthorServiceImpl(repo);

            // Запускаем демонстрацию, передавая теперь сервис вместо репозитория
            demonstrateService(service);

        } catch (RepositoryException e) {
            log.error("Ошибка репозитория: {}", e.getMessage());
            if (e.getCause() != null) {
                log.error("Причина: {}", e.getCause().getMessage());
            }
            throw e;
        } catch (ServiceException e) {
            // Обработка бизнес-ошибок от сервиса
            log.error("Ошибка сервиса: {}", e.getMessage());
            System.out.println("Бизнес-ошибка: " + e.getMessage());
        } catch (Exception e) {
            log.error("Неожиданная ошибка", e);
            throw new RuntimeException(e);
        }

        log.info("Приложение завершено");
    }

    private static void demonstrateService(AuthorService service) {
        log.info("=== Демонстрация CRUD операций ===\n");

        int id;
        int id2;
        int id3;

        // 1. CREATE
        log.info("1. CREATE: Создание автора");
        try {
            // Используем метод save(name, birthYear) из сервиса
            id = service.save("Лев Николаевич Толстой", 1828);
            log.info("   Создан: Лев Николаевич Толстой (id={})\n", id);

            id2 = service.save("Федор Михайлович Достоевский", 1821);
            log.info("   Создан: Федор Михайлович Достоевский (id={})\n", id2);

            id3 = service.save("Антон Павлович Чехов", 1860);
            log.info("   Создан: Антон Павлович Чехов (id={})\n", id3);
        } catch (ServiceException e) {
            log.warn("Не удалось создать авторов: {}", e.getMessage());
            return;
        }

        // 2. READ by ID
        log.info("2. READ: Поиск по ID");
        try {
            AuthorEntity found = service.findById(id);
            log.info("   Найден по id={}: {}\n", id, found);
        } catch (ServiceException e) {
            log.info("   Найден по id={}: null (не найден)\n", id);
        }


        // 3. READ by Field (по имени)
        log.info("3. READ: Поиск по имени");
        try {
            AuthorEntity foundByName = service.findByName("Лев Николаевич Толстой");
            log.info("   Найден по имени: {}\n", foundByName);
        } catch (ServiceException e) {
            log.info("   Найден по имени: null (не найден)\n");
        }

        // 4. READ ALL
        log.info("4. READ ALL: Все авторы");
        List<AuthorEntity> all = service.findAll();
        log.info("   Всего авторов: {}", all.size());
        all.forEach(a -> log.info("   - {}", a));
        log.info("");

        // 5. UPDATE
        log.info("5. UPDATE: Обновление");
        try {
            // Создаем объект для обновления, т.к. сервис требует полный объект
            // Берем текущего автора, меняем год и обновляем
            AuthorEntity authorToUpdate = new AuthorEntity(id, "Лев Николаевич Толстой", 1800);

            boolean updated = updateAuthorSafely(service, authorToUpdate);
            log.info("   Обновление: {}", updated);

            // Повторное обновление теми же данными
            boolean secondUpdate = updateAuthorSafely(service, authorToUpdate);
            log.info("   Идемпотентность (повтор): {}\n", secondUpdate);
        } catch (ServiceException e) {
            log.error("   Ошибка при обновлении: {}", e.getMessage());
        }

        // 6. DELETE
        log.info("6. DELETE: Удаление");
        service.deleteById(id);
        log.info("   Удалён автор с id={}", id);

        // Проверка идемпотентности: повторное удаление не должно вызывать ошибку
        try {
            service.deleteById(id);
            log.info("   Идемпотентность (повтор): выполнено без ошибки\n");
        } catch (ServiceException e) {
            log.info("   Идемпотентность (повтор): ошибка\n");
        }

        // 7. ФИНАЛЬНОЕ СОСТОЯНИЕ
        log.info("7. ФИНАЛЬНОЕ СОСТОЯНИЕ");
        log.info("   Осталось авторов: {}", service.findAll().size());
    }

    // Возвращает true, если обновление успешно, иначе false
    private static boolean updateAuthorSafely(AuthorService service, AuthorEntity author) {
        try {
            service.update(author);
            return true;
        } catch (ServiceException e) {
            return false;
        }
    }
}