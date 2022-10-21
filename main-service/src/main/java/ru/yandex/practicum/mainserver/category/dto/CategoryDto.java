package ru.yandex.practicum.mainserver.category.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * класс DTO для работы с категориями
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryDto {
    @NotNull
    private Long id;
    @NotNull
    private String name;
}
