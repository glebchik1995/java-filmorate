package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserDao;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService implements UserDao {

    private final UserDao userDao;

    @Override
    public User addUser(User user) {
        return userDao.addUser(user);
    }

    @Override
    public User updateUser(User user) {
        return userDao.updateUser(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    @Override
    public User getUserById(long userId) {
        return userDao.getUserById(userId);
    }

    @Override
    public void deleteUserById(long userId) {
        userDao.deleteUserById(userId);
    }

    @Override
    public List<User> getFriendById(long friendId) {
        return userDao.getFriendById(friendId);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        userDao.deleteFriend(userId, friendId);
    }

    @Override
    public List<User> getMutualFriends(long userId, long otherId) {
        return userDao.getMutualFriends(userId, otherId);
    }

    @Override
    public void addFriend(long id, long friendId) {
        userDao.addFriend(id, friendId);
    }

}
