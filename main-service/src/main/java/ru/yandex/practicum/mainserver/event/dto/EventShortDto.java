package ru.yandex.practicum.mainserver.event.dto;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * класс DTO для получения короткой информации о событии
 */

@Data
@Builder
public class EventShortDto {
    @Id
    private Long id;
    @NotNull
    private String title;
    @NotNull
    private String annotation;
    @NotNull
    private Boolean paid;
    @NotNull
    private LocalDateTime eventDate;
    @NotNull
    private User initiator;
    @NotNull
    private Category category;
    private Integer confirmedRequests; // Количество одобренных заявок на участие в данном событии
    private Integer views;
    private Set<String> comments;

    @Data
    @Builder
    public static class User {
        @NotNull
        private Long id;
        @NotNull
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
