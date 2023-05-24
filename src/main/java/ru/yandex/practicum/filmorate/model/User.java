package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
public class User {

    @NotNull(message = "Нумерация начинается с единицы")
    private long id;

    private final Set<Long> friends = new HashSet<>();

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
