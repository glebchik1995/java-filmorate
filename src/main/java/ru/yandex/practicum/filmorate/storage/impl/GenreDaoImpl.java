package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenreDao;

import java.util.Collection;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GenreDaoImpl implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    private final GenreMapper genreMapper = new GenreMapper();

    @Override
    public Genre getGenreById(long genreId) {
        log.debug("getGenreById({}).", genreId);
        final String sqlQuery = "SELECT * FROM genres WHERE genre_id = ?";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sqlQuery, genreId);

        if (!genreRows.next()) {
            log.warn("Жанр {} не найден.", genreId);
            throw new DataNotFoundException("Жанр не найден");
        }
        return jdbcTemplate.queryForObject(sqlQuery, genreMapper, genreId);
    }

    @Override
    public Collection<Genre> getAllGenres() {
        log.debug("getAllGenres().");
        String sqlQuery = "SELECT * "
                + "FROM genres "
                + "ORDER BY genre_id;";
        return jdbcTemplate.query(sqlQuery, genreMapper);
    }

}

