package ru.yandex.practicum.mainserver.compilation.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * класс DTO для создания новой подборки событий
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NewCompilationDto {

    private List<Long> events;
    private Boolean pinned;
    @NotNull
    private String title;
}
