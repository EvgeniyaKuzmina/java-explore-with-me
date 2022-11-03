package ru.yandex.practicum.mainservice.exception;

public class ArgumentNotValidException extends RuntimeException {

    public ArgumentNotValidException(String messages) {
        super(messages);
    }
}
