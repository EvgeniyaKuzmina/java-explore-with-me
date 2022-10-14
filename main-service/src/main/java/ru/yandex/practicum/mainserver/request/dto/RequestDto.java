package ru.yandex.practicum.mainserver.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.mainserver.status.Status;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * класс DTO для заявки на участие в событии
 */
@Data
@Builder
public class RequestDto {

    private Long id;
    private Status status;
    private Long requesterId;
    private LocalDateTime created;
    private Long eventId;


}
