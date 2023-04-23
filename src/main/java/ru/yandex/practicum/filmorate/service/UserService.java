package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getFriends(Long userId) {
        User user = userStorage.getUserById(userId);
        if (user.getFriends() == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }

        List<User> usersFriend = new ArrayList<>();
        for (Long friend : user.getFriends()) {
            usersFriend.add(userStorage.getUserById(friend));
        }
        return usersFriend;
    }

    public void addFriend(Long userId, Long friendId) {
        userStorage.getUserById(userId).getFriends().add(friendId);
        userStorage.getUserById(friendId).getFriends().add(userId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        userStorage.getUserById(userId).getFriends().remove(friendId);
        userStorage.getUserById(friendId).getFriends().remove(userId);
    }

    public List<User> getMutualFriends(Long firstUserId, Long secondUserId) {
        Set<Long> users = new HashSet<>(userStorage.getUserById(firstUserId).getFriends());
        users.retainAll(userStorage.getUserById(secondUserId).getFriends());
        List<User> mutualFriends = new ArrayList<>();
        for (Long user : users) {
            mutualFriends.add(userStorage.getUserById(user));
        }
        return mutualFriends;
    }
}
