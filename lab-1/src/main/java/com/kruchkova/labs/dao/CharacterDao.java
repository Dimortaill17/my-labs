package com.kruchkova.labs.dao;

import com.kruchkova.labs.model.Character;
import java.util.List;

public interface CharacterDao {

    /**
     * Полуучить список всех персонажей
     */
    List<Character> findAll(); //

    /**
     * Найти персонажа по id
     */
    Character findById(Long id);

    /**
     * Сохранить нового персонажа
     */
    void save(Character character);

    /**
     * Обновить существующего персонажа
     */
    void update(Character character);

    /**
     * Удалить персонажа по id
     */
    void delete(Long id);

    /**
     * Сохранить всех персонажей в файл
     */
    void saveAll(List<Character> characters);
}