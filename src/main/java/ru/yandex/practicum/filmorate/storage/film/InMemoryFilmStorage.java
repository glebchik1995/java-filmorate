package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    protected final Map<Long, Film> films = new HashMap<>();
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
        validate(film);
        film.setId(idPlus());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        validate(film);
        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException("����� �� ������");
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film getFilmById(Long filmId) {
        if (!films.containsKey(filmId)) {
            throw new UserNotFoundException("����� � ID= " + filmId + " �� ������!");
        }
        return films.get(filmId);
    }

    @Override
    public Film deleteFilm(Long filmId) {
        if (!films.containsKey(filmId)) {
            throw new UserNotFoundException("����� � ID= " + filmId + " �� ������!");
        }
        return films.remove(filmId);
    }

    public void validate(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("���� ������ �� ����� ���� ������ 28.12.1895");
        }
        if (film.getDescription().length() < 1 || film.getDescription().length() > 200) {
            throw new ValidationException("���������� �������� ������ ���� ������ 0 � �� ��������� 200");
        }
        if (film.getDuration() < 0) {
            throw new ValidationException("����������������� �� ����� ���� �������������");
        }
        if (film.getName().isEmpty() || film.getName().isBlank()) {
            throw new ValidationException("���� � ��������� ������ ������ ���� ���������");
        }
    }
}