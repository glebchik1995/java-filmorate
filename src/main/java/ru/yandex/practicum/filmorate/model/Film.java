package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Film {

    @EqualsAndHashCode.Exclude
    @NotNull(message = "!= null")
    private long id;

    @NotBlank(message = "name symbol > 0")
    private String name;

    @Size(max = 200, message = "size < 400")
    private String description;

    @NotNull(message = "!= null")
    private LocalDate releaseDate;

    @Positive(message = "id >= 0")
    private long duration;

    @NotNull
    private MpaRating mpa;

    private Set<Long> likes = new HashSet<>();

    private List<Genre> genres;

}
