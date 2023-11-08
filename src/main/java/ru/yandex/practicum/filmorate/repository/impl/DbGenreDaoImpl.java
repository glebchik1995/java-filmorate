package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.GenreDao;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DbGenreDaoImpl implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre getGenreById(Long genreId) {

        String sqlQuery = "SELECT * " +
                "          FROM genre " +
                "          WHERE genre_id=?";

        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sqlQuery, genreId);

        if (genreRows.next()) {
            log.info("Получен жанр с ID={}", genreId);
            return new Genre(
                    genreRows.getLong("genre_id"),
                    genreRows.getString("name"));
        } else {
            throw new DataNotFoundException(
                    String.format("Ошибка получения жанра. Жанр с ID=%d не найден!", genreId));
        }
    }

    @Override
    public List<Genre> getAllGenres() {

        String sqlQuery = "SELECT * " +
                "          FROM genre";

        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sqlQuery);
        List<Genre> genreList = new ArrayList<>();

        while (genreRows.next()) {
            Genre genre = new Genre(
                    genreRows.getLong("genre_id"),
                    genreRows.getString("name"));
            genreList.add(genre);
        }
        log.info("Получен список всех жанров. Количество жанров в списке = {}", genreList.size());
        return genreList;
    }
}

