package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;

public interface MpaRatingDao {

    Collection<MpaRating> getAllMpaRatings();

    MpaRating getMpaRatingById(long id);
}
