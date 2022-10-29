package ru.yandex.practicum.mainservice.error;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.mainservice.exception.ArgumentNotValidException;
import ru.yandex.practicum.mainservice.exception.ConflictException;
import ru.yandex.practicum.mainservice.exception.ObjectNotFountException;
import ru.yandex.practicum.mainservice.exception.ValidationException;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handlerValidationException(ValidationException e) {
        ApiError apiError = ApiError.builder()
                .errors(List.of())
                .message(e.getMessage())
                .reason("Ошибка валидации")
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        apiError.setEventDate(LocalDateTime.now());
        return apiError;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final List<FieldError> errors = e.getBindingResult().getFieldErrors();
        StringBuilder sb = new StringBuilder();
        for (FieldError error : errors) {
            sb.append(error.getField()).append(" ").append(error.getDefaultMessage());
        }
        ApiError apiError = ApiError.builder()
                .errors(List.of())
                .message(sb.toString())
                .reason("Ошибка валидации аргументов")
                .status(HttpStatus.BAD_REQUEST)
                .build();
        apiError.setEventDate(LocalDateTime.now());
        return apiError;

    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handlerConflictException(ConflictException e) {
        ApiError apiError = ApiError.builder()
                .errors(List.of())
                .message(e.getMessage())
                .reason("Конфликт данных")
                .status(HttpStatus.CONFLICT)
                .build();
        apiError.setEventDate(LocalDateTime.now());
        return apiError;
    }


    @ExceptionHandler(ArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handlerArgumentNotValidException(ArgumentNotValidException e) {
        ApiError apiError = ApiError.builder()
                .errors(List.of())
                .message(e.getMessage())
                .reason("Ошибка валидации аргументов")
                .status(HttpStatus.BAD_REQUEST)
                .build();
        apiError.setEventDate(LocalDateTime.now());
        return apiError;
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handlerMissingRequestHeaderException(MissingRequestHeaderException e) {
        ApiError apiError = ApiError.builder()
                .errors(List.of())
                .message("Не указан заголовок " + e.getHeaderName() + " " + e.getParameter())
                .reason("Не указан заголовок")
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        apiError.setEventDate(LocalDateTime.now());
        return apiError;

    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handlerIllegalArgumentException(IllegalArgumentException e) {
        ApiError apiError = ApiError.builder()
                .errors(List.of())
                .message(e.getMessage())
                .reason("Недопустимый аргумент")
                .status(HttpStatus.BAD_REQUEST)
                .build();
        apiError.setEventDate(LocalDateTime.now());
        return apiError;
    }

    @ExceptionHandler(ObjectNotFountException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handlerIllegalArgumentException(ObjectNotFountException e) {
        ApiError apiError = ApiError.builder()
                .errors(List.of())
                .message(e.getMessage())
                .reason("Не найдены данные")
                .status(HttpStatus.NOT_FOUND)
                .build();
        apiError.setEventDate(LocalDateTime.now());
        return apiError;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handlerConstraintViolationException(ConstraintViolationException e) {
        ApiError apiError = ApiError.builder()
                .errors(List.of())
                .message(e.getMessage())
                .reason("Нарушение ограничений")
                .status(HttpStatus.BAD_REQUEST)
                .build();
        apiError.setEventDate(LocalDateTime.now());
        return apiError;
    }
}
