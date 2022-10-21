package ru.yandex.practicum.mainserver.event.dto;

import lombok.*;
import ru.yandex.practicum.mainserver.event.location.LocationDto;

import javax.validation.constraints.NotNull;


/**
 * класс DTO для создания нового события
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NewEventDto {

    @NotNull
    private String title;
    @NotNull
    private String description;
    @NotNull
    private String annotation;
    @NotNull
    private String eventDate;
    @NotNull
    private Long category;
    private Boolean paid;
    private Boolean requestModeration;
    private Integer participantLimit;
    @NotNull
    private LocationDto location;


}
