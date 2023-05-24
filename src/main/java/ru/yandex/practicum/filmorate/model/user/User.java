package ru.yandex.practicum.filmorate.model.user;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class User {

    private int id;

    private final Map<Integer, FriendshipStatus> friends = new HashMap<>();

    @NotBlank(message = "Поле с email должно быть заполнено")
    @Email(message = "Поле с email должно содержать @")
    private String email;

    @NotNull(message = "Поле с логином должно быть заполнено")
    @NotBlank(message = "Поле с логином не должно содержать пробелы")
    private String login;

    private String name;

    @NotNull(message = "Поле с датой рождения должно быть заполнено")
    private LocalDate birthday;
}
