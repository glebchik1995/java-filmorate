package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserDao;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Override
    public List<User> findAllUsers() {
        return userDao.findAllUsers();
    }

    @Override
    public User createUser(User user) {
        validateUserName(user);
        return userDao.createUser(user);
    }

    @Override
    public User updateUser(User user) {
        validateUserName(user);
        return userDao.updateUser(user);
    }

    @Override
    public User getUserById(Long userId) {
        return userDao.getUserById(userId);
    }

    @Override
    public User addFriend(Long userId, Long friendId) {
        return userDao.addFriend(userId, friendId);
    }

    @Override
    public User unfriend(Long userId, Long friendId) {
        return userDao.unfriend(userId, friendId);
    }

    @Override
    public List<User> getAllUserFriends(Long userId) {
        return userDao.getAllUserFriends(userId);
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long targetId) {
        return userDao.getCommonFriends(userId, targetId);
    }

    @Override
    public void deleteUserById(Long userId) {
        userDao.deleteUserById(userId);
    }

    @Override
    public List<Event> getFeed(Long userId) {
        return userDao.getFeed(userId);
    }

    private void validateUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
