package ru.yandex.practicum.mainserver.event.dto;

import lombok.*;
import ru.yandex.practicum.mainserver.category.dto.CategoryDto;
import ru.yandex.practicum.mainserver.event.location.LocationDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

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
    private CategoryDto category;
    private String description;
    private String eventDate;
    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private LocalDateTime createdOn;




}
