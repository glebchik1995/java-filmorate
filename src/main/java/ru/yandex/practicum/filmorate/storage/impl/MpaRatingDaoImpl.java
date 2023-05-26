package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaRatingMapper;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.dao.MpaRatingDao;

import java.util.Collection;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MpaRatingDaoImpl implements MpaRatingDao {

    private final JdbcTemplate jdbcTemplate;
    private final MpaRatingMapper mpaRatingMapper = new MpaRatingMapper();

    @Override
    public Collection<MpaRating> getAllMpaRatings() {
        log.debug("getAllMpaRatings().");
        String sqlQuery = "SELECT * FROM ratings_mpa "
                + "ORDER BY ratings_mpa_id;";
        log.info("Получаем все рейтинги");
        return jdbcTemplate.query(sqlQuery, mpaRatingMapper);
    }

    @Override
    public MpaRating getMpaRatingById(long id) {
        log.debug("getRatingById({}).", id);
        String sqlQuery = "SELECT * "
                + "FROM ratings_mpa "
                + "WHERE ratings_mpa_id = ?";

        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (!mpaRows.next()) {
            throw new DataNotFoundException("Рейтинг c ID= " + id +  "не найден");
        }

        return jdbcTemplate.queryForObject(sqlQuery, mpaRatingMapper, id);

    }
}
