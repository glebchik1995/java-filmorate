package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.ReviewDao;
import ru.yandex.practicum.filmorate.repository.UserDao;

import java.util.LinkedList;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class DbReviewDaoImpl implements ReviewDao {

    private final UserDao userDao;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review createReview(Review review) {

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("review")
                .usingGeneratedKeyColumns("review_id");

        review.setReviewId(simpleJdbcInsert.executeAndReturnKey(review.toMap()).longValue());

        log.info("Создан отзыв с ID={}", review.getReviewId());

        userDao.addEvent(new Event(
                review.getUserId(),
                Event.EventType.REVIEW,
                Event.Operation.ADD,
                review.getReviewId()));

        return getReviewById(review.getReviewId());
    }

    @Override
    public Review updateReview(Review review) {

        Review reviewEx = getReviewById(review.getReviewId());

        String sqlQuery = "UPDATE review " +
                "          SET(content, is_positive) = (?, ?) " +
                "          WHERE review_id = ?";

        jdbcTemplate.update(sqlQuery, review.getContent(), review.getIsPositive(), review.getReviewId());

        log.info("Изменен отзыв с ID={}", review.getReviewId());

        userDao.addEvent(new Event(
                reviewEx.getUserId(),
                Event.EventType.REVIEW,
                Event.Operation.UPDATE,
                review.getReviewId()));

        return getReviewById(review.getReviewId());
    }

    @Override
    public void removeReview(Long reviewId) {

        Review review = getReviewById(reviewId);

        userDao.addEvent(new Event(
                review.getUserId(),
                Event.EventType.REVIEW,
                Event.Operation.REMOVE,
                review.getReviewId()));

        String sqlQuery = "DELETE FROM review " +
                "          WHERE review_id = ?";

        jdbcTemplate.update(sqlQuery, reviewId);

        log.info("Удален отзыв с ID={}", review.getReviewId());
    }

    @Override
    public Review getReviewById(Long reviewId) {

        String sqlQuery = "SELECT * " +
                "          FROM review " +
                "          WHERE review_id = ?";

        var rowMapper = BeanPropertyRowMapper.newInstance(Review.class);

        List<Review> reviews = jdbcTemplate.query(sqlQuery, rowMapper, reviewId);

        if (reviews.size() == 1) {
            log.info("Получен отзыв с ID={}", reviewId);
            return reviews.get(0);
        } else {
            throw new DataNotFoundException(String.format("Ошибка получения отзыва. Отзыв с ID=%d не найден!", reviewId));
        }
    }

    @Override
    public List<Review> findReviews(Long filmId, Integer count) {

        var rowMapper = BeanPropertyRowMapper.newInstance(Review.class);

        if (filmId != null) {

            String sqlQuery = "SELECT * " +
                    "          FROM review " +
                    "          WHERE film_id = ? " +
                    "          ORDER BY useful DESC , review_id " +
                    "          limit ?";

            log.info("Получено {} отзывов для фильма с ID={}", count, filmId);

            return new LinkedList<>(jdbcTemplate.query(sqlQuery, rowMapper, filmId, count));

        } else {
            String sqlQuery = "SELECT * " +
                    "          FROM review " +
                    "          ORDER BY useful desc";

            log.info("Получены все отзывы");

            return new LinkedList<>(jdbcTemplate.query(sqlQuery, rowMapper));
        }
    }

    @Override
    public void addLikeToReview(Long reviewId, Long userId) {
        addFeedback(reviewId, userId, true);
        log.info("Пользователь {} поставил лайк отзыву {}", userId, reviewId);
    }

    @Override
    public void addDislikeToReview(Long reviewId, Long userId) {
        addFeedback(reviewId, userId, false);
        log.info("Пользователь {} поставил дизлайк отзыву {}", userId, reviewId);
    }

    @Override
    public void removeLikeFromReview(Long reviewId, Long userId) {
        removeFeedback(reviewId, userId, true);
        log.info("Пользователь {} удалил лайк у отзыва {}", userId, reviewId);
    }

    @Override
    public void removeDislikeFromReview(Long reviewId, Long userId) {
        removeFeedback(reviewId, userId, false);
        log.info("Пользователь {} удалил дизлайк у отзыва {}", userId, reviewId);
    }

    private void addFeedback(Long reviewId, Long userId, Boolean isUseful) {

        userDao.getUserById(userId);
        getReviewById(reviewId);

        String sqlQuery = "INSERT INTO feedback (review_id, user_id, is_useful) " +
                "          VALUES (?, ?, ?)";

        changeUseful(reviewId, (isUseful ? 1 : -1));

        log.info("Пользователь с ID={} оставил {} фитбек отзыву с ID={}", isNegative(isUseful ? 1 : -1), userId, reviewId);

        jdbcTemplate.update(sqlQuery, reviewId, userId, isUseful);
    }

    private void removeFeedback(Long reviewId, Long userId, Boolean isUseful) {

        getReviewById(reviewId);
        userDao.getUserById(userId);

        String sqlQuery = "DELETE FROM feedback " +
                "          WHERE user_id = ? AND review_id = ? AND is_useful = ?";

        changeUseful(reviewId, (isUseful ? -1 : 1));

        log.info("Пользователь с ID={} удалил фитбек отзыву с ID={}", userId, reviewId);

        jdbcTemplate.update(sqlQuery, userId, reviewId, isUseful);
    }

    private void changeUseful(Long reviewId, int delta) {

        Review review = getReviewById(reviewId);

        String sqlQuery = "UPDATE review " +
                "          SET useful = ?" +
                "          WHERE review_id = ?";

        jdbcTemplate.update(sqlQuery, review.getUseful() + delta, reviewId);

    }

    private String isNegative(int delta) {
        if (delta < 0) {
            return "положительный";
        } else {
            return "негативный";
        }
    }
}
