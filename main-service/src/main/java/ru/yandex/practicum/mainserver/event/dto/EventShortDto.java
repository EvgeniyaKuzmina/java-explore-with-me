package ru.yandex.practicum.mainserver.event.dto;

import lombok.*;
import ru.yandex.practicum.mainserver.category.dto.CategoryDto;
import ru.yandex.practicum.mainserver.user.dto.UserShortDto;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * класс DTO для получения короткой информации о событии
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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
    private String eventDate;
    @NotNull
    private UserShortDto initiator;
    @NotNull
    private CategoryDto category;
    private Integer confirmedRequests;
    private Integer views;
    private Set<String> comments;

    public void setEventDate(LocalDateTime eventDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.eventDate = eventDate.format(formatter);
    }
}
