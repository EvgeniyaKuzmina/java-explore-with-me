package ru.yandex.practicum.mainserver.compilation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * класс DTO для подборки событий
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompilationDto {

    @NotNull
    private Long id;
    private List<Event> events;
    @NotNull
    private Boolean pinned;
    @NotNull
    private String title;

    /**
     * класс для работы с подборками событий
     */
    @Data
    @Builder
    public static class Event {

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
}
