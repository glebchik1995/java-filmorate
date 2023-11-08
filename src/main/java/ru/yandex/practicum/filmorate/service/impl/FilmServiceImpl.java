package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmDao;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    private final FilmDao filmDao;

    @Override
    public List<Film> getAllFilms() {
        return filmDao.getAllFilms();
    }

    @Override
    public Film getFilmById(Long filmId) {
        return filmDao.getFilmById(filmId);
    }

    @Override
    public Film createFilm(Film film) {
        return filmDao.createFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        return filmDao.updateFilm(film);
    }

    @Override
    public Film addUserLike(Long id, Long userId) {
        return filmDao.addUserLike(id, userId);
    }

    @Override
    public Film deleteUsersLike(Long id, Long userId) {
        return filmDao.deleteUsersLike(id, userId);
    }

    @Override
    public void deleteFilmById(Long filmId) {
        filmDao.deleteFilmById(filmId);
    }

    @Override
    public List<Film> getPopularsFilms(Integer count, Long genreId, Integer year) {
        if (genreId == 0 && year == 0) {
            return filmDao.getPopularFilmsByCount(count);
        } else if (genreId != 0 && year != 0) {
            return filmDao.getPopularByGenreAndYear(genreId, year, count);
        } else if (genreId != 0) {
            return filmDao.getPopularByGenre(genreId, count);
        } else {
            return filmDao.getPopularByYear(year, count);
        }
    }

    @Override
    public List<Film> getDirectorSortedPopularFilms(Long directorId, String sortBy) {
        if ("year".equals(sortBy)) {
            return filmDao.getDirectorFilmsSortedByYear(directorId);
        } else {
            return filmDao.getDirectorFilmsSortedByLikes(directorId);
        }
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        List<Film> userFilms = filmDao.getFilmsByUserId(userId);
        List<Film> friendFilms = filmDao.getFilmsByFriendId(friendId);

        List<Film> commonFilms = new ArrayList<>();
        for (Film film : userFilms) {
            if (friendFilms.contains(film)) {
                commonFilms.add(film);
            }
        }
        return commonFilms;
    }

    @Override
    public List<Film> filmSearch(String query, String searchBy) {
        List<Film> films;

        if (searchBy.contains("title") && searchBy.contains("director")) {
            films = filmDao.getByTitleContainingOrDirectorContaining(query, query);
            log.info("Поиск по названию и режиссеру = {}", query);
        } else if (searchBy.equals("title")) {
            films = filmDao.getByTitleContaining(query);
            log.info("Поиск по названию = {}", query);
        } else if (searchBy.equals("director")) {
            films = filmDao.getByDirectorContaining(query);
            log.info("Поиск по режиссеру = {}", query);
        } else {
            films = Collections.emptyList();
            log.info("Поиск можно сделать только по режиссеру и названию");
        }
        return films;
    }

    @Override
    public List<Film> getRecommendations(Long id) {
        return filmDao.getRecommendations(id);
    }
}
