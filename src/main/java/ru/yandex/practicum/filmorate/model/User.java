package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder(toBuilder = true)
public class User {
    @NotNull(message = "��������� ���������� � �������")
    private long id;

    @NotBlank(message = "���� � email ������ ���� ���������")
    @Email(message = "���� � email ������ ��������� @")
    private String email;

    @NotNull(message = "���� � ������� ������ ���� ���������")
    @NotBlank(message = "���� � ������� �� ������ ��������� �������")
    private String login;

    private String name;

    @NotNull(message = "���� � ����� �������� ������ ���� ���������")
    private LocalDate birthday;

    private final Set<Long> friends = new HashSet<>();
}
