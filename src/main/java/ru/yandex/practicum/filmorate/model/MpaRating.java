package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode
public class MpaRating {

    @PositiveOrZero
    private Long id;

    @NotNull(message = "!= null")
    @Length(max = 40, message = "size < 40")
    private String name;

}
