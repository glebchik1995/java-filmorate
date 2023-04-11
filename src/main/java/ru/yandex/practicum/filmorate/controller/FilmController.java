package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    protected final Map<Long, Film> films = new HashMap<>();

    private int idGenerator = 0;

    private int idPlus() {
        return ++idGenerator;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        validate(film);
        film.setId(idPlus());
        films.put(film.getId(), film);
        log.info("Фильм {} успешно добавлен ", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        validate(film);
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм не найден");
        }
        films.put(film.getId(), film);
        log.info("Фильм {} успешно обновлен", film);
        return film;
    }

    @GetMapping
    public Collection<Film> getAllFilm() {
        log.info("На текущий момент " + LocalDate.now() +
                " количество фильмов в списке составляет: " + films.size());
        return films.values();
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


