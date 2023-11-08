package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class Director {

    @PositiveOrZero
    private Long id;

    @NotBlank
    private String name;

}
