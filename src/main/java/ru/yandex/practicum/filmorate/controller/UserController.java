package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    protected final Map<Integer, User> users = new HashMap<>();
    private int idGenerator = 0;

    private int idPlus() {
        return idGenerator++;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        validate(user);
        user.setId(idPlus());
        users.put(user.getId(), user);
        log.info("Пользователь {} успешно добавлен", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        validate(user);
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Пользователь не найден");
        }
        users.put(user.getId(), user);
        log.info("Пользователь {} успешно обновлен", user);
        return user;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("На текущий момент " + LocalDate.now() +
                " количество пользователей списке составляет: " + users.size());
        return users.values();
    }

    public void validate(User user) {
        if (user.getLogin().contains(" ") || user.getLogin() == null) {
            throw new ValidationException("Поле с логином некорректно заполнено");

        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
