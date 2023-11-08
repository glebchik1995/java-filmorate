package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.impl.FilmServiceImpl;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FilmController {

    private final FilmServiceImpl filmService;

    @GetMapping("/films")
    public List<Film> getAll() {
        return filmService.getAllFilms();
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable Long id) {
        return filmService.getFilmById(id);
    }

    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping(value = "/films")
    public Film update(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping(value = "/films/{id}/like/{userId}")
    public Film addLike(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.addUserLike(id, userId);
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public Film deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.deleteUsersLike(id, userId);
    }

    @DeleteMapping(value = "/films/{id}")
    public void deleteFilm(@PathVariable Long id) {
        filmService.deleteFilmById(id);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopulars(@RequestParam(defaultValue = "10", required = false) Integer count,
                                  @RequestParam(defaultValue = "0", required = false) Long genreId,
                                  @RequestParam(defaultValue = "0", required = false) Integer year) {
        return filmService.getPopularsFilms(count, genreId, year);
    }

    @GetMapping(value = "/films/director/{directorId}")
    public List<Film> getDirectorSortedPopularFilms(@PathVariable Long directorId,
                                                    @RequestParam(required = false, defaultValue = "likes") String sortBy) {
        return filmService.getDirectorSortedPopularFilms(directorId, sortBy);
    }

    @GetMapping("/films/common")
    public List<Film> getCommonFilms(@RequestParam("userId") Long userId,
                                     @RequestParam("friendId") Long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/films/search")
    public List<Film> searchFilms(@RequestParam("query") String query, @RequestParam("by") String searchBy) {
        return filmService.filmSearch(query, searchBy);
    }

    @GetMapping(value = "/users/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable Long id) {
        return filmService.getRecommendations(id);
    }

}


