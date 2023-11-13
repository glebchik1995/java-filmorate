package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.util.After;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Film {

    @PositiveOrZero
    private Long id;

    @NotBlank
    @Size(min = 1, max = 40)
    private String name;

    @Size(min = 1, max = 200)
    @NotBlank
    private String description;

    @After("1895-12-28")
    private LocalDate releaseDate;

    @Positive
    private Long duration;

    private MpaRating mpa;

    private List<Genre> genres = new ArrayList<>();

    private final List<Director> directors = new ArrayList<>();

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("description", description);
        values.put("release_date", releaseDate);
        values.put("duration", duration);
        values.put("rating_id", mpa.getId());
        return values;
    }

    public Film(Long id, String name, String description, LocalDate releaseDate, Long duration, MpaRating mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }
}
