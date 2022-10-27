package ru.practicum.shareit.exception;

/**
 * Переданные данные конфликтуют с введенными ранее.
 */
public class ValidationConflictException extends RuntimeException {
    public ValidationConflictException(String message) {
        super(message);
    }
}
