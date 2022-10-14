package ru.yandex.practicum.mainserver.exception;

public class ValidationException extends RuntimeException {

    public ValidationException(String messages) {
        super(messages);
    }
}
