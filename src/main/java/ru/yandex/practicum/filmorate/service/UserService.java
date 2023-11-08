package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

 public interface UserService {

     List<User> findAllUsers();

     User createUser(User user);

     User updateUser(User user);

     User getUserById(Long id);

     User addFriend(Long userId, Long friendId);

     User unfriend(Long userId, Long friendId);

     List<User> getAllUserFriends(Long userId);

     List<User> getCommonFriends(Long userId, Long targetId);

     void deleteUserById(Long userId);

     List<Event> getFeed(Long userId);
 }
