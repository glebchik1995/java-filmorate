package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectUpdateException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.repository.DirectorDao;
import ru.yandex.practicum.filmorate.repository.FilmDao;
import ru.yandex.practicum.filmorate.repository.UserDao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DbFilmDaoImpl implements FilmDao {

    private final JdbcTemplate jdbcTemplate;

    private final UserDao userDao;

    private final DirectorDao directorDao;

    @Override
    public List<Film> getAllFilms() {

        String sqlQuery = "SELECT * " +
                "          FROM film f " +
                "          INNER JOIN mpa_rating mr ON (f.rating_id = mr.rating_id) " +
                "          ORDER BY film_id";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm);

        log.info("Получены все фильмы. Количество пользователей в списке = {}", films.size());

        return getPopularBySql(films);
    }

    @Override
    public Film getFilmById(Long filmId) {

        String sqlQuery = "SELECT * FROM film f " +
                "          INNER JOIN mpa_rating m " +
                "          ON (f.rating_id = m.rating_id) " +
                "          WHERE f.film_id=? ";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm, filmId);

        Map<Long, List<Genre>> filmGenresMap = loadFilmsGenres(films);
        Map<Long, List<Director>> filmDirectorsMap = loadFilmsDirectors(films);

        if (!films.isEmpty()) {
            Film film = films.get(0);
            film.getGenres().addAll(filmGenresMap.getOrDefault(film.getId(), new ArrayList<>()));
            film.getDirectors().addAll(filmDirectorsMap.getOrDefault(film.getId(), new ArrayList<>()));
            log.info("Получен фильм с ID={}", filmId);
            return film;
        } else {
            throw new DataNotFoundException(String.format("Ошибка получения фильма. Фильм с ID=%d не найден!", filmId));
        }
    }

    @Override
    public List<Film> getFilmsByUserId(Long userId) {

        String sqlQuery = "SELECT f.*, m.rating_id, m.rating_name " +
                "          FROM film f " +
                "          JOIN likes l ON (l.film_id = f.film_id) " +
                "          JOIN mpa_rating m ON (f.rating_id = m.rating_id) " +
                "          WHERE l.user_id = ?";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm, userId);

        log.info("Получены все фильмы у пользователя с ID={}. Количество фильмов в списке = {}",userId, films.size());

        return films;
    }

    @Override
    public List<Film> getFilmsByFriendId(Long friendId) {

        String sqlQuery = "SELECT f.*, m.rating_id, m.rating_name " +
                "          FROM film f " +
                "          JOIN likes l ON (l.film_id = f.film_id) " +
                "          JOIN mpa_rating m ON (f.rating_id = m.rating_id) " +
                "          WHERE l.user_id = ?";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm, friendId);

        log.info("Получены все фильмы у пользователя с ID={}. Количество фильмов в списке = {}", friendId, films.size());

        return films;
    }

    @Override
    public Film createFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film")
                .usingGeneratedKeyColumns("film_id");
        Long filmId = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();
        film.getGenres().addAll(addFilmGenresByFilmId(filmId, film.getGenres()));
        film.getDirectors().addAll(addFilmDirectorsByFilmId(filmId, film.getDirectors()));
        film = getFilmById(filmId);
        log.info("Создан фильм с ID={} ", film.getId());
        return film;
    }
    @Override
    public Film updateFilm(Film film) {

        String sqlQuery = "UPDATE film " +
                "          SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
                "          WHERE film_id = ? ";

        if (jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()) == 0) {

            log.info("Фильм с идентификатором {} не изменен.", film.getId());
            throw new ObjectUpdateException(String.format("Ошибка обновления фильма c ID=%d?", film.getId()));
        }

        String sqlQueryDeleteGenres = "DELETE " +
                "                      FROM film_genre " +
                "                      WHERE film_id = ?";

        jdbcTemplate.update(sqlQueryDeleteGenres, film.getId());
        saveGenresFromOneFilm(film, jdbcTemplate);

        String sqlQueryDeleteDirectors = "DELETE " +
                "                         FROM film_director " +
                "                         WHERE film_id = ?";

        jdbcTemplate.update(sqlQueryDeleteDirectors, film.getId());
        saveDirectorsFromOneFilm(film, jdbcTemplate);

        return getFilmById(film.getId());
    }

    @Override
    public Film addUserLike(Long filmId, Long userId) {

        String sqlQuery = "INSERT INTO likes(film_id, user_id) " +
                "          SELECT ?, ? " +
                "          WHERE NOT EXISTS (" +
                "                            SELECT 1 " +
                "                            FROM likes " +
                "                            WHERE film_id = ? AND user_id = ?" +
                "                            )";

        jdbcTemplate.update(sqlQuery,
                filmId, userId,
                filmId, userId);

        userDao.addEvent(new Event(userId, Event.EventType.LIKE, Event.Operation.ADD, filmId));

        return getFilmById(filmId);
    }

    @Override
    public Film deleteUsersLike(Long filmId, Long userId) {

        String sqlQuery = "DELETE FROM likes " +
                "          WHERE film_id = ? AND user_id = ?";

        int removedRowsNum = jdbcTemplate.update(sqlQuery, filmId, userId);

        if (removedRowsNum == 0) {
            throw new DataNotFoundException(String.format("Ошибка обновления фильма! Не найден лайк FilmId=%d, UserId=%d", filmId, userId)
            );
        }
        userDao.addEvent(new Event(userId, Event.EventType.LIKE, Event.Operation.REMOVE, filmId));
        return getFilmById(filmId);
    }

    @Override
    public void deleteFilmById(Long filmId) {

        getFilmById(filmId);

        String sqlQuery = "DELETE FROM film " +
                "          WHERE film_id = ?";

        jdbcTemplate.update(sqlQuery, filmId);
        log.debug("Фильм с ID = {} удален.", filmId);
    }

    @Override
    public List<Film> getPopularFilmsByCount(Integer count) {

        String sqlQuery = "SELECT f.*, mr.rating_name " +
                "          FROM film f " +
                "          INNER JOIN mpa_rating mr USING (rating_id) " +
                "          LEFT OUTER JOIN likes l USING (film_id) " +
                "          GROUP BY f.film_id, mr.rating_name " +
                "          ORDER BY COUNT(l.user_id) DESC , f.film_id " +
                "          LIMIT ?";

        log.info("Получен топ {} самых популярных фильмов.", count);

        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm, count);

        return getPopularBySql(films);
    }

    @Override
    public List<Film> getPopularByYear(Integer releaseYear, Integer count) {

        String sqlQuery = "SELECT f.*, mpa_rating.rating_name " +
                "          FROM film f " +
                "          LEFT JOIN likes l USING(film_id) " +
                "          INNER JOIN mpa_rating USING(rating_id)" +
                "          WHERE extract(YEAR FROM release_date) = ? " +
                "          GROUP BY f.film_id, f.name, mpa_rating.rating_name " +
                "          ORDER BY COUNT(l.user_id) DESC , f.film_id " +
                "          LIMIT ?";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm, releaseYear, count);

        log.info("Получен топ {} самых популярных фильмов c датой релиза: {} ", count, releaseYear);

        return getPopularBySql(films);
    }

    @Override
    public List<Film> getPopularByGenreAndYear(Long genreId, Integer releaseYear, Integer count) {

        String sqlQuery = "SELECT f.*, mpa_rating.rating_name " +
                "          FROM film f " +
                "          LEFT JOIN likes l USING(film_id) " +
                "          JOIN film_genre fg USING(film_id) " +
                "          INNER JOIN mpa_rating USING(rating_id)" +
                "          WHERE fg.genre_id = ? AND " +
                "          EXTRACT(YEAR FROM release_date) = ? " +
                "          GROUP BY f.film_id, f.name, mpa_rating.rating_name " +
                "          ORDER BY COUNT(l.user_id) DESC, f.film_id " +
                "          LIMIT ?";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm, genreId, releaseYear, count);

        log.info("Получаем топ {} самых популярных фильмов c ID жанра :{} и датой релиза {}. ", count, genreId, releaseYear);

        return getPopularBySql(films);
    }

    @Override
    public List<Film> getPopularByGenre(Long genreId, Integer count) {

        String sqlQuery = "SELECT f.*, mpa_rating.rating_name " +
                "          FROM film f " +
                "          LEFT JOIN likes l USING(film_id) " +
                "          JOIN film_genre as fg USING(film_id) " +
                "          INNER JOIN mpa_rating USING(rating_id)" +
                "          WHERE fg.genre_id = ? " +
                "          GROUP BY f.film_id, f.name, mpa_rating.rating_name " +
                "          ORDER BY COUNT(l.user_id) DESC , f.film_id " +
                "          LIMIT ?";

        log.info("Получен топ {} самых популярных фильмов c ID жанра :{}.", count, genreId);

        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm, genreId, count);

        return getPopularBySql(films);
    }

    @Override
    public List<Film> getRecommendations(Long userId) {
        String sqlQuery =
                "        SELECT * " +
                        "       FROM FILM f " +
                        "       JOIN MPA_RATING r ON (r.RATING_ID = f.RATING_ID) " +
                        "       WHERE FILM_ID IN (" +
                        "                         SELECT l2.FILM_ID " +
                        "                         FROM LIKES l2 " +
                        "                         WHERE l2.USER_ID = (" +
                        "                                             SELECT USER_ID " +
                        "                                             FROM LIKES l " +
                        "                                             WHERE USER_ID != " + userId + " " +
                        "                                             AND FILM_ID IN (" +
                        "                                                             SELECT FILM_ID " +
                        "                                                             FROM LIKES l2 " +
                        "                                                             WHERE USER_ID = " + userId +
                        "                                                             ) " +
                        "                                             GROUP BY USER_ID " +
                        "                                             ORDER BY COUNT(FILM_ID) DESC " +
                        "                                             LIMIT 1" +
                        "                                             ) AND l2.FILM_ID NOT IN " +
                        "                                                                    (" +
                        "                                                                     SELECT l.FILM_ID " +
                        "                                                                     FROM LIKES l " +
                        "                                                                     WHERE l.USER_ID = " + userId +
                        "                                                                      )" +
                        "                         )";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm);
        log.info("Получен список с фильмами, которые рекомендует пользователь с ID={}.", userId);
        return getPopularBySql(films);
    }

    @Override
    public List<Film> getByTitleContaining(String query) {

        String sqlQuery = "SELECT F.*, m.rating_id, m.rating_name " +
                "          FROM FILM F " +
                "          JOIN mpa_rating m ON F.rating_id = m.rating_id " +
                "          WHERE LOWER(NAME) LIKE ?";

        String param = "%" + query.toLowerCase() + "%";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm, param);
        log.info("Получен список фильмов, название которых содержит: {}.", query);
        return getPopularBySql(films);
    }

    @Override
    public List<Film> getByDirectorContaining(String query) {

        String sqlQuery = "SELECT F.*, m.rating_id, m.rating_name " +
                "          FROM FILM F" +
                "          JOIN FILM_DIRECTOR FD ON F.FILM_ID = FD.FILM_ID " +
                "          JOIN DIRECTOR D ON FD.DIRECTOR_ID = D.DIRECTOR_ID " +
                "          JOIN mpa_rating m ON F.rating_id = m.rating_id" +
                "          WHERE D.NAME LIKE ?";

        String param = "%" + query.toLowerCase() + "%";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm, param);
        log.info("Получен список фильмов, где в имени режиссера содержится: {}.", query);
        return getPopularBySql(films);
    }

    @Override
    public List<Film> getByTitleContainingOrDirectorContaining(String titleQuery, String directorQuery) {

        String sql = "SELECT F.*, m.rating_id, m.rating_name, COUNT(L.USER_ID) LIKE_COUNT " +
                "     FROM FILM F " +
                "     LEFT JOIN FILM_DIRECTOR FD ON F.FILM_ID = FD.FILM_ID " +
                "     LEFT JOIN DIRECTOR D ON FD.DIRECTOR_ID = D.DIRECTOR_ID " +
                "     JOIN mpa_rating m ON F.rating_id = m.rating_id " +
                "     LEFT JOIN LIKES L ON F.FILM_ID = L.FILM_ID " +
                "     WHERE LOWER(F.NAME) LIKE ? OR LOWER(D.NAME) LIKE ? " +
                "     GROUP BY F.FILM_ID, M.RATING_ID, M.RATING_NAME " +
                "     ORDER BY LIKE_COUNT DESC";

        String titleParam = "%" + titleQuery.toLowerCase() + "%";
        String directorParam = "%" + directorQuery.toLowerCase() + "%";
        List<Film> films = jdbcTemplate.query(sql, this::makeFilm, titleParam, directorParam);
        log.info("Получен список фильмов, название которых содержит: {} или где в имени режиссера содержится: {}.",
                titleQuery, directorQuery);

        return getPopularBySql(films);
    }

    @Override
    public List<Film> getDirectorFilmsSortedByYear(Long directorId) {

        directorDao.getDirectorById(directorId);

        String sqlQuery = "SELECT * " +
                "          FROM film_director fd " +
                "          INNER JOIN film f ON fd.film_id = f.film_id " +
                "          INNER JOIN mpa_rating m ON m.rating_id = f.rating_id " +
                "          WHERE fd.director_id=? " +
                "          ORDER BY f.release_date";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm, directorId);

        log.info("Получен отсортированный список фильмов по годам, где режиссер с ID={} ", directorId);

        return getPopularBySql(films);
    }


    @Override
    public List<Film> getDirectorFilmsSortedByLikes(Long directorId) {

        directorDao.getDirectorById(directorId);

        String sqlQuery = "SELECT *, " +
                "                    (" +
                "                     SELECT COUNT(*) " +
                "                     FROM likes " +
                "                     WHERE film_id = f.film_id" +
                "                     ) like_count " +
                "          FROM film_director fd " +
                "          INNER JOIN film f ON fd.film_id = f.film_id " +
                "          INNER JOIN mpa_rating m ON m.rating_id = f.rating_id " +
                "          WHERE fd.director_id=? " +
                "          ORDER BY like_count DESC";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::makeFilm, directorId);

        log.info("Получен отсортированный список фильмов по лайкам, где режиссер с ID={} ", directorId);

        return getPopularBySql(films);
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        return new Film(
                rs.getLong("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                Objects.requireNonNull(rs.getDate("release_date")).toLocalDate(),
                rs.getLong("duration"),
                new MpaRating(rs.getLong("rating_id"), rs.getString("rating_name"))
        );
    }

    private Map<Long, List<Director>> loadFilmsDirectors(List<Film> films) {
        return loadFilmsDirectorsByFilmsIds(films.stream()
                .map(Film::getId)
                .collect(Collectors.toList()));
    }

    private Map<Long, List<Genre>> loadFilmsGenresByFilmsIds(List<Long> filmsIds) {

        Map<Long, List<Genre>> filmGenresMap = new HashMap<>();

        if (!filmsIds.isEmpty()) {

            String questionMarks = String.join(", ", Collections.nCopies(filmsIds.size(), "?"));

            String query = "SELECT DISTINCT fg.film_id, g.genre_id, g.name " +
                    "       FROM genre g " +
                    "       INNER JOIN film_genre fg ON (fg.genre_id = g.genre_id) " +
                    "       WHERE fg.film_id in (" + questionMarks + ")";

            jdbcTemplate.query(query, rs -> {

                        long filmId = rs.getLong("film_id");

                        Genre genre = new Genre(rs.getLong("genre_id"), rs.getString("name"));

                        filmGenresMap.computeIfAbsent(filmId, key -> new ArrayList<>()).add(genre);
                    },
                    filmsIds.toArray());
        }
        return filmGenresMap;
    }

    private Map<Long, List<Director>> loadFilmsDirectorsByFilmsIds(List<Long> filmsIds) {

        Map<Long, List<Director>> filmDirectorsMap = new HashMap<>();

        if (!filmsIds.isEmpty()) {

            String questionMarks = String.join(", ", Collections.nCopies(filmsIds.size(), "?"));

            String query = "SELECT DISTINCT fd.film_id, d.director_id, d.name " +
                    "       FROM director d " +
                    "       INNER JOIN film_director fd ON (fd.director_id = d.director_id) " +
                    "       WHERE fd.film_id IN (" + questionMarks + ")";

            jdbcTemplate.query(query, rs -> {

                        long filmId = rs.getLong("film_id");

                        Director director = new Director(rs.getLong("director_id"), rs.getString("name"));

                        filmDirectorsMap.computeIfAbsent(filmId, key -> new ArrayList<>()).add(director);
                    },
                    filmsIds.toArray());
        }
        return filmDirectorsMap;
    }

    private Map<Long, List<Genre>> loadFilmsGenres(List<Film> films) {
        return loadFilmsGenresByFilmsIds(films.stream()
                .map(Film::getId)
                .collect(Collectors.toList()));
    }

    private List<Genre> loadFilmGenresByFilmId(Long filmId) {
        return loadFilmsGenresByFilmsIds(List.of(filmId)).getOrDefault(filmId, Collections.emptyList());
    }

    private List<Director> loadFilmDirectorsByFilmId(Long filmId) {
        return loadFilmsDirectorsByFilmsIds(List.of(filmId)).getOrDefault(filmId, Collections.emptyList());
    }

    private void addFilmGenreByFilmId(Long filmId, Genre genre) {

        String sqlQuery = "INSERT INTO film_genre(film_id, genre_id) " +
                "          SELECT ?, ?" +
                "          WHERE NOT EXISTS (" +
                "                            SELECT 1 " +
                "                            FROM film_genre " +
                "                            WHERE film_id = ? AND genre_id = ?" +
                "                            )";

        jdbcTemplate.update(sqlQuery, filmId, genre.getId(), filmId, genre.getId());
    }

    private void addFilmDirectorByFilmId(Long filmId, Director director) {

        String sqlQuery = "INSERT INTO film_director(film_id, director_id) " +
                "          SELECT ?, ?" +
                "          WHERE NOT EXISTS (SELECT 1 " +
                "                            FROM film_director " +
                "                            WHERE film_id = ? AND director_id = ?" +
                "                            )";

        jdbcTemplate.update(sqlQuery, filmId, director.getId(), filmId, director.getId());
    }

    private Collection<Genre> addFilmGenresByFilmId(Long filmId, Collection<Genre> filmGenres) {
        filmGenres.forEach(genre -> addFilmGenreByFilmId(filmId, genre));
        return loadFilmGenresByFilmId(filmId);
    }

    private Collection<Director> addFilmDirectorsByFilmId(Long filmId, Collection<Director> filmDirectors) {
        filmDirectors.forEach(director -> addFilmDirectorByFilmId(filmId, director));
        return loadFilmDirectorsByFilmId(filmId);
    }

    private List<Film> getPopularBySql(List<Film> films) {
        Map<Long, List<Genre>> filmGenresMap = loadFilmsGenres(films);
        Map<Long, List<Director>> filmDirectorsMap = loadFilmsDirectors(films);
        for (Film film : films) {
            film.getGenres().addAll(filmGenresMap.getOrDefault(film.getId(), new ArrayList<>()));
            film.getDirectors().addAll(filmDirectorsMap.getOrDefault(film.getId(), new ArrayList<>()));
        }
        return films;
    }

    private void saveGenresFromOneFilm(Film film, JdbcTemplate jdbcTemplate) {

        String sqlQueryAddGenres = "INSERT INTO film_genre (film_id, genre_id) " +
                "                   VALUES (?, ?)";

        List<Genre> genres = new ArrayList<>(film.getGenres());
        jdbcTemplate.batchUpdate(sqlQueryAddGenres, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(@NotNull PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, film.getId());
                ps.setLong(2, genres.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });

        log.info("У фильма с ID = {} записаны жанры.", film.getId());
    }

    private void saveDirectorsFromOneFilm(Film film, JdbcTemplate jdbcTemplate) {

        List<Director> directors = new ArrayList<>(film.getDirectors());

        String sqlQueryAddDirectors = "INSERT INTO film_director (director_id, film_id) " +
                "                      VALUES (?, ?)";

        jdbcTemplate.batchUpdate(sqlQueryAddDirectors, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(@NotNull PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, directors.get(i).getId());
                ps.setLong(2, film.getId());
            }

            @Override
            public int getBatchSize() {
                return directors.size();
            }
        });

        log.info("У фильма с ID = {} записаны режиссеры.", film.getId());
    }

}
