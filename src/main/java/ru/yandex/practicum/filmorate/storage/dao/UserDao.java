package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserDao {

    User addUser(User user);

    User updateUser(User user);

    List<User> getAllUsers();

    User getUserById(long userId);

    void deleteUserById(long userId);

    List<User> getFriendById(long id);

    void deleteFriend(long id, long friendId);

    List<User> getMutualFriends(long id, long otherId);

    void addFriend(long id, long friendId);

}
