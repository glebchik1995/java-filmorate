package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.MpaRating;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.impl.FilmDaoImpl;
import ru.yandex.practicum.filmorate.storage.impl.GenreDaoImpl;
import ru.yandex.practicum.filmorate.storage.impl.UserDaoImpl;


import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

    private final UserDaoImpl userDaoImpl;
    private final FilmDaoImpl filmdaoImpl;
    private final GenreDaoImpl genreDaoImpl;
    private final FilmService filmService;


    private User user;

    private Film film;

    @BeforeEach
    public void beforeEach() {
        user = User.builder()
                .id(1)
                .email("Shaburov@yandex.ru")
                .login("Reviewer")
                .name("Semen Shaburov")
                .birthday(LocalDate.of(1990, 6, 7))
                .build();

        film = Film.builder()
                .id(1)
                .name("American Pie")
                .description("In the center of the plot of the film are four graduates of the school who " +
                        "have concluded a mutual assistance agreement with each other in case of loss of " +
                        "virginity and following adventures")
                .releaseDate(LocalDate.of(1961, 10, 5))
                .duration(95)
                .mpa(new MpaRating(1, "NC-17", "Лицам до 18 лет просмотр запрещён"))
                .likes(new HashSet<>())
                .build();
        film.setGenres(new LinkedHashSet<>(List.of(new Genre(1, "Комедия"))));
    }


    @Test
    public void shouldCreateUserAndGetUserById() {
        userDaoImpl.addUser(user);
        Optional<User> userOptional = Optional.ofNullable(userDaoImpl.getUserById(1));
        assertThat(userOptional)
                .hasValueSatisfying(user ->
                        assertThat(user)
                                .hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("name", "Semen Shaburov"));
    }

    @Test
    public void shouldGetUsers() {
        user = userDaoImpl.addUser(user);
        List<User> list = userDaoImpl.getAllUsers();
        assertThat(list).contains(user);

    }

    @Test
    public void shouldUpdateUser() {
        userDaoImpl.addUser(user);
        User updateUser = User.builder()
                .id(user.getId())
                .name("newUser")
                .login("newReviewer")
                .email("newReviewer@yandex.ru")
                .birthday(LocalDate.of(1980, 12, 23))
                .build();
        Optional<User> testUpdateUser = Optional.ofNullable(userDaoImpl.updateUser(updateUser));
        assertThat(testUpdateUser)
                .hasValueSatisfying(user -> assertThat(user)
                        .hasFieldOrPropertyWithValue("name", "newUser")
                );
    }

    @Test
    public void shouldDeleteUser() {
        user = userDaoImpl.addUser(user);
        userDaoImpl.deleteUserById(user.getId());
        List<User> listUsers = userDaoImpl.getAllUsers();
        assertThat(listUsers).hasSize(0);
    }

    @Test
    public void shouldFilmAndGetFilmById() {
        Optional<User> userOptional = Optional.ofNullable(userDaoImpl.getUserById(1));
        assertThat(userOptional).isPresent().hasValueSatisfying(user ->
                assertThat(user).hasFieldOrPropertyWithValue("id", 1));

    }

    @Test
    public void shouldGetAllFilms() {
        filmdaoImpl.addFilm(film);
        List<Film> listFilms = filmdaoImpl.getAllFilms();
        assertThat(listFilms).contains(film);
        Assertions.assertEquals(listFilms.size(), 1);
    }

    @Test
    public void shouldUpdateFilm() {
        filmdaoImpl.addFilm(film);
        Film updateFilm = Film.builder()
                .id(film.getId())
                .name("Avengers")
                .description("Final")
                .releaseDate(LocalDate.of(1975, 11, 19))
                .duration(133)
                .genres(new LinkedHashSet<>())
                .likes(new LinkedHashSet<>())
                .build();
        updateFilm.setMpa(new MpaRating(1, "G", ""));
        Optional<Film> testUpdateFilm = Optional.ofNullable(filmdaoImpl.updateFilm(updateFilm));
        assertThat(testUpdateFilm)
                .hasValueSatisfying(film ->
                        assertThat(film)
                                .hasFieldOrPropertyWithValue("name", "Avengers")
                                .hasFieldOrPropertyWithValue("description", "Final")
                );
    }

    @Test
    public void shouldDeleteFilm() {
        film = filmdaoImpl.addFilm(film);
        filmdaoImpl.deleteFilm(film.getId());
        List<Film> listFilms = filmdaoImpl.getAllFilms();
        assertThat(listFilms).hasSize(0);
        assertThat(Optional.of(listFilms.get(0)))
                .hasValueSatisfying(film -> AssertionsForClassTypes.assertThat(film)
                        .hasFieldOrPropertyWithValue("name", "American Pie"));
    }

    @Test
    public void shouldPutLike() {
        user = userDaoImpl.addUser(user);
        film = filmdaoImpl.addFilm(film);
        filmService.putLike(film.getId(), user.getId());
        film = filmdaoImpl.getFilmById(film.getId());
        assertThat(film.getLikes()).hasSize(1);
        assertThat(film.getLikes()).contains(user.getId());
    }

    @Test
    public void shouldDeleteLike() {
        user = userDaoImpl.addUser(user);
        film = filmdaoImpl.addFilm(film);
        filmService.putLike(film.getId(), user.getId());
        filmService.deleteLike(film.getId(), user.getId());
        film = filmdaoImpl.getFilmById(film.getId());
        assertThat(film.getLikes()).hasSize(0);


    }

    @Test
    public void shouldGetGenres() {
        Assertions.assertTrue(genreDaoImpl.getAllGenres().contains(new Genre(1, "Комедия")));
    }

    @Test
    public void shouldGetGenreById() {

        Assertions.assertEquals(genreDaoImpl.getGenreById(1), new Genre(1, "Комедия"));
    }
}
