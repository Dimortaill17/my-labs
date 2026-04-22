package com.kruchkova.labs.service;

import com.kruchkova.labs.entity.AuthorEntity;
import java.util.List;

// Интерфейс сервисного слоя для работы с авторами
// Определяет контракт бизнес-логики
public interface AuthorService {

    // Create: создаёт нового автора, возвращает сгенерированный ID
    int save(String name, Integer birthYear);

    // Read: находит автора по ID, бросает ServiceException если не найден
    AuthorEntity findById(int id);

    // Read: находит автора по имени (доп метод по полю)
    // Бросает ServiceException если не найден
    AuthorEntity findByName(String name);

    // Read: возвращает список всех авторов
    List<AuthorEntity> findAll();

    // Update: обновляет данные автора, бросает ServiceException если не найден
    void update(AuthorEntity author);

    // Delete: удаляет автора по ID
    void deleteById(int id);
}