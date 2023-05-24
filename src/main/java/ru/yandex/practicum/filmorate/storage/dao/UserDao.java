package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.user.User;

import java.util.List;

public interface UserDao {

    User addUser(User user);

    User updateUser(User user);

    List<User> getAllUsers();

    User getUserById(int userId);

    void deleteUserById(int userId);

    List<User> getUsersFriendsById(int id);

    void deleteFriend(int id, int friendId);

    List<User> getMutualFriends(int id, int otherId);

    void addFriends(int id, int friendId);
}
