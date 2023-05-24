package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.dao.UserDao;

import java.time.LocalDate;
import java.util.*;

@Service
@AllArgsConstructor
public class UserService {

    @Qualifier("UserDao")
    private final UserDao userDao;


    public void addUser(User user) {
        validate(user);
        userDao.addUser(user);
    }

    public void updateUser(User user) {
        validate(user);
        userDao.updateUser(user);
    }

    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    public User getUserById(int id) {
        return userDao.getUserById(id);
    }

    public List<User> getUsersFriendsById(int userId) {
        return userDao.getUsersFriendsById(userId);
    }

    public void addFriends(int userId, int friendId) {
        userDao.addFriends(userId, friendId);
    }

    public void deleteFriend(int userId, int friendId) {
        userDao.deleteFriend(userId, friendId);
    }

    public void deleteUserById(int userId) {
        userDao.deleteUserById(userId);
    }

    public List<User> getMutualFriends(int userId, int otherId) {
        return userDao.getMutualFriends(userId, otherId);
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
