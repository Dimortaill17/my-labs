package com.kruchkova.labs.service.impl;

import com.kruchkova.labs.entity.AuthorEntity;
import com.kruchkova.labs.exception.ServiceException;
import com.kruchkova.labs.repository.AuthorRepository;
import com.kruchkova.labs.service.AuthorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Year;
import java.util.List;
import java.util.Optional;

// Реализация сервисного слоя для работы с авторами
// Инкапсулирует бизнес-логику и оборачивает репозиторий
public class AuthorServiceImpl implements AuthorService {

    private static final Logger log = LoggerFactory.getLogger(AuthorServiceImpl.class);

    // Сообщение об ошибке при отсутствии автора
    private static final String AUTHOR_NOT_FOUND_MESSAGE = "Автор не найден";

    // Зависимость от репозитория
    private final AuthorRepository authorRepository;

    // Конструктор с инъекцией зависимости
    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public int save(String name, Integer birthYear) {
        // Валидация имени
        if (name == null || name.trim().isEmpty()) {
            log.warn("Попытка создать автора с пустым именем");
            throw new ServiceException("Имя автора не может быть пустым");
        }

        // Валидация года рождения (бизнес-правило: не в будущем и не слишком древний)
        int currentYear = Year.now().getValue();
        if (birthYear == null || birthYear < 1000 || birthYear > currentYear) {
            log.warn("Попытка создать автора с некорректным годом: {}", birthYear);
            throw new ServiceException("Год рождения должен быть в диапазоне от 1000 до " + currentYear);
        }

        AuthorEntity author = new AuthorEntity(name, birthYear);
        int id = authorRepository.save(author);
        log.info("Автор сохранён с ID: {}", id);
        return id;
    }

    @Override
    public AuthorEntity findById(int id) {
        Optional<AuthorEntity> authorOpt = authorRepository.findById(id);

        if (authorOpt.isEmpty()) {
            log.warn("Автор с ID {} не найден", id);
            throw new ServiceException(AUTHOR_NOT_FOUND_MESSAGE + " с ID: " + id);
        }

        log.info("Автор с ID {} найден", id);
        return authorOpt.get();
    }

    @Override
    public AuthorEntity findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            log.warn("Попытка поиска автора по пустому имени");
            throw new ServiceException("Имя для поиска не может быть пустым");
        }

        // Делегируем поиск репозиторию (там уже есть метод findByField/name)
        // Но так как репозиторий может вернуть null или Optional, обернем в исключение
        AuthorEntity author = authorRepository.findByField(name);

        if (author == null) {
            log.warn("Автор с именем '{}' не найден", name);
            throw new ServiceException(AUTHOR_NOT_FOUND_MESSAGE + " с именем: " + name);
        }

        log.info("Автор с именем '{}' найден", name);
        return author;
    }

    @Override
    public List<AuthorEntity> findAll() {
        log.info("Запрос на получение всех авторов");
        return authorRepository.findAll();
    }

    @Override
    public void update(AuthorEntity author) {
        if (author == null) {
            log.warn("Попытка обновить null-автора");
            throw new ServiceException("Объект автора не может быть null");
        }
        if (author.getId() == null) {
            log.warn("Попытка обновить автора без ID");
            throw new ServiceException("ID автора обязателен для обновления");
        }
        if (author.getName() == null || author.getName().trim().isEmpty()) {
            log.warn("Попытка обновить автора с пустым именем");
            throw new ServiceException("Имя автора не может быть пустым");
        }

        // Проверка существования перед обновлением
        if (authorRepository.findById(author.getId()).isEmpty()) {
            log.warn("Попытка обновить несуществующего автора с ID: {}", author.getId());
            throw new ServiceException(AUTHOR_NOT_FOUND_MESSAGE + " с ID: " + author.getId());
        }

        boolean updated = authorRepository.update(author);
        if (!updated) {
            log.error("Не удалось обновить автора с ID: {}", author.getId());
            throw new ServiceException("Ошибка при обновлении автора с ID: " + author.getId());
        }

        log.info("Автор с ID {} успешно обновлён", author.getId());
    }

    @Override
    public void deleteById(int id) {
        authorRepository.deleteById(id);
        log.info("Автор с ID {} удалён (или уже отсутствовал)", id);
    }
}