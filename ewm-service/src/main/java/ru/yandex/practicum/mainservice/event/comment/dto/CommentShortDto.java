package ru.yandex.practicum.mainserver.event.comment.dto;

import lombok.*;
import ru.yandex.practicum.mainserver.event.dto.EventShortDto;
import ru.yandex.practicum.mainserver.user.dto.UserShortDto;

import javax.validation.constraints.NotNull;
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
public class CommentShortDto {


    private Long id;

    private String text;

    private UserShortDto author;
    private String creat;
    public void setCreat(LocalDateTime creat) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.creat = creat.format(formatter);
    }
}
