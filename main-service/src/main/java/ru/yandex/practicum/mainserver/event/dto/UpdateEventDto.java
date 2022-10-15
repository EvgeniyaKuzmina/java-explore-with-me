package ru.yandex.practicum.mainserver.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.mainserver.category.model.Category;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * класс DTO для работы с обновлением события
 */

@Data
@Builder
public class UpdateEventDto {
    @NotNull
    private Long id;

    private String title;

    private String description;

    private String annotation;

    private LocalDateTime eventDate;

    private Category category;
    private Boolean paid;
    private Long participantLimit;

}