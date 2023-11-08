package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmDao {

    List<Film> getAllFilms();

    Film getFilmById(Long id);

    List<Film> getFilmsByUserId(Long userId);//

    List<Film> getFilmsByFriendId(Long friendId);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Film addUserLike(Long id, Long userId);

    Film deleteUsersLike(Long id, Long userId);

    void deleteFilmById(Long id);

    List<Film> getPopularFilmsByCount(Integer count);

    List<Film> getPopularByYear(Integer releaseYear, Integer count);

    List<Film> getPopularByGenreAndYear(Long genreId, Integer releaseYear, Integer count);

    List<Film> getPopularByGenre(Long genreId, Integer count);

    List<Film> getRecommendations(Long id);

    List<Film> getByTitleContaining(String query);

    List<Film> getByDirectorContaining(String query);

    List<Film> getByTitleContainingOrDirectorContaining(String titleQuery, String directorQuery);

    List<Film> getDirectorFilmsSortedByYear(Long directorId);

    List<Film> getDirectorFilmsSortedByLikes(Long directorId);

}
