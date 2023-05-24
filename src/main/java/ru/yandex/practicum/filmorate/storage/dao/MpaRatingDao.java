package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.film.MpaRating;

import java.util.List;

public interface MpaRatingDao {

    List<MpaRating> getAllMpaRatings();

    MpaRating getRatingById(int id);
}
