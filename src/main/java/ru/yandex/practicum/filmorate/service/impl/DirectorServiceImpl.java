package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectSaveException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.repository.DirectorDao;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorServiceImpl implements DirectorService {

    private final DirectorDao directorDao;

    public Director createDirector(Director director) {
        Optional<Director> createdDirector = directorDao.createDirector(director);
        if (createdDirector.isPresent()) return createdDirector.get();
        else {
            log.info("Ошибка занесения режиссера: {}", director);
            throw new ObjectSaveException(String.format("Ошибка занесения режиссера. Ошибка входных данных! %s", director));
        }
    }

    public Director updateDirector(Director director) {
        Optional<Director> updatedDirector = directorDao.updateDirector(director);
        if (updatedDirector.isPresent()) return updatedDirector.get();
        else {
            log.info("Ошибка обновления режиссера: {}", director);
            throw new ObjectSaveException(String.format("Ошибка обновления режиссера. Ошибка входных данных! %s", director));
        }
    }

    public void deleteDirectorById(Long directorId) {
        directorDao.deleteDirectorById(directorId);
    }

    public Director getDirectorById(Long directorId) {
        Optional<Director> gotDirector = directorDao.getDirectorById(directorId);
        if (gotDirector.isPresent()) return gotDirector.get();
        else {
            log.info("Ошибка получения режиссера: {}", directorId);
            throw new DataNotFoundException(String.format("Ошибка получения режиссера. %x", directorId));
        }
    }

    public List<Director> getAllDirectors() {
        return directorDao.getAllDirectors();
    }
}
