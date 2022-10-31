package ru.yandex.practicum.mainservice.error;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

/**
 * класс описывающий ошибки
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ApiError {
    private Collection<String> errors;
    private String message;
    private String reason;
    private HttpStatus status;
    private String timestamp;

    public void setEventDate(LocalDateTime timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.timestamp = timestamp.format(formatter);
    }
}
