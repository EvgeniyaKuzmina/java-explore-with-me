package ru.yandex.practicum.mainservice.event.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.mainservice.event.location.LocationDto;

import javax.validation.constraints.NotNull;


/**
 * класс DTO для создания нового события
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class NewEventDto {

    @NotNull
    @Length(max = 120)
    @Length(min = 3)
    private String title;
    @NotNull
    @Length(max = 7000)
    @Length(min = 20)
    private String description;
    @NotNull
    @Length(max = 2000)
    @Length(min = 20)
    private String annotation;
    @NotNull
    private String eventDate;
    @NotNull
    private Long category;
    @Builder.Default
    private Boolean paid = false;
    @Builder.Default
    private Boolean requestModeration = true;
    @Builder.Default
    private Integer participantLimit = 0;
    @NotNull
    private LocationDto location;


}
