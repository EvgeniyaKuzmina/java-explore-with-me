package ru.yandex.practicum.mainservice.request.dto;

import lombok.*;
import ru.yandex.practicum.mainservice.status.Status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * класс DTO для заявки на участие в событии
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RequestDto {

    private Long id;
    private Status status;
    private Long requester;
    private String created;
    private Long event;

    public void setCreated(LocalDateTime createdOn) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.created = createdOn.format(formatter);
    }
}
