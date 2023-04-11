package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final String exception;

    public ErrorResponse(String exception) {
        this.exception = exception;
    }
}
