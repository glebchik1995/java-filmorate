package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Получен запрос на получение списка всех пользователей.");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        log.info("Получен запрос на получение пользователя с ID={}.", id);
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getUsersFriendsById(@PathVariable int id) {
        log.info("Получен запрос на получение списка пользователей, которые являются друзьями пользователя с ID={}.",
                id);
        return userService.getUsersFriendsById(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("Получен запрос на получение списка общих друзей пользователя с ID={} и пользователя с ID={}.",
                id, otherId);
        return userService.getMutualFriends(id, otherId);
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        userService.addUser(user);
        log.info("Пользователь {} успешно добавлен", user);
        return user;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.addFriends(id, friendId);
        log.info("Пользователь с ID={} добавляет в список друзей пользователя с ID={}.", friendId, id);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        userService.updateUser(user);
        log.info("Пользователь {} успешно обновлен", user);
        return user;
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.deleteFriend(id, friendId);
        log.info("Пользователь с ID={} удаляет из списка друзей пользователя с ID={}.", friendId, id);
    }
    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable int id) {
        userService.deleteUserById(id);
        log.info("Пользователь с ID={} успешно удален.", id);

    }
}
