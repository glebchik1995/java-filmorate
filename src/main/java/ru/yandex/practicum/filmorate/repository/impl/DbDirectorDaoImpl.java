package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectUpdateException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.repository.DirectorDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DbDirectorDaoImpl implements DirectorDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Director> getAllDirectors() {

        String sqlQuery = "SELECT * " +
                "          FROM director";

        List<Director> directors = jdbcTemplate.query(sqlQuery, this::makeDirector);

        log.info("Получены все режиссеры. Количество режиссеров в списке = {}", directors.size());

        return directors;
    }

    @Override
    public Optional<Director> getDirectorById(Long directorId) {

        String sqlQuery = "SELECT * " +
                "          FROM director " +
                "          WHERE director_id = ?";

        List<Director> directors = jdbcTemplate.query(sqlQuery, this::makeDirector, directorId);

        if (!directors.isEmpty()) {
            log.info("Получен режиссер с ID={}", directorId);
            return Optional.of(directors.get(0));
        } else {
            throw new DataNotFoundException(String.format("Режиссер с ID=%d не найден.", directorId));
        }
    }

    @Override
    public Optional<Director> createDirector(Director director) {

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("director")
                .usingGeneratedKeyColumns("director_id");

        Long directorId = simpleJdbcInsert.executeAndReturnKey(Collections.singletonMap("name", director.getName())).longValue();

        log.info("Добавлен режиссер с ID={}", directorId);

        return getDirectorById(directorId);
    }

    @Override
    public Optional<Director> updateDirector(Director director) {

        String sqlQuery = "UPDATE director SET name = ? " +
                "          WHERE director_id = ? ";

        int response = jdbcTemplate.update(sqlQuery,
                director.getName(), director.getId());
        if (response == 1) {
            log.info("Изменен режиссер с ID={}", director.getId());
            return getDirectorById(director.getId());
        } else {
            throw new ObjectUpdateException(String.format("Ошибка обновления режиссера c ID=%d!", director.getId()));
        }
    }

    @Override
    public void deleteDirectorById(Long directorId) {

        String sqlQuery = "DELETE FROM director " +
                "          WHERE director_id = ?";

        int response = jdbcTemplate.update(sqlQuery, directorId);
        if (response != 1) {
            throw new ObjectUpdateException(String.format("Режиссер с ID=%d не удален!", directorId));
        } else {
            jdbcTemplate.update(sqlQuery, directorId);
            log.info("Директор с ID={} удален.", directorId);
        }
    }


    private Director makeDirector(ResultSet rs, int rowNum) throws SQLException {
        return new Director(
                rs.getLong("director_id"),
                rs.getString("name")
        );
    }
}
