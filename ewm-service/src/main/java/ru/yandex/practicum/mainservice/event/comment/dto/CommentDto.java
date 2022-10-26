package ru.yandex.practicum.mainservice.event.comment.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    private String creat;
    public void setCreat(LocalDateTime creat) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.creat = creat.format(formatter);
    }
}
