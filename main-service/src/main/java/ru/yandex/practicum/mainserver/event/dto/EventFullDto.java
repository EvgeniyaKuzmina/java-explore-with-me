package ru.yandex.practicum.mainserver.event.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * класс DTO для получения полной информации о событии
 */

@Data
@Builder
public class EventFullDto {

    private Long id;
    @NotNull
    private String title;
    private String description;
    @NotNull
    private String annotation;
    private String state;
    @NotNull
    private Boolean paid;
    private LocalDateTime createdOn;
    @NotNull
    private LocalDateTime eventDate;
    private LocalDateTime publishedOn;
    private Integer views;
    private User initiator;
    @NotNull
    private Category category;
    private Set<String> comments;
    private Integer confirmedRequests; // Количество одобренных заявок на участие в данном событии
    @NotNull
    private Location location;
    private Long participantLimit;
    private Boolean requestModeration;


    @Data
    @Builder
    public static class Location {
        private Float lat;
        private Float lon;
    }

    @Data
    @Builder
    public static class User {
        private Long id;
        private String name;
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
