package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        userService.createUser(user);
        log.info("Пользователь {} успешно добавлен", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        userService.updateUser(user);
        log.info("Пользователь {} успешно обновлен", user);
        return user;
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("На текущий момент " + LocalDate.now() +
                " количество пользователей в списке составляет: " + userService.getAllUsers().size());
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        log.info("Получен запрос на получение пользователя с ID={}.", id);
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        log.info("Получен запрос на получение списка пользователей, которые являются друзьями пользователя с ID={}.",
                id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Получен запрос на получение списка общих друзей пользователя с ID={} и пользователя с ID={}.",
                id, otherId);
        return userService.getMutualFriends(id, otherId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.addFriend(id, friendId);
        log.info("Пользователь с ID={} добавляет в список друзей пользователя с ID={}.", friendId, id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.deleteFriend(id, friendId);
        log.info("Пользователь с ID={} удаляет из списка друзей пользователя с ID={}.", friendId, id);
    }
}
