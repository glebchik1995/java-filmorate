package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.impl.MpaRatingServiceImpl;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaRatingController {

    private final MpaRatingServiceImpl mpaRatingService;

    @GetMapping
    public List<MpaRating> getAllMpaRatings() {
        return mpaRatingService.getAllMpaRatings();
    }

    @GetMapping("/{id}")
    public MpaRating getRatingsMpaById(@PathVariable long id) {
        return mpaRatingService.getRatingById(id);
    }
}
