package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private FilmService filmService;
    private FilmStorage filmStorage;
    @Autowired
    public FilmController(FilmService filmService, FilmStorage filmStorage) {
        this.filmService = filmService;
        this.filmStorage = filmStorage;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Фильм {} успешно добавлен ", film);
        return filmStorage.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Фильм {} успешно обновлен", film);
        return filmStorage.updateFilm(film);
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("На текущий момент " + LocalDate.now() +
                " количество фильмов в списке составляет: " + filmStorage.getAllFilms().size());
        return filmStorage.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        return filmStorage.getFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(name = "count", defaultValue = "10") Integer count) {
        return filmService.getPopular(count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.putLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}")
    public Film deleteFilm(@PathVariable Long id) {
        return filmStorage.deleteFilm(id);
    }

}


