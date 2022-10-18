package ru.yandex.practicum.mainserver.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.mainserver.category.dto.CategoryDto;
import ru.yandex.practicum.mainserver.event.location.LocationDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * класс DTO для редактирования события администратором.
 */

@Data
@Builder
public class AdminUpdateEventRequest {

    private String title;
    private String annotation;
    private CategoryDto category;
    private String description;
    private LocalDateTime eventDate;
    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private LocalDateTime createdOn;



}
