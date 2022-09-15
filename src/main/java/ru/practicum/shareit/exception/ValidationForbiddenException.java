package ru.practicum.shareit.exception;

/**
 * Нет прав на выполнение запроса.
 */
public class ValidationForbiddenException extends RuntimeException {
    public ValidationForbiddenException(String message) {
        super(message);
    }
}
