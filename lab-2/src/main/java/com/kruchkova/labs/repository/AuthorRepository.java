package com.kruchkova.labs.repository;

import com.kruchkova.labs.entity.AuthorEntity;
import java.util.List;
import java.util.Optional;

public interface AuthorRepository {

    int save(AuthorEntity author);

    Optional<AuthorEntity> findById(int id);

    AuthorEntity findByField(String field);

    List<AuthorEntity> findAll();

    boolean update(AuthorEntity author);

    void deleteById(int id);
}