package ru.practicum.shareit.exception;

public class ErrorResponse {
    String error;  // Описание ошибки.

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}

