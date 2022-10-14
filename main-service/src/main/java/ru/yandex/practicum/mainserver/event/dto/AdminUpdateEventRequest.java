package ru.yandex.practicum.mainserver.event.dto;

import lombok.Builder;
import lombok.Data;

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
    private Category category;
    private String description;
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private Long participantLimit;
    private Boolean requestModeration;
    private LocalDateTime createdOn;

    @Data
    @Builder
    public static class Location {
        private Float lat;
        private Float lon;
    }


    @Data
    @Builder
    public static class Category {
        @NotNull
        private Long id;
        @NotNull
        private String name;
    }

}
