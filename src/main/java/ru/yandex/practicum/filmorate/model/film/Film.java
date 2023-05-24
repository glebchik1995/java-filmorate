package ru.yandex.practicum.filmorate.model.film;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Film {

    @NotNull(message = "Нумерация начинается с единицы")
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

    private MpaRating mpa;

    private Set<Integer> likes = new HashSet<>();

    private Set<Genre> genres = new LinkedHashSet<>();

}
