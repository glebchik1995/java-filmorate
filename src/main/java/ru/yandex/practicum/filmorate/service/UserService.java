package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public void createUser(User user) {
        userStorage.createUser(user);
    }

    public void updateUser(User user) {
        userStorage.updateUser(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public List<User> getFriends(Long userId) {
        getUserById(userId);
        List<User> friendsList = new ArrayList<>();
        for (Long friendId : userStorage.getUserById(userId).getFriends()) {
            User friendById = userStorage.getUserById(friendId);
            friendsList.add(friendById);
        }
        return friendsList;
    }

    public void addFriend(Long userId, Long friendId) {
        userStorage.getAllUsers().stream()
                .filter(x -> x.getId() == userId)
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + userId + " не найден"));
        userStorage.getAllUsers().stream()
                .filter(x -> x.getId() == friendId)
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + friendId + " не найден"));
        userStorage.getUserById(userId).getFriends().add(friendId);
        userStorage.getUserById(friendId).getFriends().add(userId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        userStorage.getAllUsers().stream()
                .filter(x -> x.getId() == userId)
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + userId + " не найден"));
        userStorage.getAllUsers().stream()
                .filter(x -> x.getId() == friendId)
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + friendId + " не найден"));
        userStorage.getUserById(userId).getFriends().remove(friendId);
        userStorage.getUserById(friendId).getFriends().remove(userId);
    }

    public List<User> getMutualFriends(Long firstUserId, Long secondUserId) {
        userStorage.getAllUsers().stream()
                .filter(x -> x.getId() == firstUserId)
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + firstUserId + " не найден"));
        userStorage.getAllUsers().stream()
                .filter(x -> x.getId() == secondUserId)
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + secondUserId + " не найден"));
        List<Long> general = userStorage.getUserById(firstUserId)
                .getFriends()
                .stream()
                .filter(userStorage.getUserById(secondUserId).getFriends()::contains).collect(Collectors.toList());
        List<User> mutualFriends = new ArrayList<>();
        for (Long user : general) {
            mutualFriends.add(userStorage.getUserById(user));
        }
        return mutualFriends;
    }

    public void validate(User user) {
        if (user.getLogin().contains(" ") || user.getLogin() == null) {
            throw new ValidationException("Поле с логином некорректно заполнено");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Поле с e-mail некорректно заполнено");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем времени");
        }
    }
}
