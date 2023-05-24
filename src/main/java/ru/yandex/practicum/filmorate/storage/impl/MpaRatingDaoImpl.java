package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.film.MpaRating;
import ru.yandex.practicum.filmorate.storage.dao.MpaRatingDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static ru.yandex.practicum.filmorate.fields.FieldsTable.*;


@Slf4j
@Repository
public class MpaRatingDaoImpl implements MpaRatingDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaRatingDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    @Override
    public List<MpaRating> getAllMpaRatings() {
        log.debug("getAllMpaRatings().");
        String sqlQuery = "SELECT * FROM ratings_mpa;";
        List<MpaRating> listMPA = jdbcTemplate.query(sqlQuery, this::makeMPA);
        log.info("Получены все рейтинги MPA: {}.", listMPA);
        return listMPA;
    }

    @Transactional
    @Override
    public MpaRating getRatingById(int id) {
        log.debug("getRatingById({}).", id);
        SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT * FROM ratings_mpa WHERE ratings_mpa_id = ?", id);
        if (rs.next()) {
            return MpaRating.builder()
                    .id(rs.getInt(MPA_RATING_ID))
                    .name(rs.getString(MPA_RATING_NAME))
                    .build();
        } else {
            throw new IllegalArgumentException("Не найден рейтинг с id = " + id);
        }
    }

    private MpaRating makeMPA(ResultSet rs, int rowNum) throws SQLException {
        return MpaRating.builder()
                .id(rs.getInt(MPA_RATING_ID))
                .name(rs.getString(MPA_RATING_NAME))
                .description(rs.getString(MPA_RATING_DESCRIPTION))
                .build();
    }
}
