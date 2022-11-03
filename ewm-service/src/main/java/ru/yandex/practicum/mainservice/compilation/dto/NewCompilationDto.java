package ru.yandex.practicum.mainservice.compilation.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

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
    @Length(max = 120)
    @Length(min = 3)
    private String title;
}
