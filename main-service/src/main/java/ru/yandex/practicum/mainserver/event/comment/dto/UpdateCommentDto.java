package ru.yandex.practicum.mainserver.event.comment.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateCommentDto {

    @NotNull
    private Long commentId;
    @NotNull
    private String text;
}
