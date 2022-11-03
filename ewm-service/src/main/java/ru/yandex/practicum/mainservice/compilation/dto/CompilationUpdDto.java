package ru.yandex.practicum.mainservice.compilation.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

/**
 * класс DTO для обновления подборки событий
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CompilationUpdDto {

    private Long eventId;
    private Boolean pinned;
    @Length(min = 3, max = 120)
    private String title;
}
