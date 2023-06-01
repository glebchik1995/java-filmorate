package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Genre {

    @NotNull(message = "!= null")
    private long id;

    @NotBlank(message = "name symbol > 0")
    @Size(max = 50, message = "size < 50")
    private String name;

}

