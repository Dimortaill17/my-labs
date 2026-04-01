package com.kruchkova.labs.entity;

public class AuthorEntity {
    private Integer id;
    private String name;
    private Integer birthYear;

    public AuthorEntity() {
    }

    public AuthorEntity(String name, Integer birthYear) {
        this.name = name;
        this.birthYear = birthYear;
    }

    public AuthorEntity(Integer id, String name, Integer birthYear) {
        this.id = id;
        this.name = name;
        this.birthYear = birthYear;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

    @Override
    public String toString() {
        return "AuthorEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birthYear=" + birthYear +
                '}';
    }
}