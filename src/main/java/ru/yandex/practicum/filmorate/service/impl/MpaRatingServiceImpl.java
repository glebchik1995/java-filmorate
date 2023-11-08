package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.repository.MpaRatingDao;
import ru.yandex.practicum.filmorate.service.MpaRatingService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaRatingServiceImpl implements MpaRatingService {

    private final MpaRatingDao mpaRatingDao;

    public MpaRating getRatingById(Long ratingId) {
        return mpaRatingDao.getMpaRatingById(ratingId);
    }

    public List<MpaRating> getAllMpaRatings() {
        return mpaRatingDao.getAllMpaRatings();
    }
}
