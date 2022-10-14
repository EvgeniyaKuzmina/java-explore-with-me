package ru.yandex.practicum.mainserver.event.comment.dto;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * класс DTO для работы с комментариями
 */

@Builder
@Data
public class CommentDto {

    private Long id;
    @NotNull
    private String text;
    @NotNull
    private Long event_id;
    private Long author_id;
    private LocalDateTime creat;

}
