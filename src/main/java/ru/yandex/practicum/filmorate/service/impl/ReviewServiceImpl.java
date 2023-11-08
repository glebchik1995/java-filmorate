package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.FilmDao;
import ru.yandex.practicum.filmorate.repository.ReviewDao;
import ru.yandex.practicum.filmorate.repository.UserDao;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewDao reviewDao;

    private final UserDao userDao;

    private final FilmDao filmDao;

    public Review createReview(Review review) {
        userDao.getUserById(review.getUserId());
        filmDao.getFilmById(review.getFilmId());
        return reviewDao.createReview(review);
    }

    public Review updateReview(Review review) {
        return reviewDao.updateReview(review);
    }

    public void removeReview(Long reviewId) {
        reviewDao.removeReview(reviewId);
    }

    public Review getReviewById(Long reviewId) {
        return reviewDao.getReviewById(reviewId);
    }

    public List<Review> findReviews(Long filmId, Integer count) {
        return reviewDao.findReviews(filmId, count);
    }

    public void addLikeToReview(Long reviewId, Long userId) {
        reviewDao.addLikeToReview(reviewId, userId);
    }

    public void addDislikeToReview(Long reviewId, Long userId) {
        reviewDao.addDislikeToReview(reviewId, userId);
    }

    public void removeLikeFromReview(Long reviewId, Long userId) {
        reviewDao.removeLikeFromReview(reviewId, userId);
    }

    public void removeDislikeFromReview(Long reviewId, Long userId) {
        reviewDao.removeDislikeFromReview(reviewId, userId);
    }
}
