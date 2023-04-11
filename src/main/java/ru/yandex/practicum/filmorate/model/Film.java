package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class Film {

    private int id;

    @NotBlank(message = "Поле с названием фильма должно быть заполнено")
    private String name;

    @NotBlank
    @Size(min = 1, max = 200, message = "Количество символов должно быть больше 0 и не превышать 200")
    private String description;

    @NotNull(message = "Поле дата релиза должно быть заполнено")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность не может быть отрицательной")
    private int duration;
}
