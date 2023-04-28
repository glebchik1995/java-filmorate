package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long idGenerator = 0L;

    private Long idPlus() {
        return ++idGenerator;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());

    }

    @Override
    public void createUser(User user) {
        for (User user1 : users.values()) {
            if (user1.getEmail().equals(user.getEmail())) {
                throw new DataAlreadyExistException("Пользователь с таким email уже существует");
            }
        }
        user.setId(idPlus());
        users.put(user.getId(), user);
    }

    @Override
    public void updateUser(User user) {
        for (User user1 : users.values()) {
            if (user1.getEmail().equals(user.getEmail())) {
                throw new DataNotFoundException("Пользователь с таким email уже существует");
            }
            if (!users.containsKey(user.getId())) {
                throw new DataNotFoundException("Пользователь с ID=" + user.getId() + " не найден!");
            }
            users.put(user.getId(), user);
        }
    }

    @Override
    public User getUserById(Long userId) {
        if (!users.containsKey(userId)) {
            throw new DataNotFoundException("Пользователь с ID=" + userId + " не найден!");
        }
        return users.get(userId);
    }
}

