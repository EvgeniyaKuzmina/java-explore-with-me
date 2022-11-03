package ru.yandex.practicum.mainservice.event.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

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
    @Length(min = 3, max = 120)
    private String title;
    @Length(min = 20, max = 7000)
    private String description;
    @Length(min = 20, max = 2000)
    private String annotation;
    private String eventDate;
    private Long category;
    private Boolean paid;
    private Integer participantLimit;
}
