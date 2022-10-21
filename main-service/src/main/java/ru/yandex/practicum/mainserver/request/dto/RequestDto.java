package ru.yandex.practicum.mainserver.request.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import ru.yandex.practicum.mainserver.status.Status;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * класс DTO для заявки на участие в событии
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestDto {

    private Long id;
    private Status status;
    private Long requesterId;
    private LocalDateTime created;
    private Long eventId;


}
