package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        filmService.createFilm(film);
        log.info("Фильм {} успешно добавлен ", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        filmService.updateFilm(film);
        log.info("Фильм {} успешно обновлен", film);
        return film;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("На текущий момент " + LocalDate.now() +
                " количество фильмов в списке составляет: " + filmService.getAllFilms().size());
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        log.info("Получен запрос на получение фильма с ID={}.", id);
        return filmService.getFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") Integer count) {
        log.info("Получен запрос на получение списка из {} самых популярных фильмов.",count);
        return filmService.getPopular(count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.putLike(id, userId);
        log.info("Пользователь с ID={} ставит ♥ фильму с ID={}.", userId, id);

    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.deleteLike(id, userId);
        log.info("Пользователь с ID={} убрал ♥ у фильма с ID={}.", userId, id);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable Long id) {
        filmService.deleteFilm(id);
        log.info("Фильм с ID={} успешно удален.", id);
    }

}


