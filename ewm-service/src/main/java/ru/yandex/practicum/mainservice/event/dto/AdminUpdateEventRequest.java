package ru.yandex.practicum.mainservice.event.dto;

import lombok.*;
import ru.yandex.practicum.mainservice.event.location.LocationDto;

import java.time.LocalDateTime;

/**
 * класс DTO для редактирования события администратором.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AdminUpdateEventRequest {

    private String title;
    private String annotation;
    private Long category;
    private String description;
    private String eventDate;
    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private LocalDateTime createdOn;
}
