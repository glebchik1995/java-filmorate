package ru.yandex.practicum.filmorate.controller;

public class ErrorResponse {
    private final String exception;

    public ErrorResponse(String exception) {
        this.exception = exception;
    }

    public String getException() {
        return exception;
    }
}
