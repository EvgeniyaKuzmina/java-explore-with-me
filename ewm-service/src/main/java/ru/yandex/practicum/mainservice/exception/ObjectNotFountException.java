package ru.yandex.practicum.mainservice.exception;

public class ObjectNotFountException extends RuntimeException {

    public ObjectNotFountException(String messages) {
        super(messages);
    }
}
