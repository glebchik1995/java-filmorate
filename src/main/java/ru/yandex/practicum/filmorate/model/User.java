package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class User {

    private int id;

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
