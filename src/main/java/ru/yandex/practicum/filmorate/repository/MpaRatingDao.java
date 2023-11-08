package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

public interface MpaRatingDao {

    List<MpaRating> getAllMpaRatings();

    MpaRating getMpaRatingById(Long id);
}
