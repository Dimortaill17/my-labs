package com.kruchkova.labs.model;

/**
 * Модель персонажа из сериала Rick and Morty
 * Соответствует данным из CSV-файла
 */
public class Character {
    private Long id;              // уникальный идентификатор
    private String name;          // имя персонажа
    private String status;        // статус (Alive/Dead/unknown)
    private String species;       // раса (Human, Alien)
    private String type;          // тип (может быть пустым)
    private String gender;        // пол (Male/Female/unknown)
    private String originName;    // название мира происхождения
    private String locationName;  // локация
    private String created;       // дата создания

    // Конструктор по умолчанию
    public Character() {
    }

    // Конструктор со всеми полями
    public Character(Long id, String name, String status, String species,
                     String type, String gender, String originName,
                     String locationName, String created) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.species = species;
        this.type = type;
        this.gender = gender;
        this.originName = originName;
        this.locationName = locationName;
        this.created = created;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    // Удобный вывод в консоль (id, имя, статус, происхождение)
    @Override
    public String toString() {
        return String.format("Character{id=%d, name='%s', status='%s', origin='%s'}",
                id, name, status, originName);
    }

    // Преобразует объект в строку для записи в CSV-файл
    public String toCsvString() {
        return String.join(",",
                String.valueOf(id),
                name,
                status,
                species,
                type,
                gender,
                originName,
                locationName,
                created
        );
    }
}