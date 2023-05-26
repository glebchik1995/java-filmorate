package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserDao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userRowMapper = new UserMapper();

    @Override
    public List<User> getAllUsers() {
        log.debug("getAllUsers().");
        String query = "SELECT * "
                + "FROM users;";
        return jdbcTemplate.query(query, userRowMapper);
    }
    
    @Override
    public User addUser(User user) {
        log.debug("addUser({}).", user);
        String sqlQuery = "INSERT INTO users(login,name, email, birthday) " +
                "VALUES(?,?,?,?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
                    ps.setString(1, user.getLogin());
                    ps.setString(2, user.getName());
                    ps.setString(3, user.getEmail());
                    ps.setDate(4, Date.valueOf(user.getBirthday()));
                    return ps;
                },
                keyHolder);
        if (keyHolder.getKey() != null) {
            user.setId(keyHolder.getKey().intValue());
        }
        return user;

    }
    
    @Override
    public User updateUser(User user) {
        log.debug("updateUser({}).", user);
        check(user.getId());
        String sql = "UPDATE users "
                + "SET (login, name, email, birthday) = (?,?,?,?) " +
                "WHERE user_id=?";
        jdbcTemplate.update(sql, user.getLogin(), user.getName(), user.getEmail(),
                user.getBirthday(), user.getId());
        log.info("Пользователь с ID:{} обновлен ", user.getId());
        return user;
    }

    @Override
    public User getUserById(long id) {
        log.debug("getUserById({}).", id);
        check(id);
        String sql = "SELECT * "
                + "FROM users "
                + "WHERE user_id = ?";
        log.info("Получаем пользователя с ID:{}", id);
        return jdbcTemplate.queryForObject(sql, userRowMapper, id);
    }
    
    @Override
    public void deleteUserById(long id) {
        log.debug("deleteUser({}).", id);
        check(id);
        String sql = "DELETE FROM users " +
                "WHERE user_id = ?";
        jdbcTemplate.update(sql, id);
        log.info("Пользователь c ID: {} удален", id);
    }
    
    @Override
    public void addFriend(long userId, long friendId) {
        log.debug("deleteLike({}, {}).", userId, friendId);
        check(userId);
        check(friendId);
        String sqlQuery = "INSERT INTO friends (user_id, other_user_id, status) VALUES (?, ?, ?)";
        String checkQuery = "SELECT * FROM friends WHERE user_id = ? AND other_user_id = ?";

        SqlRowSet userRows = jdbcTemplate.queryForRowSet(checkQuery, friendId, userId);

        if (!userRows.next()) {
            jdbcTemplate.update(sqlQuery, userId, friendId, false);
            log.info("Пользователь c ID: {} отправил запрос на добавления в друзья пользователю c ID {}",
                    userId, friendId);
        } else {
            jdbcTemplate.update(sqlQuery, userId, friendId, true);
            jdbcTemplate.update(sqlQuery, friendId, userId, true);
            log.info("Пользователь c ID: {} принял заявку в друзья от пользователя c ID {}", userId, friendId);
        }

        getUserById(userId);
    }

    @Override
    public List<User> getFriendById(long id) {
        log.debug("getMutualFriends({}).", id);
        check(id);
        String sql = "SELECT * " +
                "FROM users " +
                "WHERE user_id IN (SELECT other_user_id FROM friends WHERE user_id=?)";
        return jdbcTemplate.query(sql, userRowMapper, id);
    }

    @Override
    public void deleteFriend(long userId, long unFriendId) {
        check(userId);
        check(unFriendId);
        String sql = "DELETE friends "
                + "WHERE user_id = ? "
                + "AND other_user_id = ?";
        jdbcTemplate.update(sql, userId, unFriendId);
        log.info("Пользователь c ID: {} удалил из друзей пользователя c ID {}", userId, unFriendId);

    }

    @Override
    public List<User> getMutualFriends(long userId, long otherUserId) {
        check(userId);
        check(otherUserId);
        String sqlQuery = "SELECT u.* FROM friends AS f " +
                "LEFT JOIN users u ON u.user_id = f.other_user_id " +
                "WHERE f.user_id = ? " +
                "AND f.other_user_id IN " +
                "( " +
                "SELECT f.other_user_id " +
                "FROM friends AS f " +
                "LEFT JOIN users AS u ON u.user_id = f.other_user_id " +
                "WHERE f.user_id = ?" +
                ")";
        log.info("Получаем общих друзей у пользователя с ID: {} и пользователя c ID: {} ", userId, otherUserId);
        return jdbcTemplate.query(sqlQuery, userRowMapper, userId, otherUserId);
    }

    private void check(long userId) {
        String sqlQuery = "SELECT * FROM users WHERE user_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, userId);

        if (!userRows.next()) {
            log.warn("Пользователь с идентификатором {} не найден.", userId);
            throw new DataNotFoundException("Пользователь с идентификатором " + userId + " не найден.");
        }
    }
}
