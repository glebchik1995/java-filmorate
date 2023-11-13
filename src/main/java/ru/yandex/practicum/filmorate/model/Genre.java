package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Genre {

    @PositiveOrZero
    private Long id;

    @NotBlank(message = "name symbol > 0")
    @Length(max = 50, message = "size < 50")
    private String name;

}