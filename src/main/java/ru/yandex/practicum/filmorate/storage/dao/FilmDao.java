package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.List;

public interface FilmDao {

    List<Film> getAllFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilmById(int filmId);

    List<Film> getPopularFilms(int count);

    void deleteFilm(int filmId);

    void putLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);

}
