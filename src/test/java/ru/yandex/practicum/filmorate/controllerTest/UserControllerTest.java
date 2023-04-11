package ru.yandex.practicum.filmorate.controllerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
    private Map<Integer, User> users;
    private User user;
    private UserController userController;

    @BeforeEach
    public void beforeEach() {
        userController = new UserController();
        users = new HashMap<>();
        user = User.builder()
                .id(1)
                .email("gleb.verbickiy@yandex.ru")
                .login("glebchik_95")
                .name("Gleb")
                .birthday(LocalDate.of(1995, 12, 15))
                .build();
    }

    @Test
    public void shouldCreateUserWhenNameIsEmpty() {
        user.setName("");
        userController.createUser(user);
        assertEquals(user.getLogin(), user.getName());
        assertEquals(1, userController.getAllUsers().size());
    }

    @Test
    public void shouldNoCreateUserWhenLoginIsEmpty() {
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> {
                    user.setLogin(" glebchik 95");
                    userController.createUser(user);
                });
        assertEquals("Поле с логином некорректно заполнено", exception.getMessage());
        assertEquals(0, userController.getAllUsers().size(),
                "Количество пользователей в списке = 0");
    }


    @Test
    public void shouldAddOneUser() {
        users.put(user.getId(), user);
        assertEquals(1, users.size(), "Пользователь успешно добавлен");
    }

    @Test
    public void shouldNoCreateUserWhenBirthdayIsInFuture() {

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> {
                    user.setBirthday(LocalDate.of(3333, 3, 3));
                    userController.createUser(user);
                });
        assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }

    @Test
    public void shouldNoAddUserWhenLoginContainsSpaces() {
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> {
                    user.setLogin(" gleb chik");
                    userController.createUser(user);
                });
        assertEquals("Поле с логином некорректно заполнено", exception.getMessage());
        assertEquals(0, userController.getAllUsers().size(), "Количество пользователей в списке = 0");
    }

}
