package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
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
    public User createUser(User user) {
        validate(user);
        user.setId(idPlus());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        validate(user);
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException("������������ �� ������");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(Long userId) {
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException("������������ � ID= " + userId + " �� ������!");
        }
        return users.get(userId);
    }

    @Override
    public User deleteUser(Long userId) {
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException("������������ � ID= " + userId + " �� ������!");
        }
        for (User user : users.values()) {
            user.getFriends().remove(userId);
        }
        return users.remove(userId);
    }

    public void validate(User user) {
        if (user.getLogin().contains(" ") || user.getLogin() == null) {
            throw new ValidationException("���� � ������� ����������� ���������");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("���� � e-mail ����������� ���������");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("���� �������� �� ����� ���� � ������� �������");
        }
    }
}
