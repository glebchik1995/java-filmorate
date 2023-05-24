package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public void createUser(User user) {
        validate(user);
        userStorage.createUser(user);
    }

    public void updateUser(User user) {
        validate(user);
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
        final User user = userStorage.getUserById(userId);
        final User friend = userStorage.getUserById(friendId);
        userStorage.getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        final User user = userStorage.getUserById(userId);
        final User friend = userStorage.getUserById(friendId);
        userStorage.getUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getMutualFriends(Long userId, Long otherId) {
        final User firstUser = userStorage.getUserById(userId);
        final User secondUser = userStorage.getUserById(otherId);
        Set<Long> intersections = new HashSet<>(firstUser.getFriends());
        intersections.retainAll(secondUser.getFriends());
        List<User> mutualFriends = new ArrayList<>();
        for (Long intersection : intersections) {
            mutualFriends.add(userStorage.getUserById(intersection));
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
