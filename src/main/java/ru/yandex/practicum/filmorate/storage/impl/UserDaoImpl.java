package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.dao.UserDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static ru.yandex.practicum.filmorate.storage.fields.FieldsTable.*;


@Slf4j
@Repository
public class UserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;


    @Autowired
    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    private User makeUser(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getInt(USER_ID))
                .name(rs.getString(USER_NAME))
                .email(rs.getString(EMAIL))
                .login(rs.getString(LOGIN))
                .birthday(rs.getDate(BIRTHDAY).toLocalDate())
                .build();
    }

    @Transactional
    @Override
    public List<User> getAllUsers() {
        log.debug("getAllUsers().");
        String sqlQuery = "SELECT * FROM users;";
        List<User> listUsers = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeUser(rs));
        log.info("Получен список всех пользователей: {}.", listUsers);
        return listUsers;
    }

    @Transactional
    @Override
    public User addUser(User user) {
        log.debug("addUser({}).", user);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns(USER_ID);
        int result = simpleJdbcInsert.executeAndReturnKey(toMap(user)).intValue();
        user.setId(result);
        log.info("В хранилище сохранен пользователь: {}.", result);
        return user;

    }

    private Map<String, Object> toMap(User user) {
        Map<String, Object> values = new HashMap<>();
        values.put(EMAIL, user.getEmail());
        values.put(LOGIN, user.getLogin());
        values.put(USER_NAME, user.getName());
        values.put(BIRTHDAY, user.getBirthday());
        return values;
    }

    @Transactional
    @Override
    public User updateUser(User user) {
        log.debug("updateUser({}).", user);
        String sqlQuery = "UPDATE users "
                + "SET email = ?, login = ?, name = ?, birthday = ? "
                + "WHERE user_id = ?;";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        if (getUserById(user.getId()) == null) {
            throw new DataNotFoundException("Пользователь с id = " + user.getId() + " не найден.");
        }
        log.debug("Обновлён пользователь c ID: " + user.getId());
        return user;
    }

    @Transactional
    @Override
    public User getUserById(int id) {
        log.debug("getUserById({}).", id);
        String sqlQuery = "SELECT * FROM users WHERE user_id = ?;";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (rs.next()) {
            return User.builder()
                    .id(rs.getInt(USER_ID))
                    .email(rs.getString(EMAIL))
                    .login(rs.getString(LOGIN))
                    .name(rs.getString(USER_NAME))
                    .birthday(Objects.requireNonNull(rs.getDate(BIRTHDAY)).toLocalDate())
                    .build();
        } else {
            throw new DataNotFoundException("Пользователь с id = " + id + " не найден.");
        }
    }

    @Transactional
    @Override
    public void deleteUserById(int id) {
        log.debug("deleteUser({}).", id);
        String sqlQuery = "DELETE FROM users WHERE user_id = ?;";
        if (id > 0) {
            jdbcTemplate.update(sqlQuery, id);
            log.debug("Удален пользователь с ID: {}", id);
        } else
            throw new DataNotFoundException("Пользователь с ID=" + id + " не найден!");
    }

    @Transactional
    @Override
    public List<User> getUsersFriendsById(int id) {
        log.debug("getUsersFriends({}).", id);
        String sqlQuery = "select u.user_id, u.email, u.name, u.login, u.birthday " +
                "from friends as f left join users as u " +
                "on f.other_user_id = u.user_id where f.user_id = ?" +
                "order by u.user_id";

        List<User> friends = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeUser(rs), id);
        log.info("Получен пользователь c ID={}", id);
        return friends;
    }

    @Transactional
    @Override
    public void deleteFriend(int id, int friendId) {
        log.debug("deleteFriend({}, {}).", id, friendId);
        String sqlQuery = "DELETE FROM friends WHERE user_id = ? AND other_user_id = ? ;";
        jdbcTemplate.update(sqlQuery, id, friendId);
        log.debug("Пользователь с ID: {} удалил из друзей Пользователь с ID: {}", id, friendId);
    }

    @Transactional
    @Override
    public List<User> getMutualFriends(int userId, int otherUserId) {
        log.debug("getMutualFriends({}, {}).", userId, otherUserId);
        String query = "SELECT u.* FROM friends f "
                + "JOIN users u ON f.other_user_id = u.user_id "
                + "WHERE f.user_id = ? OR f.user_id = ? AND status = true "
                + "GROUP BY f.other_user_id "
                + "HAVING COUNT(f.user_id) > 1;";
        List<User> listUsers = jdbcTemplate.query(query, (rs, rowNum) -> makeUser(rs), userId, otherUserId);
        log.info("Получен список всех общих друзей:{}.", listUsers);
        return listUsers;
    }

    @Transactional
    @Override
    public void addFriends(int userId, int friendId) {

        if (userId < 1) {
            throw new DataNotFoundException("Пользователь с ID: " + userId + " не найден");
        }
        if (friendId < 1) {
            throw new DataNotFoundException("Пользователь с ID: " + friendId + " не найден");
        }

        String query = "INSERT INTO FRIENDS(user_id, other_user_id, status) VALUES (?,?,true);";
        jdbcTemplate.update(query, userId, friendId);
    }
}
