package ru.yandex.practicum.mainservice.compilation.dto;

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
@ToString
public class NewCompilationDto {

    private List<Long> events;
    @Builder.Default
    private Boolean pinned = false;
    @NotNull
    private String title;
}
