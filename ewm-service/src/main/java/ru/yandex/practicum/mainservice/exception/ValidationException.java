package ru.yandex.practicum.mainservice.exception;

public class ValidationException extends RuntimeException {

    public ValidationException(String messages) {
        super(messages);
    }
}
