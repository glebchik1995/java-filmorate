package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class FilmService {
    private FilmStorage filmStorage;
    private UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> getPopular(Integer count) {
        if (count < 1) {
           throw new ValidationException("Количество фильмов для вывода не должно быть меньше 1");
        }

        return filmStorage
                .getAllFilms()
                .stream()
                .sorted(Comparator.comparing(Film::getId).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public void putLike(Long filmId, Long userId) {
        if (filmStorage.getFilmById(filmId) != null) {
            if (userStorage.getUserById(userId) != null) {
                filmStorage.getFilmById(filmId).getLikes().add(userId);
            } else {
                throw new UserNotFoundException("Пользователь c ID=" + userId + " не найден!");
            }
        } else {
            throw new FilmNotFoundException("Фильм c ID=" + filmId + " не найден!");
        }
    }

    public void deleteLike(Long filmId, Long userId) {
        if (filmStorage.getFilmById(filmId) != null) {
            if (userStorage.getUserById(userId) != null) {
                filmStorage.getFilmById(filmId).getLikes().remove(userId);
            } else {
                throw new UserNotFoundException("Пользователь c ID=" + userId + " не найден!");
            }
        } else {
            throw new FilmNotFoundException("Фильм c ID=" + filmId + " не найден!");
        }
    }
}
