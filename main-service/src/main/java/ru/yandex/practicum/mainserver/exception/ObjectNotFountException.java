package ru.yandex.practicum.mainserver.exception;

public class ObjectNotFountException extends RuntimeException {

    public ObjectNotFountException(String messages) {
        super(messages);
    }
}
