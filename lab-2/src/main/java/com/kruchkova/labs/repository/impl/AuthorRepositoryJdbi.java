package com.kruchkova.labs.repository.impl;

import com.kruchkova.labs.config.DatabaseConfig;
import com.kruchkova.labs.entity.AuthorEntity;
import com.kruchkova.labs.exception.RepositoryException;
import com.kruchkova.labs.repository.AuthorDao;
import com.kruchkova.labs.repository.AuthorMapper;
import com.kruchkova.labs.repository.AuthorRepository;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class AuthorRepositoryJdbi implements AuthorRepository {
    private static final Logger log = LoggerFactory.getLogger(AuthorRepositoryJdbi.class);

    private final Jdbi jdbi;
    private final AuthorDao dao;

    public AuthorRepositoryJdbi(DatabaseConfig databaseConfig) {
        try {
            this.jdbi = Jdbi.create(databaseConfig.getDataSource());
            this.jdbi.installPlugin(new SqlObjectPlugin());
            this.jdbi.registerRowMapper(new AuthorMapper());
            this.dao = jdbi.onDemand(AuthorDao.class);
            log.debug("JDBI репозиторий инициализирован");
        } catch (Exception e) {
            log.error("Ошибка инициализации JDBI", e);
            throw new RepositoryException("Ошибка инициализации JDBI", e);
        }
    }

    @Override
    public int save(AuthorEntity author) {
        try {
            int id = dao.insert(author);
            author.setId(id);
            log.debug("Сохранен автор с id: {}", id);
            return id;
        } catch (Exception e) {
            log.error("Ошибка при сохранении автора", e);
            throw new RepositoryException("Ошибка при сохранении автора", e);
        }
    }

    @Override
    public Optional<AuthorEntity> findById(int id) {
        try {
            Optional<AuthorEntity> result = dao.findById(id);
            log.debug("Поиск автора по id {}: {}", id, result.isPresent() ? "найден" : "не найден");
            return result;
        } catch (Exception e) {
            log.error("Ошибка при поиске автора по id: {}", id, e);
            throw new RepositoryException("Ошибка при поиске автора по id: " + id, e);
        }
    }

    @Override
    public AuthorEntity findByField(String field) {
        try {
            return dao.findByName(field).orElse(null);
        } catch (Exception e) {
            log.error("Ошибка при поиске автора по имени: {}", field, e);
            throw new RepositoryException("Ошибка при поиске автора по имени: " + field, e);
        }
    }

    @Override
    public List<AuthorEntity> findAll() {
        try {
            List<AuthorEntity> result = dao.findAll();
            log.debug("Получено {} авторов", result.size());
            return result;
        } catch (Exception e) {
            log.error("Ошибка при получении всех авторов", e);
            throw new RepositoryException("Ошибка при получении всех авторов", e);
        }
    }

    @Override
    public boolean update(AuthorEntity author) {
        if (author.getId() == null) {
            log.warn("Попытка обновления автора без id");
            return false;
        }

        try {
            int updatedRows = dao.update(author);
            boolean success = updatedRows > 0;
            log.debug("Обновление автора с id {}: {}", author.getId(), success ? "успешно" : "не найдено записей");
            return success;
        } catch (Exception e) {
            log.error("Ошибка при обновлении автора с id: {}", author.getId(), e);
            throw new RepositoryException("Ошибка при обновлении автора с id: " + author.getId(), e);
        }
    }

    @Override
    public void deleteById(int id) {
        try {
            dao.deleteById(id);
            log.debug("Удаление автора с id: {}", id);
        } catch (Exception e) {
            log.error("Ошибка при удалении автора с id: {}", id, e);
            throw new RepositoryException("Ошибка при удалении автора с id: " + id, e);
        }
    }
}