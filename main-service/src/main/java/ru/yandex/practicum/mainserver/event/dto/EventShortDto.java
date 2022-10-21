package ru.yandex.practicum.mainserver.event.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import ru.yandex.practicum.mainserver.category.dto.CategoryDto;
import ru.yandex.practicum.mainserver.user.dto.UserShortDto;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * класс DTO для получения короткой информации о событии
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    private Integer confirmedRequests; // Количество одобренных заявок на участие в данном событии
    private Integer views;
    private Set<String> comments;


}
