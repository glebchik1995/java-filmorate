package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmDao {

    Collection<Film> getAllFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilmById(long filmId);

    Collection<Film> getPopularFilms(long count);

    void deleteFilm(long filmId);

    void deleteFilmGenreById(long id);

    void addLike(long filmId, long userId);

    void deleteLike(long filmId, long userId);

}
