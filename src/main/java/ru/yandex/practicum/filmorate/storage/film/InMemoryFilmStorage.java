package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private Long idGenerator = 0L;

    private Long idPlus() {
        return ++idGenerator;
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film createFilm(Film film) {
        if (films.containsKey(film.getId())) {
            throw new DataAlreadyExistException("Такой фильм уже существует");
        }
        film.setId(idPlus());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new DataNotFoundException("Фильм не найден");
        }
        films.put(film.getId(), film);
    }

    @Override
    public Film getFilmById(Long filmId) {
        if (!films.containsKey(filmId)) {
            throw new DataNotFoundException("Фильм с ID= " + filmId + " не найден!");
        }
        return films.get(filmId);
    }

    @Override
    public Film deleteFilm(Long filmId) {
        if (!films.containsKey(filmId)) {
            throw new DataNotFoundException("Фильм с ID= " + filmId + " не найден!");
        }
        return films.remove(filmId);
    }

}
