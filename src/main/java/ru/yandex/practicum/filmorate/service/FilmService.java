package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public void createFilm(Film film) {
        validate(film);
        filmStorage.createFilm(film);
    }

    public void updateFilm(Film film) {
        validate(film);
        filmStorage.updateFilm(film);
    }


    public Film getFilmById(Long filmId) {
        return filmStorage.getFilmById(filmId);
    }


    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void deleteFilm(Long filmId) {
        filmStorage.deleteFilm(filmId);
    }

    public List<Film> getPopular(Integer count) {
        if (count < 1) {
            throw new IllegalArgumentException("Количество фильмов для вывода не должно быть меньше 1");
        }

        return filmStorage
                .getAllFilms()
                .stream()
                .sorted(Comparator.comparing(Film::getId).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public void putLike(Long filmId, Long userId) {
        userStorage.getUserById(userId);
        Film film = filmStorage.getFilmById(filmId);
        film.getLikes().add(userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        final Film film = filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);
        filmStorage.getFilmById(filmId);
        film.getLikes().remove(userId);
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
