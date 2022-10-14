package ru.yandex.practicum.mainserver.exception;

public class ArgumentNotValidException extends RuntimeException {

    public ArgumentNotValidException(String messages) {
        super(messages);
    }
}
