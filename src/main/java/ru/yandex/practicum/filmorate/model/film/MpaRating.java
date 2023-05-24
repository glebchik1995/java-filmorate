package ru.yandex.practicum.filmorate.model.film;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MpaRating {

    private int id;

    private String name;

    private String description;

}
