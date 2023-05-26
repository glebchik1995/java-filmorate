package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class MpaRating {

    @NotNull(message = "!= null")
    private long id;

    @NotNull(message = "!= null")
    @Size(max = 40, message = "size < 40")
    private String name;

}
