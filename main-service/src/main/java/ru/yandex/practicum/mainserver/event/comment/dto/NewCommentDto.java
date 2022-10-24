package ru.yandex.practicum.mainserver.event.comment.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NewCommentDto {

    @NotNull
    private String text;

    }
