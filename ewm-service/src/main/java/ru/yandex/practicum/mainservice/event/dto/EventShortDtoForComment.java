package ru.yandex.practicum.mainservice.event.dto;

import lombok.*;
import ru.yandex.practicum.mainservice.category.dto.CategoryDto;
import ru.yandex.practicum.mainservice.user.dto.UserShortDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * класс DTO для получения короткой информации о событии
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventShortDtoForComment {

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
    private Long views;


    public void setEventDate(LocalDateTime eventDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.eventDate = eventDate.format(formatter);
    }
}
