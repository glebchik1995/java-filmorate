package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;

public interface MpaRatingService {

    MpaRating getRatingById(Long ratingId);

    Collection<MpaRating> getAllMpaRatings();
}
