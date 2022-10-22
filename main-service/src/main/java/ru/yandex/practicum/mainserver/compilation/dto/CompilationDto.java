package ru.yandex.practicum.mainserver.compilation.dto;

import lombok.*;
import ru.yandex.practicum.mainserver.event.dto.EventShortDto;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * класс DTO для подборки событий
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CompilationDto {

    @NotNull
    private Long id;
    private List<EventShortDto> events;
    @NotNull
    private Boolean pinned;
    @NotNull
    private String title;


}
