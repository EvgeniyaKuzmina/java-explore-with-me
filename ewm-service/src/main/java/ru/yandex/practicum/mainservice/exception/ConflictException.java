package ru.yandex.practicum.mainservice.exception;

public class ConflictException extends RuntimeException {

    public ConflictException(String messages) {
        super(messages);
    }
}
