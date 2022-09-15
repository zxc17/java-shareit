package ru.practicum.shareit.exception;

/**
 * Переданы некорректные данные.
 */
public class ValidationDataException extends RuntimeException {
    public ValidationDataException(String message) {
        super(message);
    }
}
