package ru.yandex.practicum.mainservice.event.comment.dto;

import lombok.*;
import ru.yandex.practicum.mainservice.event.dto.EventShortDtoForComment;
import ru.yandex.practicum.mainservice.status.Status;
import ru.yandex.practicum.mainservice.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * класс DTO для работы с комментариями
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CommentDto {

    private Long id;
    private String text;
    private UserShortDto author;
    private EventShortDtoForComment event;
    private String created;
    private Status status;

    public void setCreated(LocalDateTime created) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.created = created.format(formatter);
    }
}
