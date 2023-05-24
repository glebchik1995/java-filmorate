package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.MpaRating;
import ru.yandex.practicum.filmorate.storage.dao.FilmDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static ru.yandex.practicum.filmorate.storage.сonstant.FieldsTable.*;

@Slf4j
@Repository
public class FilmDaoImpl implements FilmDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    private Film makeFilm(ResultSet rs) throws SQLException {
        return Film.builder()
                .id(rs.getInt(FILM_ID))
                .name(rs.getString(FILM_NAME))
                .description(rs.getString(FILM_DESCRIPTION))
                .releaseDate(rs.getDate(RELEASE_DATE).toLocalDate())
                .duration(rs.getInt(DURATION))
                .mpa(new MpaRating(rs.getInt(MPA_RATING_ID), rs.getString(MPA_RATING_NAME),
                        rs.getString(FILM_DESCRIPTION)))
                .genres(new LinkedHashSet<>())
                .build();
    }

    @Transactional
    @Override
    public List<Film> getAllFilms() {
        log.debug("getAllFilms().");
        String query = "SELECT films.*,ratings_mpa.mpa_name FROM films " +
                "INNER JOIN ratings_mpa ON films.ratings_mpa_id = ratings_mpa.ratings_mpa_id";
        List<Film> listFilm = jdbcTemplate.query(query, (rs, rowNum) -> makeFilm(rs));
        listFilm.forEach(film -> film.getGenres().addAll(getFilmsGenreById(film.getId())));
        log.info("Получены все фильмы: {}.", listFilm);
        return listFilm;
    }

    @Transactional
    private Set<Genre> getFilmsGenreById(int id) {
        String query = "SELECT DISTINCT * FROM genres "
                + "RIGHT JOIN film_genres on genres.genre_id = film_genres.genre_id "
                + "WHERE film_genres.film_id = ?" +
                "order by GENRES.GENRE_ID";
        return new LinkedHashSet<>(jdbcTemplate.query(query, (rs, rowNum) ->
                new Genre(rs.getInt("genre_id"), rs.getString("genre_name")), id));
    }

    @Transactional
    @Override
    public Film addFilm(Film film) {
        log.debug("addFilm({}).", film);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns(FILM_ID);
        int result = simpleJdbcInsert.executeAndReturnKey(toMap(film)).intValue();
        MpaRating mpa = makeMpaById(film.getMpa().getId());
        film.setId(result);
        film.setMpa(mpa);
        if (film.getGenres() != null)
            filmGenresTable(film);
        film.setGenres(getFilmsGenreById(result));
        log.info("В хранилище сохранен фильм: {}.", result);
        return film;
    }

    @Transactional
    private void filmGenresTable(Film film) {
        String sql = "INSERT INTO film_genres (film_id, genre_id) " +
                "VALUES (?, ?)";
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(sql,
                    film.getId(),
                    genre.getId());
        }
    }

    @Transactional
    @Override
    public Film updateFilm(Film film) {
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

        updateFilmGenresTable(film);
        MpaRating mpa = makeMpaById(film.getMpa().getId());
        film.setMpa(mpa);
        film.setGenres(getFilmsGenreById(film.getId()));

        return getFilmById(film.getId());
    }

    @Transactional
    private void updateFilmGenresTable(Film film) {
        log.debug("updateFilmGenresTable({})", film);
        deleteGenresByFilmId(film);
        filmGenresTable(film);
    }

    @Transactional
    private void deleteGenresByFilmId(Film film) {
        log.debug("deleteGenresByFilmId({}).", film);
        jdbcTemplate.update("DELETE "
                + "FROM film_genres "
                + "WHERE film_id=?", film.getId());
        log.debug("Удалены все жанры у фильма {}.", film.getId());
    }

    @Transactional
    @Override
    public Film getFilmById(int id) {
        log.debug("getFilmById({}).", id);
        String sqlQuery = "SELECT films.*,ratings_mpa.mpa_name FROM films,ratings_mpa "
                + "WHERE films.film_id = ? "
                + "AND films.ratings_mpa_id = ratings_mpa.ratings_mpa_id";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(
                sqlQuery, id);
        if (rs.next()) {
            Film film = Film.builder()
                    .id(rs.getInt(FILM_ID))
                    .name(rs.getString(FILM_NAME))
                    .description(rs.getString(FILM_DESCRIPTION))
                    .releaseDate(Objects.requireNonNull(rs.getDate(RELEASE_DATE)).toLocalDate())
                    .duration(rs.getInt(DURATION))
                    .mpa(new MpaRating(rs.getInt(MPA_RATING_ID), rs.getString(MPA_RATING_NAME), rs.getString(FILM_DESCRIPTION)))
                    .genres(new LinkedHashSet<>())
                    .build();
            film.getGenres().addAll(getFilmsGenreById(film.getId()));
            return film;
        } else {
            throw new DataNotFoundException("Фильм с ID = " + id + " не найден.");
        }
    }

    @Transactional
    @Override
    public List<Film> getPopularFilms(int count) {
        log.debug("getPopularFilms({}).", count);
        String sqlQuery = "SELECT f.*, rm.mpa_name "
                + "FROM films AS f "
                + "INNER JOIN ratings_mpa AS rm ON f.ratings_mpa_id = rm.ratings_mpa_id "
                + "LEFT OUTER JOIN likes AS l ON f.film_id = l.film_id "
                + "GROUP BY f.film_id, l.user_id "
                + "ORDER BY COUNT(l.user_id) DESC "
                + "LIMIT ?;";
        List<Film> popularFilms = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs), count);
        log.info("Получены список популярных:{} фильмов. Количество фильмов в списке: {}.", popularFilms, count);
        return popularFilms;
    }

    @Transactional
    @Override
    public void deleteFilm(int filmId) {
        log.debug("deleteFilm({}).", filmId);
        String sqlQuery = "DELETE "
                + "FROM film_genres "
                + "WHERE film_id = ?";
        if (jdbcTemplate.update(sqlQuery, filmId) != 0) {
            jdbcTemplate.update(sqlQuery, filmId);
            log.info("Удален фильм с ID: {}", filmId);
        } else
            throw new DataNotFoundException("Фильм с ID=" + filmId + " не найден!");
    }

    @Transactional
    @Override
    public void putLike(int filmId, int userId) {
        log.debug("putLike({}, {}).", filmId, userId);
        String sqlQuery = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Transactional
    @Override
    public void deleteLike(int filmId, int userId) {
        log.debug("deleteLike({}, {}).", filmId, userId);
        if (userId < 0) {
            throw new DataNotFoundException("Не найден пользователь с id = " + userId);
        }
        String sqlQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        log.info("Пользователь с ID={} удаляет ♥ у фильма с ID={}.", userId, filmId);
    }

    private Map<String, Object> toMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put(FILM_NAME, film.getName());
        values.put(FILM_DESCRIPTION, film.getDescription());
        values.put(RELEASE_DATE, film.getReleaseDate());
        values.put(DURATION, film.getDuration());
        values.put(MPA_RATING_ID, film.getMpa().getId());
        return values;
    }

    @Transactional
    private MpaRating makeMpaById(int mpaId) {
        String sqlQuery = "SELECT mpa_name FROM ratings_mpa WHERE ratings_mpa_id = ?";
        String mpaName = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeMpaName(rs), mpaId).get(0);
        return MpaRating.builder()
                .id(mpaId)
                .name(mpaName)
                .build();
    }

    private String makeMpaName(ResultSet rs) throws SQLException {
        return rs.getString(MPA_RATING_NAME);
    }

}
