package com.kruchkova.labs.service;

import com.kruchkova.labs.dao.CharacterDao;
import com.kruchkova.labs.model.Character;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class CharacterService {

    private final CharacterDao characterDao;

    public CharacterService(CharacterDao characterDao) {
        this.characterDao = characterDao;
    }

    /**
     * Получить всех персонажей
     */
    public List<Character> getAllCharacters() {
        return characterDao.findAll();
    }

    /**
     * Получить персонажа по id
     */
    public Character getCharacterById(Long id) {
        return characterDao.findById(id);
    }

    /**
     * Создать нового персонажа
     */
    public void createCharacter(Character character) {
        characterDao.save(character);
    }

    /**
     * Обновить персонажа
     */
    public void updateCharacter(Character character) {
        characterDao.update(character);
    }

    /**
     * Удалить персонажа
     */
    public void deleteCharacter(Long id) {
        characterDao.delete(id);
    }

    /**
     * Подсчитать количество персонажей по origin
     * Возвращает Map<String, Long> где ключ - origin, значение - количество
     */
    public Map<String, Long> countCharactersByOrigin() {
        List<Character> characters = characterDao.findAll();

        return characters.stream()
                .collect(Collectors.groupingBy(
                        Character::getOriginName,
                        Collectors.counting()
                ));
    }

    /**
     * Сохранить статистику по происхождению в файл
     */
    public void saveOriginCountToFile(Map<String, Long> originCountMap, String outputFilePath) {
        List<String> lines = new ArrayList<>();
        lines.add("origin,count"); // Заголовок

        // Сортировка от самых популярных origins к менее популярным
        originCountMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(entry -> lines.add(entry.getKey() + "," + entry.getValue()));

        try {
            Path path = Paths.get(outputFilePath);
            Files.write(path, lines);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении результата: " + e.getMessage());
        }
    }
}