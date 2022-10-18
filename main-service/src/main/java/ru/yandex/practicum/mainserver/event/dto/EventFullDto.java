package ru.yandex.practicum.mainserver.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.mainserver.category.dto.CategoryDto;
import ru.yandex.practicum.mainserver.event.location.LocationDto;
import ru.yandex.practicum.mainserver.status.Status;
import ru.yandex.practicum.mainserver.user.dto.UserShortDto;

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
    private Status state;
    @NotNull
    private Boolean paid;
    private LocalDateTime createdOn;
    @NotNull
    private LocalDateTime eventDate;
    private LocalDateTime publishedOn;
    private Integer views;
    private UserShortDto initiator;
    @NotNull
    private CategoryDto category;
    private Set<String> comments;
    private Integer confirmedRequests; // Количество одобренных заявок на участие в данном событии
    @NotNull
    private LocationDto location;
    private Integer participantLimit;
    private Boolean requestModeration;

}
