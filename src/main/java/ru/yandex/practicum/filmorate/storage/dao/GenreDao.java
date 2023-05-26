package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

public interface GenreDao {

    Genre getGenreById(long genreId);

    Collection<Genre> getAllGenres();
}
