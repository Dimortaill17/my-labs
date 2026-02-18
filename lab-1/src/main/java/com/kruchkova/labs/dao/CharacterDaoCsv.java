package com.kruchkova.labs.dao;

import com.kruchkova.labs.model.Character;
import com.kruchkova.labs.util.CsvUtil;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class CharacterDaoCsv implements CharacterDao {

    private static final String CSV_HEADER = "id,name,status,species,type,gender,origin/name,location/name,created";
    private final String filePath; // путь к CSV-файлу
    private List<Character> characters; // кеш данных в памяти

    public CharacterDaoCsv(String filePath) {
        this.filePath = filePath;
        this.characters = loadCharacters();
    }

    // Загружает данные из CSV-файла
    private List<Character> loadCharacters() {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                return new ArrayList<>();
            }

            List<String> lines = Files.readAllLines(path);
            if (lines.isEmpty()) {
                return new ArrayList<>();
            }

            // Пропускаем заголовок
            return lines.stream()
                    .skip(1)
                    .map(this::parseCsvLine)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Создает объект Character из строки CSV
    private Character parseCsvLine(String line) {
        try {
            String[] values = CsvUtil.parseCsvLine(line);
            if (values.length < 9) {
                return null;
            }

            // Создание нового персонажа и заполнение полей
            Character character = new Character();
            character.setId(Long.parseLong(values[0])); // id
            character.setName(values[1]);               // имя
            character.setStatus(values[2]);             // статус (Alive/Dead/unknown)
            character.setSpecies(values[3]);            // раса (Human/Alien)
            character.setType(values[4]);               // тип (может быть пустым)
            character.setGender(values[5]);             // пол
            character.setOriginName(values[6]);         // название мира происхождения
            character.setLocationName(values[7]);       // локация
            character.setCreated(values[8]);            // дата создания

            return character;
        } catch (Exception e) {
            System.err.println("Ошибка при парсинге строки: " + line);
            return null;
        }
    }

    // Сохраняет текущий список в файл
    private void saveToFile() {
        try {
            List<String> lines = new ArrayList<>();
            lines.add(CSV_HEADER);
            lines.addAll(characters.stream()
                    .map(Character::toCsvString)
                    .toList());

            Files.write(Paths.get(filePath), lines);

        } catch (IOException e) {
            System.err.println("Ошибка при сохранении в файл: " + e.getMessage());
        }
    }

    // Возвращает список всех персонажей
    @Override
    public List<Character> findAll() {
        return new ArrayList<>(characters); // возвращает копию списка
    }

    // Ищет персонажа по id
    @Override
    public Character findById(Long id) {
        return characters.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // Сохраняет нового персонажа
    @Override
    public void save(Character character) {
        // Проверяем, существует ли уже персонаж с таким id
        if (findById(character.getId()) != null) {
            throw new IllegalArgumentException("Персонаж с id " + character.getId() + " уже существует");
        }
        characters.add(character);
        saveToFile();
    }

    // Обновляет существующего персонажа
    @Override
    public void update(Character character) {
        Character existing = findById(character.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Персонаж с id " + character.getId() + " не найден");
        }

        int index = characters.indexOf(existing);
        characters.set(index, character);
        saveToFile();
    }

    // Удаляет персонажа по id
    @Override
    public void delete(Long id) {
        Character existing = findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Персонаж с id " + id + " не найден");
        }

        characters.remove(existing);
        saveToFile();
    }

    // Сохраняет всех персонажей в файл (перезаписывает текущий список)
    @Override
    public void saveAll(List<Character> newCharacters) {
        this.characters = new ArrayList<>(newCharacters);
        saveToFile();
    }
}