package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.dao.MpaRatingDao;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MpaRatingService {
    private final MpaRatingDao mpaRatingDao;

    public MpaRating getRatingById(long ratingId) {
        return mpaRatingDao.getMpaRatingById(ratingId);
    }

    public Collection<MpaRating> getAllMpaRatings() {
        return mpaRatingDao.getAllMpaRatings();
    }
}
