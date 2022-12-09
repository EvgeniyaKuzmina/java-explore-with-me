package ru.yandex.practicum.mainservice.event.comment.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * класс DTO для работы с комментариями
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NewCommentDto {

    @NotNull
    @Length(min = 2, max = 7000)
    private String text;
}
