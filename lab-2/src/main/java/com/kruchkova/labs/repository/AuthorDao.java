package com.kruchkova.labs.repository;

import com.kruchkova.labs.entity.AuthorEntity;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

@RegisterRowMapper(AuthorMapper.class)
public interface AuthorDao {

    @SqlUpdate("INSERT INTO author (name, birth_year) VALUES (:name, :birthYear) RETURNING id")
    @GetGeneratedKeys
    int insert(@BindBean AuthorEntity author);

    @SqlQuery("SELECT id, name, birth_year FROM author WHERE id = :id")
    Optional<AuthorEntity> findById(@Bind("id") int id);

    @SqlQuery("SELECT id, name, birth_year FROM author WHERE name = :name LIMIT 1")
    Optional<AuthorEntity> findByName(@Bind("name") String name);

    @SqlQuery("SELECT id, name, birth_year FROM author ORDER BY id")
    List<AuthorEntity> findAll();

    @SqlUpdate("UPDATE author SET name = :name, birth_year = :birthYear WHERE id = :id")
    int update(@BindBean AuthorEntity author);

    @SqlUpdate("DELETE FROM author WHERE id = :id")
    void deleteById(@Bind("id") int id);
}