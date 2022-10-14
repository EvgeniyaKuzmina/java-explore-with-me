package ru.yandex.practicum.mainserver.exception;

public class ConflictException extends RuntimeException {

    public ConflictException(String messages) {
        super(messages);
    }
}
