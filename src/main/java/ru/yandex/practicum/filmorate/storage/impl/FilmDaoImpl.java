package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.FilmDao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FilmDaoImpl implements FilmDao {

    private final JdbcTemplate jdbcTemplate;

    private final FilmMapper filmMapper;

    @Override
    public Collection<Film> getAllFilms() {
        log.debug("getAllFilms().");
        String sqlQuery = "SELECT films.*, ratings_mpa.* " +
                "FROM films " +
                "JOIN ratings_mpa on ratings_mpa.ratings_mpa_id = films.ratings_mpa_id;";
        log.info("Получаем все фильмы");
        return jdbcTemplate.query(sqlQuery, filmMapper);
    }

    @Override
    public Film getFilmById(long filmId) {
        log.debug("getFilmById({}).", filmId);
        checkFilm(filmId);
        String sqlQuery = "SELECT films.*, ratings_mpa.* " +
                "FROM films " +
                "JOIN ratings_mpa ON ratings_mpa.ratings_mpa_id = films.ratings_mpa_id " +
                "WHERE films.film_id = ?;";

        Film film = jdbcTemplate.queryForObject(sqlQuery, filmMapper, filmId);
        assert film != null;
        return film;

    }

    @Override
    public Film addFilm(Film film) {
        log.debug("addFilm({}).", film);
        String sqlQuery = "INSERT INTO films (name, description, release_date, duration, ratings_mpa_id) " +
                "VALUES (?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
                    PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
                    preparedStatement.setString(1, film.getName());
                    preparedStatement.setString(2, film.getDescription());
                    preparedStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
                    preparedStatement.setLong(4, film.getDuration());
                    preparedStatement.setLong(5, film.getMpa().getId());
                    return preparedStatement;
                },
                keyHolder);
        if (keyHolder.getKey() != null) {
            film.setId(keyHolder.getKey().longValue());
        }

        if (film.getGenres() != null) {

            filmGenresTable(film);
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        checkFilm(film.getId());
        log.debug("updateFilm({}).", film);

        log.debug("updateFilm({}).", film);
        String sqlQueryOnUpdate = "UPDATE films "
                + "SET name = ?, description = ?, release_date = ?, duration = ?, ratings_mpa_id = ? "
                + "WHERE film_id = ?";

        try {
            jdbcTemplate.update(sqlQueryOnUpdate,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());

        } catch (DataAccessException e) {
            throw new DataNotFoundException("Фильм с ID = " + film.getId() + " не найден");
        }

        if (film.getMpa() != null) {
            String updateMpa = "UPDATE films SET ratings_mpa_id = ? WHERE film_id = ?";
            jdbcTemplate.update(updateMpa, film.getMpa().getId(), film.getId());
        }

        if (film.getGenres() != null) {
            updateFilmGenresTable(film);
        }

        return getFilmById(film.getId());
    }

    @Override
    public void deleteFilm(long filmId) {
        log.debug("deleteFilm({}).", filmId);
        String sqlQuery = "DELETE FROM films " +
                "WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    private void filmGenresTable(Film film) {

        String sqlUpdateGenresQuery = "INSERT INTO film_genres (film_id, genre_id) " +
                "VALUES (?, ?)";

        String sqlCheck = "SELECT * FROM film_genres WHERE film_id = ? AND genre_id = ?";

        for (Genre genre : film.getGenres()) {

            SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sqlCheck, film.getId(), genre.getId());

            if (!genreRows.next()) {
                jdbcTemplate.update(sqlUpdateGenresQuery, film.getId(), genre.getId());
            }
        }
    }

    private void updateFilmGenresTable(Film film) {
        log.debug("updateFilmGenresTable({})", film);
        deleteFilmGenreById(film.getId());
        filmGenresTable(film);
    }

    @Override
    public void deleteFilmGenreById(long id) {
        log.debug("deleteFilmGenre({}).", id);
        String sql = "DELETE FROM film_genres " +
                "WHERE film_id = ?";
        jdbcTemplate.update(sql, id);
        log.debug("Удалены все жанры у фильма с ID {}.", id);
    }

    @Override
    public void addLike(long filmId, long userId) {
        log.debug("addLike({}, {}).", filmId, userId);
        checkFilm(filmId);
        checkUser(userId);
        String sqlQuery = "INSERT INTO likes (film_id, user_id) " +
                "VALUES (?,?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        log.info("Пользователь c ID: {} поставил лайк к фильму c ID: {}", userId, filmId);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        log.debug("deleteLike({}, {}).", filmId, userId);
        checkFilm(filmId);
        checkUser(userId);
        String sqlQuery = "DELETE FROM likes "
                + "WHERE film_id = ? "
                + "AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        log.info("Пользователь c ID: {} удалил лайк у фильма c ID: {}", userId, filmId);
    }

    @Override
    public Collection<Film> getPopularFilms(long count) {
        log.debug("getPopularFilms({}).", count);
        String sqlQuery = "SELECT f.*, rm.ratings_mpa_name "
                + "FROM films AS f "
                + "INNER JOIN ratings_mpa AS rm ON f.ratings_mpa_id = rm.ratings_mpa_id "
                + "LEFT OUTER JOIN likes AS l ON f.film_id = l.film_id "
                + "GROUP BY f.film_id, l.user_id "
                + "ORDER BY COUNT(l.user_id) DESC "
                + "LIMIT ?;";
        log.info("Получаем топ {} самых популярных фильмов", count);
        return jdbcTemplate.query(sqlQuery, filmMapper, count);
    }

    private void checkUser(long userId) {

        String sqlQueryUser = "SELECT * FROM users WHERE user_id = ?";

        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQueryUser, userId);

        if (!userRows.next()) {
            throw new DataNotFoundException("Пользователь с ID=" + userId + " не найден!");
        }
    }

    private void checkFilm(long filmId) {

        String sqlQueryFilm = "SELECT * FROM films WHERE film_id = ?";

        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQueryFilm, filmId);

        if (!filmRows.next()) {
            throw new DataNotFoundException("Фильм с ID=" + filmId + " не найден!");
        }
    }
}
