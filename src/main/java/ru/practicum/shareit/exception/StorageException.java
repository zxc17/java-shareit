package ru.practicum.shareit.exception;

/**
 * Сбой базы данных. Вернулись неожиданные данные, либо не выполнено сохранение данных.
 */
public class StorageException extends RuntimeException {
    public StorageException(String message) {
        super(message);
    }
}
