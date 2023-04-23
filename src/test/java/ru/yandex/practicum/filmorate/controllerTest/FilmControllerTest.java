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
                .name("������������ �����")
                .description("�������")
                .duration(95)
                .releaseDate(LocalDate.of(1999, 1, 10))
                .build();
    }

    @Test
    public void shouldAddOneFilm() {
        films.put(film.getId(), film);
        assertEquals(1, films.size(), "� ������ �������� �����");
    }

    @Test
    public void shouldNoCreateFilmWhenNameIsEmpty() {
        final ValidationException exception = assertThrows(ValidationException.class,
                () -> {
                    film.setName("");
                    filmController.createFilm(film);
                });
        assertEquals("���� � ��������� ������ ������ ���� ���������", exception.getMessage());
        assertEquals(0, filmController.getAllFilms().size(),"������ ������� ����");
    }

    @Test
    public void shouldThrowExceptionIfDescriptionLengthEqualZero() {

        final ValidationException exception = assertThrows(ValidationException.class,
                () -> {
                    film.setDescription("");
                    filmController.createFilm(film);
                });
        assertEquals("���������� �������� ������ ���� ������ 0 � �� ��������� 200", exception.getMessage());
        assertEquals(0, filmController.getAllFilms().size(),"������ ������� ����");

    }

    @Test
    public void shouldNoCreateUserWhenBirthdayIsInFuture() {

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> {
                    film.setReleaseDate(LocalDate.of(1800, 3, 3));
                    filmController.createFilm(film);
                });
        assertEquals("���� ������ �� ����� ���� ������ 28.12.1895", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionIfDescriptionLengthIsMore200() {

        final ValidationException exception = assertThrows(ValidationException.class,
                () -> {
                    film.setDescription("�".repeat(202));
                    filmController.createFilm(film);
                });
        assertEquals("���������� �������� ������ ���� ������ 0 � �� ��������� 200", exception.getMessage());
        assertEquals(0, filmController.getAllFilms().size(),"������ ������� ����");

    }

    @Test
    public void shouldNoAddFilmWhenFilmDurationIsNegative() {

        final ValidationException exception = assertThrows(ValidationException.class,
                () -> {
                    film.setDuration(-1);
                    filmController.createFilm(film);
                });
        assertEquals("����������������� �� ����� ���� �������������", exception.getMessage());
        assertEquals(0, filmController.getAllFilms().size(),"������ ������� ����");

    }

}

