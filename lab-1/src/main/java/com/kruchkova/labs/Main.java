package com.kruchkova.labs;

import com.kruchkova.labs.dao.CharacterDao;
import com.kruchkova.labs.dao.CharacterDaoCsv;
import com.kruchkova.labs.model.Character;
import com.kruchkova.labs.service.CharacterService;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // Пути к файлам
        String inputFilePath = "lab-1/src/main/resources/characters.csv";
        String outputFilePath = "lab-1/src/main/resources/origin_count.csv";

        // Создание DAO и сервис
        CharacterDao characterDao = new CharacterDaoCsv(inputFilePath);
        CharacterService characterService = new CharacterService(characterDao);

        System.out.println("Вариант: Map<String, Long> - количество персонажей по origin");
        System.out.println();

        // 1. Чтение данных из файла
        System.out.println("1. Чтение данных из файла " + inputFilePath);
        List<Character> characters = characterService.getAllCharacters();
        System.out.println("Загружено персонажей: " + characters.size());
        System.out.println();

        // 2. Обработка данных - группировка по origin
        System.out.println("2. Обработка данных - подсчет персонажей по origin");
        Map<String, Long> originCountMap = characterService.countCharactersByOrigin();

        // Вывод результата в консоль
        System.out.println("Результат группировки:");
        System.out.println("------------------------");
        originCountMap.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .forEach(entry ->
                        System.out.printf("%-35s → %d персонажей%n", entry.getKey(), entry.getValue())
                );
        System.out.println("Всего уникальных origins: " + originCountMap.size());
        System.out.println();

        // 3. Запись результата в файл
        System.out.println("3. Запись результата в файл " + outputFilePath);
        characterService.saveOriginCountToFile(originCountMap, outputFilePath);
        System.out.println("Результат успешно сохранен!");
        System.out.println();

        // 4. Демонстрация CRUD операций
        System.out.println("4. Демонстрация CRUD операций");
        demonstrateCrudOperations(characterService);
    }

    // Демонстрирует работу Create, Read, Update, Delete
    private static void demonstrateCrudOperations(CharacterService characterService) {
        // CREATE - создание нового персонажа
        System.out.println("--- CREATE ---");
        Character newCharacter = new Character(
                100L, "Test Character", "Alive", "Human", "", "Male",
                "Test Origin", "Test Location", "2024-01-01T12:00:00.000Z"
        );
        characterService.createCharacter(newCharacter);
        System.out.println("Создан новый персонаж: " + newCharacter.getName());

        // READ - чтение персонажа
        System.out.println("--- READ ---");
        Character readCharacter = characterService.getCharacterById(100L);
        System.out.println("Получен персонаж: " + readCharacter);

        // UPDATE - обновление персонажа
        System.out.println("--- UPDATE ---");
        readCharacter.setStatus("Dead");
        characterService.updateCharacter(readCharacter);
        System.out.println("Обновлен статус персонажа: " + readCharacter.getStatus());

        // DELETE - удаление персонажа
        System.out.println("--- DELETE ---");
        characterService.deleteCharacter(100L);
        System.out.println("Персонаж удален");

        // Проверка, что персонаж действительно удален
        Character deletedCharacter = characterService.getCharacterById(100L);
        System.out.println("Проверка удаления: " +
                (deletedCharacter == null ? "Персонаж не найден (успешно)" : "Персонаж все еще существует"));
    }
}