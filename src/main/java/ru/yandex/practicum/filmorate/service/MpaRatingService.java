package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.film.MpaRating;
import ru.yandex.practicum.filmorate.storage.dao.MpaRatingDao;

import java.util.List;
@Service
@RequiredArgsConstructor
public class MpaRatingService {
    private final MpaRatingDao mpaRatingDao;
    public MpaRating getRatingById(int ratingId) {
        return mpaRatingDao.getRatingById(ratingId);
    }

    public List<MpaRating> getAllMpaRatings() {
        return mpaRatingDao.getAllMpaRatings();
    }
}
