package ru.yandex.practicum.filmorate.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import static ru.yandex.practicum.filmorate.fields.FieldsTable.*;
import static ru.yandex.practicum.filmorate.fields.FieldsTable.FILM_DESCRIPTION;
@Repository
public class FilmMapper implements RowMapper<Film> {

    final JdbcTemplate jdbcTemplate;

    final MpaRatingMapper mpaRatingMapper;
    final GenreMapper genreMapper;

    @Autowired
    public FilmMapper(JdbcTemplate jdbcTemplate, MpaRatingMapper mpaRatingMapper, GenreMapper genreMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaRatingMapper = mpaRatingMapper;
        this.genreMapper = genreMapper;
    }

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {

        return Film.builder()
                .id(rs.getLong(FILM_ID))
                .name(rs.getString(FILM_NAME))
                .description(rs.getString(FILM_DESCRIPTION))
                .releaseDate(Objects.requireNonNull(rs.getDate(RELEASE_DATE)).toLocalDate())
                .duration(rs.getLong(DURATION))
                .mpa(new MpaRating(rs.getLong(MPA_RATING_ID), rs.getString(MPA_RATING_NAME)))
                .genres(findGenres(rs.getLong(FILM_ID)))
                .build();
    }

    private List<Genre> findGenres(long filmId) {
        final String genreSql = "SELECT genres.genre_id, genres.genre_name " +
                "FROM genres "
                + "LEFT JOIN film_genres ON genres.genre_id = film_genres.genre_id "
                + "WHERE film_id = ? "
                + "ORDER BY genre_id;";

        return jdbcTemplate.query(genreSql, genreMapper, filmId);
    }
}