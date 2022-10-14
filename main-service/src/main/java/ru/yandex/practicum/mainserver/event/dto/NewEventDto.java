package ru.yandex.practicum.mainserver.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.mainserver.category.model.Category;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


/**
 * класс DTO для создания нового события
 */

@Data
@Builder
public class NewEventDto {

    @NotNull
    private String title;
    @NotNull
    private String description;
    @NotNull
    private String annotation;
    @NotNull
    private LocalDateTime eventDate;
    @NotNull
    private Category category;
    private Boolean paid;
    private Boolean requestModeration;
    private Long participantLimit;
    @NotNull
    private Location location;


    @Data
    @Builder
    public static class Location {
        private Float lat;
        private Float lon;
    }
}
