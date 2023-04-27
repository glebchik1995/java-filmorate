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
    public User getUserById(@PathVariable Long userId) {
        log.info("Получен запрос на получение пользователя с ID={}.", userId);
        return userService.getUserById(userId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long userId) {
        log.info("Получен запрос на получение списка пользователей, которые являются друзьями пользователя с ID={}.",
                userId);
        return userService.getFriends(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable Long firstUserId, @PathVariable Long secondUserId) {
        log.info("Получен запрос на получение списка общих друзей пользователя с ID={} и пользователя с ID={}.",
                firstUserId, secondUserId);
        return userService.getMutualFriends(firstUserId, secondUserId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        userService.addFriend(userId, friendId);
        log.info("Пользователь с ID={} добавляет в список друзей пользователя с ID={}.", friendId, userId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        userService.deleteFriend(userId, friendId);
        log.info("Пользователь с ID={} удаляет из списка друзей пользователя с ID={}.", friendId, userId);
    }
}
