package ru.yandex.practicum.mainserver.event.dto;

import lombok.*;
import ru.yandex.practicum.mainserver.category.dto.CategoryDto;
import ru.yandex.practicum.mainserver.category.model.Category;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * класс DTO для работы с обновлением события
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateEventDto {
    @NotNull
    private Long id;

    private String title;

    private String description;

    private String annotation;

    private String eventDate;

    private CategoryDto category;
    private Boolean paid;
    private Integer participantLimit;

}
