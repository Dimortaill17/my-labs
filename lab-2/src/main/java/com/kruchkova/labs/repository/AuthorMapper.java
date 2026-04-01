package com.kruchkova.labs.repository;

import com.kruchkova.labs.entity.AuthorEntity;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthorMapper implements RowMapper<AuthorEntity> {

    @Override
    public AuthorEntity map(ResultSet rs, StatementContext ctx) throws SQLException {
        AuthorEntity author = new AuthorEntity();
        author.setId(rs.getInt("id"));
        author.setName(rs.getString("name"));
        author.setBirthYear(rs.getInt("birth_year"));
        return author;
    }
}