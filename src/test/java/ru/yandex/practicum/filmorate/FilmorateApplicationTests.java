package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.MpaRating;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.impl.FilmDaoImpl;
import ru.yandex.practicum.filmorate.storage.impl.UserDaoImpl;


import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    private final JdbcTemplate jdbcTemplate;
    private final UserDaoImpl userDaoImpl;
    private final FilmDaoImpl filmdaoImpl;

    private User user;

    private Film film;

    @BeforeEach
    public void beforeEach() {
        user = User.builder()
                .email("Shaburov@yandex.ru")
                .login("Reviewer")
                .name("Semen Shaburov")
                .birthday(LocalDate.of(1990, 6, 7))
                .build();

        film = Film.builder()
                .name("new film")
                .description("description new film")
                .releaseDate(LocalDate.of(1995, 10, 5))
                .duration(111)
                .mpa(new MpaRating(1, "NC-17", "Лицам до 18 лет просмотр запрещён"))
                .genres(new HashSet<>())
                .likes(new LinkedHashSet<>())
                .build();
    }

    @AfterEach
    void afterEach() {

        String sqlQuery =
                        "delete from films;\n" +
                        "delete from users;\n" +
                        "delete from likes;\n" +
                        "delete from genres;\n" +
                        "delete from film_genres;";
        jdbcTemplate.update(sqlQuery);
        userDaoImpl.getAllUsers().clear();
        filmdaoImpl.getAllFilms().clear();
    }

    private User createNewUpdatedUser() {
        user = User.builder()
                .name("user name updated")
                .login("login updated user")
                .email("updated_user_email")
                .birthday(LocalDate.of(2000, 12, 20))
                .build();
        return user;
    }

    private Film createNewUpdatedFilm() {
        film = Film.builder()
                .name("update film")
                .description("description update film")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(99)
                .mpa(new MpaRating(1, "G", "У фильма нет возрастных ограничений"))
                .genres(new HashSet<>())
                .likes(new LinkedHashSet<>())
                .build();
        return film;
    }

    @Test
    public void shouldGetUserById() throws ValidationException, DataNotFoundException {
        user = userDaoImpl.addUser(user);
        Optional<User> userOptional = Optional.ofNullable(userDaoImpl.getUserById(user.getId()));
        assertThat(userOptional)
                .hasValueSatisfying(user1 -> assertThat(user1)
                        .hasFieldOrPropertyWithValue("id", user.getId())
                        .hasFieldOrPropertyWithValue("name", "Semen Shaburov"));
    }

    @Test
    public void shouldUpdateUser() throws ValidationException, DataNotFoundException {
        user = userDaoImpl.addUser(user);
        userDaoImpl.addUser(user);
        User updateUser = createNewUpdatedUser();
        Optional<User> testUpdateUser = Optional.ofNullable(userDaoImpl.updateUser(updateUser));
        assertThat(testUpdateUser)
                .hasValueSatisfying(user1 -> assertThat(user1)
                        .hasFieldOrPropertyWithValue("id", user.getId())
                        .hasFieldOrPropertyWithValue("name", "user name updated")
                );
    }


    @Test
    public void shouldGetAllUsers() throws ValidationException, DataNotFoundException {
        user = userDaoImpl.addUser(user);
        userDaoImpl.addUser(user);
        List<User> list = userDaoImpl.getAllUsers();
        assertThat(list).contains(user);
    }

    @Test
    public void shouldDeleteUser() {
        user = userDaoImpl.addUser(user);
        userDaoImpl.deleteUserById(user.getId());
        List<User> listUsers = userDaoImpl.getAllUsers();
        assertThat(listUsers).hasSize(0);
    }

    @Test
    void shouldCreateFilm() throws ValidationException, DataNotFoundException {
        film = filmdaoImpl.addFilm(film);
        filmdaoImpl.addFilm(film);
        List<Film> listFilms = filmdaoImpl.getAllFilms();
        assertThat(listFilms).contains(film);

    }

    @Test
    void shouldGetFilmById() throws ValidationException, DataNotFoundException {
        film = filmdaoImpl.addFilm(film);
        Optional<Film> filmOptional = Optional.ofNullable(filmdaoImpl.getFilmById(film.getId()));
        assertThat(filmOptional)
                .hasValueSatisfying(film -> assertThat(film)
                        .hasFieldOrPropertyWithValue("id", film.getId())
                        .hasFieldOrPropertyWithValue("name", "new film")
                );
    }

    @Test
    void shouldDeleteFilm() throws ValidationException, DataNotFoundException {
        film = filmdaoImpl.addFilm(film);
        filmdaoImpl.deleteFilm(film.getId());
        List<Film> listFilms = filmdaoImpl.getAllFilms();
        assertThat(listFilms).hasSize(1);

    }

    @Test
    public void shouldGetAllFilms() throws ValidationException, DataNotFoundException {
        film = filmdaoImpl.addFilm(film);
        List<Film> listFilms = filmdaoImpl.getAllFilms();
        assertThat(listFilms).contains(film);
    }

    @Test
    public void shouldUpdateFilm() throws ValidationException, DataNotFoundException {
        film = filmdaoImpl.addFilm(film);
        Film updateFilm = createNewUpdatedFilm();
        Optional<Film> testUpdateFilm = Optional.ofNullable(filmdaoImpl.updateFilm(updateFilm));
        assertThat(testUpdateFilm)
                .hasValueSatisfying(film ->
                        assertThat(film)
                                .hasFieldOrPropertyWithValue("name", "update film")
                                .hasFieldOrPropertyWithValue("description", "description update film")
                );
    }

    @Test
    public void shouldPutLike() throws ValidationException, DataNotFoundException {
        film = filmdaoImpl.addFilm(film);
        user = userDaoImpl.addUser(user);
        filmdaoImpl.putLike(film.getId(), user.getId());
        film = filmdaoImpl.getFilmById(film.getId());
        assertThat(film.getLikes()).hasSize(1);
        assertThat(film.getLikes()).contains(user.getId());
    }


    @Test
    public void shouldDeleteLike() throws ValidationException, DataNotFoundException {
        user = userDaoImpl.addUser(user);
        film = filmdaoImpl.addFilm(film);
        filmdaoImpl.putLike(film.getId(), user.getId());
        filmdaoImpl.deleteLike(film.getId(), user.getId());
        film = filmdaoImpl.getFilmById(film.getId());
        assertThat(film.getLikes()).hasSize(1);

    }

}
