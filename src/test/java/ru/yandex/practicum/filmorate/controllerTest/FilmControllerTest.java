package ru.yandex.practicum.filmorate.controllerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {
    private Map<Long, Film> films;
    Film film;
    FilmController filmController;

    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController();
        films = new HashMap<>();
        film = Film.builder()
                .name("Американский пирог")
                .description("Комедия")
                .duration(95)
                .releaseDate(LocalDate.of(1999, 1, 10))
                .build();
    }

    @Test
    public void shouldAddOneFilm() {
        films.put(film.getId(), film);
        assertEquals(1, films.size(), "В список добавлен фильм");
    }

    @Test
    public void shouldNoCreateFilmWhenNameIsEmpty() {
        final ValidationException exception = assertThrows(ValidationException.class,
                () -> {
                    film.setName("");
                    filmController.createFilm(film);
                });
        assertEquals("Поле с названием фильма должно быть заполнено", exception.getMessage());
        assertEquals(0, filmController.getAllFilm().size(),"Список фильмов пуст");
    }

    @Test
    public void shouldThrowExceptionIfDescriptionLengthEqualZero() {

        final ValidationException exception = assertThrows(ValidationException.class,
                () -> {
                    film.setDescription("");
                    filmController.createFilm(film);
                });
        assertEquals("Количество символов должно быть больше 0 и не превышать 200", exception.getMessage());
        assertEquals(0, filmController.getAllFilm().size(),"Список фильмов пуст");

    }

    @Test
    public void shouldNoCreateUserWhenBirthdayIsInFuture() {

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> {
                    film.setReleaseDate(LocalDate.of(1800, 3, 3));
                    filmController.createFilm(film);
                });
        assertEquals("Дата релиза не может быть раньше 28.12.1895", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionIfDescriptionLengthIsMore200() {

        final ValidationException exception = assertThrows(ValidationException.class,
                () -> {
                    film.setDescription("ю".repeat(202));
                    filmController.createFilm(film);
                });
        assertEquals("Количество символов должно быть больше 0 и не превышать 200", exception.getMessage());
        assertEquals(0, filmController.getAllFilm().size(),"Список фильмов пуст");

    }

    @Test
    public void shouldNoAddFilmWhenFilmDurationIsNegative() {

        final ValidationException exception = assertThrows(ValidationException.class,
                () -> {
                    film.setDuration(-1);
                    filmController.createFilm(film);
                });
        assertEquals("Продолжительность не может быть отрицательной", exception.getMessage());
        assertEquals(0, filmController.getAllFilm().size(),"Список фильмов пуст");

    }

}

