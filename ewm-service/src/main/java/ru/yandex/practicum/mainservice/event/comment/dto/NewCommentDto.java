package ru.yandex.practicum.mainservice.event.comment.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NewCommentDto {

    @NotNull
    private String text;
}
