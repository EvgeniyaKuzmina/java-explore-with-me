package ru.yandex.practicum.mainservice.event.dto;

import lombok.*;
import ru.yandex.practicum.mainservice.category.dto.CategoryDto;
import ru.yandex.practicum.mainservice.event.location.LocationDto;
import ru.yandex.practicum.mainservice.status.Status;
import ru.yandex.practicum.mainservice.user.dto.UserShortDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * класс DTO для получения полной информации о событии
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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
    private String createdOn;
    @NotNull
    private String eventDate;
    private String publishedOn;
    private Long views;
    private UserShortDto initiator;
    @NotNull
    private CategoryDto category;
    private Collection<CommentShortDto> comments;
    private Integer confirmedRequests; // Количество одобренных заявок на участие в данном событии
    @NotNull
    private LocationDto location;
    private Integer participantLimit;
    private Boolean requestModeration;

    public void setPublishedOn(LocalDateTime publishedOn) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.publishedOn = publishedOn.format(formatter);
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.createdOn = createdOn.format(formatter);
    }

    public void setEventDate(LocalDateTime eventDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.eventDate = eventDate.format(formatter);
    }
}
