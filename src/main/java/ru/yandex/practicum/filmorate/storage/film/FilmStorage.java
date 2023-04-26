package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> getAllFilms();

    Film createFilm(Film film);

    void updateFilm(Film film);

    Film getFilmById(Long filmId);

    Film deleteFilm(Long filmId);
}
