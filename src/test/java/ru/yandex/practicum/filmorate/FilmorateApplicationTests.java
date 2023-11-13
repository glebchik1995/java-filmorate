package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.repository.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = FilmoRateApplication.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmorateApplicationTests {

    private final UserDao userStorage;

    private final FilmDao filmStorage;

    private final GenreDao genreStorage;

    private final MpaRatingDao mpaStorage;

    private final ReviewDao reviewStorage;

    private final DirectorDao directorStorage;
    private User userOne;
    private User userTwo;
    private Film filmOne;
    private Film filmTwo;
    private Review reviewOne;
    private Review reviewTwo;
    private Director directorOne;
    private Director directorTwo;

    @BeforeEach
    void setUp() {

        userOne = User.builder()
                .id(0L)
                .login("loginOne")
                .name("nameOne")
                .email("email@email.ru")
                .birthday(LocalDate.of(1990, 12, 12))
                .build();

        userTwo = User.builder()
                .id(0L)
                .login("loginTwo")
                .name("nameTwo")
                .email("yandex@yandex.ru")
                .birthday(LocalDate.of(1995, 5, 5))
                .build();

        filmOne = Film.builder()
                .id(0L)
                .name("filmOne")
                .description("descriptionOne")
                .releaseDate(LocalDate.of(1949, 1, 1))
                .duration(100L)
                .mpa(new MpaRating(1L, "G"))
                .genres(new ArrayList<>())
                .build();

        filmTwo = Film.builder()
                .id(0L)
                .name("filmTwo")
                .description("descriptionTwo")
                .releaseDate(LocalDate.of(1977, 7, 7))
                .duration(200L)
                .mpa(new MpaRating(1L, "NC-17"))
                .genres(new ArrayList<>())
                .build();

        reviewOne = Review.builder()
                .content("Review_One_Content")
                .isPositive(true)
                .userId(1L)
                .filmId(1L)
                .useful(0)
                .reviewId(0L)
                .build();

        reviewTwo = Review.builder()
                .content("Review_Two_Content")
                .isPositive(true)
                .userId(1L)
                .filmId(1L)
                .useful(0)
                .reviewId(0L)
                .build();

        directorOne = Director.builder()
                .id(0L)
                .name("directorOne")
                .build();

        directorTwo = Director.builder()
                .id(0L)
                .name("directorTwo")
                .build();
    }

    @Test
    public void shouldGetReviewsTest() {
        userStorage.createUser(userOne);
        filmStorage.createFilm(filmOne);

        reviewStorage.createReview(reviewOne);
        reviewStorage.createReview(reviewTwo);

        assertEquals(reviewStorage.findReviews(1L, 2).size(), 2);
    }

    @Test
    public void shouldGetReviewsByFilmIdTest() {
        userStorage.createUser(userOne);
        filmStorage.createFilm(filmOne);
        filmStorage.createFilm(filmTwo);

        reviewStorage.createReview(reviewOne);
        reviewStorage.createReview(reviewTwo);

        assertEquals(reviewStorage.findReviews(1L, 1).size(), 1);
    }

    @Test
    public void shouldGetReviewByIdTest() {

        userStorage.createUser(userOne);
        filmStorage.createFilm(filmOne);
        reviewStorage.createReview(reviewOne);

        Optional<Review> reviewOptional = Optional.ofNullable(reviewStorage.getReviewById(1L));
        assertThat(reviewOptional)
                .isPresent()
                .hasValueSatisfying(review -> {
                    assertThat(review).hasFieldOrPropertyWithValue("reviewId", 1L);
                    assertThat(review).hasFieldOrPropertyWithValue("content", "Review_One_Content");
                    assertThat(review).hasFieldOrPropertyWithValue("isPositive", true);
                    assertThat(review).hasFieldOrPropertyWithValue("userId", 1L);
                    assertThat(review).hasFieldOrPropertyWithValue("filmId", 1L);
                });
    }

    @Test
    public void shouldUpdateReviewTest() {
        filmStorage.createFilm(filmTwo);
        filmStorage.createFilm(filmTwo);
        userStorage.createUser(userOne);
        reviewStorage.createReview(reviewOne);

        Review reviewOneUpdate = new Review(1L,
                "Review_One_Content_Update",
                false,
                0L,
                0L,
                1);

        Optional<Review> updatedReview = Optional.ofNullable(reviewStorage.updateReview(reviewOneUpdate));
        assertThat(updatedReview)
                .isPresent()
                .hasValueSatisfying(review -> {
                    assertThat(review).hasFieldOrPropertyWithValue("reviewId", 1L);
                    assertThat(review).hasFieldOrPropertyWithValue("content", "Review_One_Content_Update");
                    assertThat(review).hasFieldOrPropertyWithValue("isPositive", false);
                    assertThat(review).hasFieldOrPropertyWithValue("userId", 1L);
                    assertThat(review).hasFieldOrPropertyWithValue("filmId", 1L);
                });
    }

    @Test
    public void shouldGetGenresTest() {
        assertEquals(genreStorage.getAllGenres().size(), 6);
    }

    @Test
    public void shouldGetGenreByIdTest() {
        assertEquals(genreStorage.getGenreById(1L).getName(), "Комедия");
        assertEquals(genreStorage.getGenreById(2L).getName(), "Драма");
        assertEquals(genreStorage.getGenreById(3L).getName(), "Мультфильм");
        assertEquals(genreStorage.getGenreById(4L).getName(), "Триллер");
        assertEquals(genreStorage.getGenreById(5L).getName(), "Документальный");
        assertEquals(genreStorage.getGenreById(6L).getName(), "Боевик");
    }

    @Test
    void shouldGetMpaTest() {
        assertEquals(mpaStorage.getAllMpaRatings().size(), 5);
    }

    @Test
    void shouldGetMpaByIdTest() {
        assertEquals(mpaStorage.getMpaRatingById(1L).getName(), "G");
        assertEquals(mpaStorage.getMpaRatingById(2L).getName(), "PG");
        assertEquals(mpaStorage.getMpaRatingById(3L).getName(), "PG-13");
        assertEquals(mpaStorage.getMpaRatingById(4L).getName(), "R");
        assertEquals(mpaStorage.getMpaRatingById(5L).getName(), "NC-17");
    }

    @Test
    void shouldEmptyGetFilmsTest() {
        List<Film> films = filmStorage.getAllFilms();
        assertTrue(films.isEmpty());
    }

    @Test
    void shouldAddFilmTest() {
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.createFilm(filmOne));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(film).hasFieldOrPropertyWithValue("name", "filmOne");
                    assertThat(film).hasFieldOrPropertyWithValue("description", "descriptionOne");
                    assertThat(film).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1949, 1, 1));
                    assertThat(film).hasFieldOrPropertyWithValue("duration", 100L);
                });
    }

    @Test
    void shouldGetFilmsTest() {
        filmStorage.createFilm(filmOne);
        List<Film> films = filmStorage.getAllFilms();

        assertEquals(films.size(), 1);
        assertThat(films.get(0)).hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "filmOne")
                .hasFieldOrPropertyWithValue("description", "descriptionOne")
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1949, 1, 1))
                .hasFieldOrPropertyWithValue("duration", 100L);

        filmStorage.createFilm(filmTwo);
        films = filmStorage.getAllFilms();

        assertEquals(films.size(), 2);
        assertThat(films)
                .isNotEmpty()
                .satisfies(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", "filmOne");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("description", "descriptionOne");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1949, 1, 1));
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("duration", 100L);

                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("id", 2L);
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("name", "filmTwo");
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("description", "descriptionTwo");
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1977, 7, 7));
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("duration", 200L);
                });
    }

    @Test
    void shouldUpdateFilmTest() {
        filmStorage.createFilm(filmOne);
        filmTwo.setId(1L);
        filmStorage.updateFilm(filmTwo);
        List<Film> users = filmStorage.getAllFilms();

        assertEquals(users.size(), 1);
        assertThat(users)
                .isNotEmpty()
                .satisfies(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", "filmTwo");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("description", "descriptionTwo");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1977, 7, 7));
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("duration", 200L);
                });
    }

    @Test
    void shouldEmptyGetUsersTest() {
        List<User> users = userStorage.findAllUsers();
        assertTrue(users.isEmpty());
    }

    @Test
    void shouldAddUserTest() {
        Optional<User> userOptional = Optional.ofNullable(userStorage.createUser(userOne));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(user).hasFieldOrPropertyWithValue("name", "nameOne");
                    assertThat(user).hasFieldOrPropertyWithValue("email", "email@email.ru");
                    assertThat(user).hasFieldOrPropertyWithValue("login", "loginOne");
                    assertThat(user).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1990, 12, 12));
                });
    }

    @Test
    void shouldGetUsersTest() {
        userStorage.createUser(userOne);
        List<User> users = userStorage.findAllUsers();

        assertEquals(users.size(), 1);
        assertThat(users)
                .isNotEmpty()
                .satisfies(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", "nameOne");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("email", "email@email.ru");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("login", "loginOne");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1990, 12, 12));
                });

        userStorage.createUser(userTwo);
        users = userStorage.findAllUsers();

        assertEquals(users.size(), 2);
        assertThat(users)
                .isNotEmpty()
                .satisfies(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", "nameOne");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("email", "email@email.ru");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("login", "loginOne");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1990, 12, 12));

                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("id", 2L);
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("name", "nameTwo");
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("email", "yandex@yandex.ru");
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("login", "loginTwo");
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1995, 5, 5));
                });
    }

    @Test
    void shouldUpdateUserTest() {
        userStorage.createUser(userOne);
        userTwo.setId(1L);
        userStorage.updateUser(userTwo);
        List<User> users = userStorage.findAllUsers();

        assertEquals(users.size(), 1);
        assertThat(users)
                .isNotEmpty()
                .satisfies(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", "nameTwo");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("email", "yandex@yandex.ru");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("login", "loginTwo");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1995, 5, 5));
                });
    }

    @Test
    void shouldSaveOneFriendTest() {
        userStorage.createUser(userOne);
        userStorage.createUser(userTwo);

        userStorage.addFriend(1L, 2L);

        List<User> friendsUserOne = userStorage.getAllUserFriends(1L);

        assertEquals(friendsUserOne.size(), 1);
        assertThat(friendsUserOne)
                .isNotEmpty()
                .satisfies(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 2L);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", "nameTwo");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("email", "yandex@yandex.ru");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("login", "loginTwo");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1995, 5, 5));
                });

        List<User> friendsUserTwo = userStorage.getAllUserFriends(2L);

        assertTrue(friendsUserTwo.isEmpty());
    }

    @Test
    void shouldDeleteOneFriendTest() {
        userStorage.createUser(userOne);
        userStorage.createUser(userTwo);
        userStorage.addFriend(1L, 2L);
        userStorage.unfriend(1L, 2L);

        List<User> friendsUserOne = userStorage.getAllUserFriends(1L);

        assertTrue(friendsUserOne.isEmpty());
    }

    @Test
    void shouldFindRecommendationsFilmsTest() {
        userStorage.createUser(userOne);
        userStorage.createUser(userTwo);
        filmStorage.createFilm(filmOne);
        filmStorage.createFilm(filmTwo);
        filmOne.setName("Some new film");
        filmStorage.createFilm(filmOne);

        List<Film> emptyListFilms = filmStorage.getRecommendations(1L);

        assertTrue(emptyListFilms.isEmpty(), "Список должен быть пуст");

        filmStorage.addUserLike(1L, 1L);
        filmStorage.addUserLike(2L, 1L);
        filmStorage.addUserLike(1L, 2L);
        filmStorage.addUserLike(3L, 2L);

        List<Film> emptyAnotherListFilms = filmStorage.getRecommendations(10L);

        assertTrue(emptyAnotherListFilms.isEmpty(), "Список должен быть пуст");

        filmStorage.addUserLike(1L, 1L);
        filmStorage.addUserLike(2L, 1L);
        filmStorage.addUserLike(1L, 2L);
        filmStorage.addUserLike(3L, 2L);

        List<Film> oneFilmRecommended = filmStorage.getRecommendations(1L);

        assertThat(oneFilmRecommended).hasSize(1);
        assertThat(oneFilmRecommended.get(0))
                .hasFieldOrPropertyWithValue("id", 3L)
                .hasFieldOrPropertyWithValue("name", "Some new film")
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1949, 1, 1))
                .hasFieldOrPropertyWithValue("duration", 100L)
                .hasFieldOrPropertyWithValue("mpa", new MpaRating(1L, "G"));

        filmStorage.addUserLike(3L, 1L);

        List<Film> emptyListFilmsAfterLike = filmStorage.getRecommendations(1L);

        assertTrue(emptyListFilmsAfterLike.isEmpty());
    }

    @Test
    void shouldDeleteFilmByIdTest() {
        filmStorage.createFilm(filmOne);
        List<Film> films = filmStorage.getAllFilms();

        assertEquals(films.size(), 1);

        filmStorage.deleteFilmById(1L);

        films = filmStorage.getAllFilms();

        assertTrue(films.isEmpty());
    }

    @Test
    void shouldDeleteUserByIdTest() {
        userStorage.createUser(userOne);
        List<User> users = userStorage.findAllUsers();

        assertEquals(users.size(), 1);

        userStorage.deleteUserById(1L);

        users = userStorage.findAllUsers();

        assertTrue(users.isEmpty());
    }

    @Test
    void shouldCreateDirectorTest() {
        Optional<Director> directorOptional = directorStorage.createDirector(directorOne);

        assertThat(directorOptional)
                .isPresent()
                .hasValueSatisfying(director -> {
                    assertThat(director).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(director).hasFieldOrPropertyWithValue("name", "directorOne");
                });
    }

    @Test
    void shouldGetEmptyDirectorsTest() {
        List<Director> directors = directorStorage.getAllDirectors();
        assertTrue(directors.isEmpty());
    }

    @Test
    void shouldUpdateDirectorTest() {
        directorStorage.createDirector(directorOne);
        directorTwo.setId(1L);
        directorStorage.updateDirector(directorTwo);
        List<Director> directors = directorStorage.getAllDirectors();

        assertEquals(1, directors.size());
        assertThat(directors.get(0))
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "directorTwo");
    }

    @Test
    void shouldGetDirectorsTest() {
        directorStorage.createDirector(directorOne);
        assertEquals(1, directorStorage.getAllDirectors().size());
        directorStorage.createDirector(directorTwo);
        assertEquals(2, directorStorage.getAllDirectors().size());
    }

    @Test
    void shouldGetDirectorTest() {
        directorStorage.createDirector(directorOne);
        directorOne.setId(1L);
        assertTrue(directorStorage.getDirectorById(1L).isPresent());
    }

    @Test
    void shouldRemoveDirectorTest() {
        Optional<Director> director = directorStorage.createDirector(directorOne);
        assertTrue(director.isPresent());
        directorStorage.deleteDirectorById(director.get().getId());
        assertEquals(0, directorStorage.getAllDirectors().size());
    }

}