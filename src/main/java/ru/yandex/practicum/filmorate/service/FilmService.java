package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.FilmDao;

import java.util.Collection;
import java.util.List;

@Service
@AllArgsConstructor
public class FilmService implements FilmDao {

    private final FilmDao filmDao;

    public Film addFilm(Film film) {
        return filmDao.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmDao.updateFilm(film);
    }

    public Film getFilmById(long filmId) {
        return filmDao.getFilmById(filmId);
    }

    @Override
    public List<Film> getPopularFilms(long count) {
        return null;
    }

    public Collection<Film> getAllFilms() {
        return filmDao.getAllFilms();
    }

    public void deleteFilm(long filmId) {
        filmDao.deleteFilm(filmId);
    }

    @Override
    public void deleteFilmGenreById(long id) {
        filmDao.deleteFilmGenreById(id);
    }

    @Override
    public void addLike(long filmId, long userId) {
        filmDao.addLike(filmId, userId);
    }

    public Collection<Film> getPopularFilms(Integer count) {
        return filmDao.getPopularFilms(count);
    }

    public void putLike(long filmId, long userId) {
        filmDao.addLike(filmId, userId);
    }

    public void deleteLike(long filmId, long userId) {
        filmDao.deleteLike(filmId, userId);
    }


}
