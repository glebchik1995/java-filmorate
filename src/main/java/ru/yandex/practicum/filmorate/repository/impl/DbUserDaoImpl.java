package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectSaveException;
import ru.yandex.practicum.filmorate.exception.ObjectUpdateException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DbUserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> findAllUsers() {

        String sqlQuery = "SELECT * " +
                "          FROM users";

        List<User> users = jdbcTemplate.query(sqlQuery, this::makeUser);

        log.info("Получены все пользователи. Количество пользователей в списке = {}", users.size());

        return users;
    }

    @Override
    public User createUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        Long userId = simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();
        user = getUserById(userId);
        log.info("Добавлен пользователь с ID={}", userId);
        return user;
    }

    @Override
    public User updateUser(User user) {

        String sqlQuery = "UPDATE users SET " +
                "          email = ?, " +
                "          login = ?, " +
                "          name = ?, " +
                "          birthday = ? " +
                "                  WHERE user_id = ?";

        int response = jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());

        if (response == 1) {
            log.info("Изменен пользователь с ID={}", user.getId());
            return getUserById(user.getId());
        } else {
            throw new ObjectUpdateException(String.format("Ошибка обновления пользователя с ID=%d", user.getId()));
        }
    }


    @Override
    public User getUserById(Long userId) {

        String sqlQuery = "SELECT * " +
                "          FROM users " +
                "          WHERE user_id=?";

        List<User> users = jdbcTemplate.query(sqlQuery, this::makeUser, userId);

        if (!users.isEmpty()) {
            log.info("Получен пользователь с ID={}", userId);
            return users.get(0);
        } else {
            throw new DataNotFoundException(String.format("Пользователь с ID %s не найден", userId));
        }
    }

    @Override
    public User unfriend(Long userId, Long friendId) {

        getUserById(userId);
        getUserById(friendId);

        String sqlSelectQuery = "SELECT * " +
                "                FROM friend_request " +
                "                WHERE initiator_user_id = ? " +
                "                AND target_user_id = ? " +
                "                AND confirmed = true";

        SqlRowSet requestRows = jdbcTemplate.queryForRowSet(sqlSelectQuery, userId, friendId);

        if (requestRows.next()) {

            String sqlUpdateQuery = "UPDATE friend_request " +
                    "                SET (initiator_user_id, target_user_id, confirmed) = (?, ?, false)";

            int updatedRowsNum = jdbcTemplate.update(sqlUpdateQuery, friendId, userId);

            if (updatedRowsNum == 0) {
                throw new ObjectSaveException(String.format("Ошибка обновления статуса заявки в друзья. UserId=%d, FriendId=%d", userId, friendId));
            }
        } else {

            log.info("Статус дружбы пользователя c ID={} и пользователя c ID={} изменен на false", friendId, userId);

            String sqlDeleteQuery = "DELETE FROM friend_request " +
                    "                WHERE initiator_user_id = ? " +
                    "                AND target_user_id = ?";

            jdbcTemplate.update(sqlDeleteQuery, userId, friendId);

            log.info("Пользователь c ID={} удалил из друзей пользователя c ID={}", friendId, userId);

        }
        addEvent(new Event(userId, Event.EventType.FRIEND, Event.Operation.REMOVE, friendId));
        return getUserById(userId);
    }

    public User addFriend(Long userId, Long friendId) {

        getUserById(userId);
        getUserById(friendId);

        String sqlSelectQuery = "SELECT * " +
                "                FROM friend_request " +
                "                WHERE target_user_id = ? AND initiator_user_id = ? ";

        SqlRowSet requestRows = jdbcTemplate.queryForRowSet(sqlSelectQuery, userId, friendId);

        if (requestRows.next()) {
            confirmFriendRequest(requestRows.getLong("friend_request_id"));
        } else {
            log.info("Статус дружбы пользователя c ID={} и пользователя c ID={} теперь true", friendId, userId);

            String sqlQuery = "INSERT INTO friend_request(initiator_user_id, target_user_id, confirmed) " +
                    "          SELECT ?, ?, false " +
                    "          WHERE NOT EXISTS (" +
                    "                            SELECT 1 " +
                    "                            FROM friend_request " +
                    "                            WHERE initiator_user_id = ? AND target_user_id = ?)";
            jdbcTemplate.update(sqlQuery,
                    userId, friendId,
                    userId, friendId);

            log.info("Пользователь c ID={} добавил в друзья пользователя c ID={}", friendId, userId);

        }
        addEvent(new Event(userId, Event.EventType.FRIEND, Event.Operation.ADD, friendId));
        return getUserById(userId);
    }

    @Override
    public List<User> getAllUserFriends(Long userId) {

        getUserById(userId);

        String sqlQuery = "SELECT u.* " +
                "          FROM users u " +
                "          WHERE u.user_id in ( " +
                "                               SELECT fr.target_user_id " +
                "                               FROM friend_request fr " +
                "                               WHERE fr.initiator_user_id = ? " +
                "                               UNION " +
                "                               SELECT fr.initiator_user_id " +
                "                               FROM friend_request fr " +
                "                               WHERE fr.target_user_id = ? AND fr.confirmed = true" +
                "                               )";

        log.info("Получен список всех друзей пользователя с ID={}", userId);

        return jdbcTemplate.query(sqlQuery, this::makeUser, userId, userId);
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long targetId) {

        getUserById(userId);
        getUserById(targetId);

        String sqlQuery =
                "SELECT u.* " +
                        "FROM users u " +
                        "WHERE u.user_id IN ( " +
                        "                     SELECT uf.target_user_id " +
                        "                     FROM ( " +
                        "                           SELECT fr.target_user_id " +
                        "                           FROM friend_request fr " +
                        "                           WHERE fr.initiator_user_id = ?  " +

                        "                     UNION " +

                        "                     SELECT fr.initiator_user_id " +
                        "                     FROM friend_request fr " +
                        "                     WHERE fr.target_user_id = ? and fr.confirmed = true " +
                        "                           ) uf " +
                        "                     JOIN ( " +
                        "                            SELECT fr.target_user_id " +
                        "                            FROM friend_request fr " +
                        "                            WHERE fr.initiator_user_id = ?  " +

                        "                      UNION " +

                        "                           SELECT fr.initiator_user_id " +
                        "                           FROM friend_request fr " +
                        "                           WHERE fr.target_user_id = ? and fr.confirmed = true " +
                        "                           ) tf on uf.target_user_id = tf.target_user_id " +
                        "                    );";

        log.info("Получен список общих друзей пользователя с ID={} с пользователем с ID={}", userId, targetId);

        return jdbcTemplate.query(sqlQuery, this::makeUser, userId, userId, targetId, targetId);
    }

    @Override
    public void deleteUserById(Long userId) {

        getUserById(userId);

        String sqlQuery = "DELETE FROM users " +
                "          WHERE user_id = ?";

        jdbcTemplate.update(sqlQuery, userId);
        log.debug("Пользователь с ID = {} удален.", userId);
    }

    @Override
    public List<Event> getFeed(Long userId) {

        getUserById(userId);

        String sqlQuery = "SELECT * " +
                "          FROM event " +
                "          WHERE user_id = ? " +
                "          ORDER BY ts";

        log.info("Получена лента событий");

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> Event.builder()
                .eventId(rs.getLong("event_id"))
                .timestamp(rs.getLong("ts"))
                .userId(rs.getLong("user_id"))
                .eventType(rs.getString("event_type"))
                .operation(rs.getString("operation"))
                .entityId(rs.getLong("entity_id"))
                .build(), userId);
    }

    @Override
    public void addEvent(Event event) {
        new SimpleJdbcInsert(jdbcTemplate).withTableName("event")
                .usingGeneratedKeyColumns("event_id")
                .execute(event.toMap());
        log.info("Добавлено событие с ID={}", event.getEventId());
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                rs.getLong("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                Objects.requireNonNull(rs.getDate("birthday")).toLocalDate());
    }

    private void confirmFriendRequest(Long friendRequestId) {

        String sqlQuery = "UPDATE friend_request " +
                "          SET confirmed = ? " +
                "          WHERE INITIATOR_USER_ID = ?";

        int updatedRowsNum = jdbcTemplate.update(sqlQuery, true, friendRequestId);

        if (updatedRowsNum == 0) {
            throw new ObjectSaveException(
                    String.format("Ошибка обновления статуса заявки в друзья с ID=%d", friendRequestId));
        }
    }

}
