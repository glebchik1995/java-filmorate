package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class User {

    Set<Long> friendsList = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @NotNull(message = "!= null")
    private long id;

    @NotBlank(message = "email symbol > 0")
    @Email(message = "@")
    private String email;

    @NotNull(message = "!= null")
    @NotBlank(message = "login symbol > 0")
    @Size(max = 50, message = "size < 50")
    private String login;

    @Size(max = 50, message = "size < 50")
    private String name;

    @NotNull(message = "!= null")
    @PastOrPresent(message = "should not be in the future")
    private LocalDate birthday;
}
