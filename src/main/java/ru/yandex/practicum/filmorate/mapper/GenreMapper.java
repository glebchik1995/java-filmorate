package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

import static ru.yandex.practicum.filmorate.fields.FieldsTable.GENRE_ID;
import static ru.yandex.practicum.filmorate.fields.FieldsTable.GENRE_NAME;
@Repository
public class GenreMapper implements RowMapper<Genre> {
    @Override
    public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getLong(GENRE_ID))
                .name(rs.getString(GENRE_NAME))
                .build();
    }
}
