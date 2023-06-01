package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import static ru.yandex.practicum.filmorate.fields.FieldsTable.*;
@Repository
public class UserMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong(USER_ID))
                .email(rs.getString(EMAIL))
                .login(rs.getString(LOGIN))
                .name(rs.getString(USER_NAME))
                .birthday(Objects.requireNonNull(rs.getDate(BIRTHDAY)).toLocalDate())
                .build();
    }
}
