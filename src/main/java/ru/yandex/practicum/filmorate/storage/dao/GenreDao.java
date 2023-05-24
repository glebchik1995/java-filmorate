package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.film.Genre;

import java.util.List;

public interface GenreDao {

    Genre getGenreById(int genreId);

    List<Genre> getAllGenres();
}
