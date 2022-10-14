package ru.yandex.practicum.mainserver.compilation.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * класс DTO для создания новой подборки событий
 */

@Data
@Builder
public class NewCompilationDto {

    private List<Long> events;
    private Boolean pinned;
    @NotNull
    private String title;
}
