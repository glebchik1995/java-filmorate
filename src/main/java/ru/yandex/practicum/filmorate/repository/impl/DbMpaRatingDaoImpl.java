package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.repository.MpaRatingDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DbMpaRatingDaoImpl implements MpaRatingDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<MpaRating> getAllMpaRatings() {

        String sqlQuery = "SELECT * " +
                "          FROM mpa_rating " +
                "          ORDER BY rating_id";

        List<MpaRating> ratings = jdbcTemplate.query(sqlQuery, this::makeMpaRating);

        log.info("Получен список всех рейтингов. Количество позиций рейтинга в списке = {}", ratings.size());

        return ratings;
    }

    @Override
    public MpaRating getMpaRatingById(Long ratingId) {

        String sqlQuery = "SELECT * " +
                "          FROM mpa_rating " +
                "          WHERE rating_id = ?";

        List<MpaRating> ratings = jdbcTemplate.query(sqlQuery, this::makeMpaRating, ratingId);

        if (ratings.size() == 1) {
            log.info("Получен рейтинги с ID={}", ratingId);
            return ratings.get(0);
        } else {
            throw new DataNotFoundException(String.format("Ошибка получения рейтинга. Рейтинг не найден! Id=%d", ratingId));
        }
    }

    private MpaRating makeMpaRating(ResultSet rs, int rowNum) throws SQLException {
        return new MpaRating(
                rs.getLong("rating_id"),
                rs.getString("rating_name"));
    }
}
