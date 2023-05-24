package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.dao.FilmDao;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class FilmService {

    @Qualifier("FilmDaoImpl")
    private final FilmDao filmDao;

    public void addFilm(Film film) {
        validate(film);
        filmDao.addFilm(film);
    }
    public void updateFilm(Film film) {
        validate(film);
        filmDao.updateFilm(film);
    }

    public Film getFilmById(int filmId) {
        return filmDao.getFilmById(filmId);
    }

    public List<Film> getAllFilms() {
        return filmDao.getAllFilms();
    }

    public void deleteFilm(int filmId) {
        filmDao.deleteFilm(filmId);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmDao.getPopularFilms(count);
    }

    public void putLike(int filmId, int userId) {
        filmDao.putLike(filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        filmDao.deleteLike(filmId, userId);
    }

    public void validate(Film film) {

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");
        }
        if (film.getDescription().length() < 1 || film.getDescription().length() > 200) {
            throw new ValidationException("Количество символов должно быть больше 0 и не превышать 200");
        }
        if (film.getDuration() < 0) {
            throw new ValidationException("Продолжительность не может быть отрицательной");
        }
        if (film.getName().isEmpty() || film.getName().isBlank()) {
            throw new ValidationException("Поле с названием фильма должно быть заполнено");
        }
    }
}
