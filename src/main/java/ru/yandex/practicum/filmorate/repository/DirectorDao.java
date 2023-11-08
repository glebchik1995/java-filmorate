package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorDao {
    List<Director> getAllDirectors();

    Optional<Director> getDirectorById(Long id);

    Optional<Director> createDirector(Director director);

    Optional<Director> updateDirector(Director director);

    void deleteDirectorById(Long id);
}
