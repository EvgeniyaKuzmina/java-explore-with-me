package ru.yandex.practicum.mainserver.event.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * класс DTO для работы с обновлением события
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UpdateEventDto {
    @NotNull
    private Long eventId;

    private String title;

    private String description;

    private String annotation;

    private String eventDate;

    private Long category;
    private Boolean paid;
    private Integer participantLimit;

}
