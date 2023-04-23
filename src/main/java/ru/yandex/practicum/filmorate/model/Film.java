package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
public class Film {
    @NotNull(message = "��������� ���������� � �������")
    private long id;

    @NotBlank(message = "���� � ��������� ������ ������ ���� ���������")
    private String name;

    @NotBlank
    @Size(min = 1, max = 200, message = "���������� �������� ������ ���� ������ 0 � �� ��������� 200")
    private String description;

    @NotNull(message = "���� ���� ������ ������ ���� ���������")
    private LocalDate releaseDate;
    @Positive(message = "����������������� �� ����� ���� �������������")
    private int duration;
    private final Set<Long> likes = new HashSet<>();
}
