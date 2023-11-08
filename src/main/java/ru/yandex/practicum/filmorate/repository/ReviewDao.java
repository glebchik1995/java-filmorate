package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewDao {

    Review createReview(Review review);

    Review updateReview(Review review);

    void removeReview(Long reviewId);

    Review getReviewById(Long reviewId);

    List<Review> findReviews(Long filmId, Integer count);

    void addLikeToReview(Long reviewId, Long userId);

    void addDislikeToReview(Long reviewId, Long userId);

    void removeLikeFromReview(Long reviewId, Long userId);

    void removeDislikeFromReview(Long reviewId, Long userId);
}
