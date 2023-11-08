package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {

    List<Film> getAllFilms();

    Film getFilmById(Long id);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Film addUserLike(Long id, Long userId);

    Film deleteUsersLike(Long id, Long userId);

    void deleteFilmById(Long id);

    List<Film> getPopularsFilms(Integer count, Long genreId, Integer year);

    List<Film> getDirectorSortedPopularFilms(Long directorId, String sortBy);

    List<Film> getCommonFilms(Long userId, Long friendId);

    List<Film> filmSearch(String query, String searchBy);

    List<Film> getRecommendations(Long id);

}
