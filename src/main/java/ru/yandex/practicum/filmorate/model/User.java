package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode
public class User {

    @PositiveOrZero
    private Long id;

    @NotBlank
    @Length(max = 255)
    @Email
    private String email;

    @NotNull
    @NotBlank
    @Length(max = 255)
    private String login;

    @Length(max = 255)
    private String name;

    @NotNull
    @PastOrPresent
    private LocalDate birthday;

    private final Set<Long> friendsId = new HashSet<>();

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("email", email);
        values.put("login", login);
        values.put("name", name);
        values.put("birthday", birthday);
        return values;
    }
}
