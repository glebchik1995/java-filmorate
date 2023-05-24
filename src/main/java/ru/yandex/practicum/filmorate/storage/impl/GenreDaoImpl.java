package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenreDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static ru.yandex.practicum.filmorate.storage.sqlСonstant.FieldsTable.GENRE_ID;
import static ru.yandex.practicum.filmorate.storage.sqlСonstant.FieldsTable.GENRE_NAME;

@Slf4j
@Repository
public class GenreDaoImpl implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    @Override
    public Genre getGenreById(int genreId) {
        log.debug("getGenreById({}).", genreId);
        SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT * FROM genres WHERE genre_id = ?;", genreId);
        if (rs.next()) {
            return Genre.builder()
                    .id(rs.getInt(GENRE_ID))
                    .name(rs.getString(GENRE_NAME))
                    .build();
        } else {
            throw new DataNotFoundException("Не найден жанр с id = " + genreId);
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        log.debug("getAllGenres().");
        String sqlQuery = "SELECT * FROM genres;";
        List<Genre> listGenre = jdbcTemplate.query(sqlQuery, this::makeGenre);
        log.info("Получены все жанры: {}.", listGenre);
        return listGenre;
    }

    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt(GENRE_ID))
                .name(rs.getString(GENRE_NAME))
                .build();
    }
}

