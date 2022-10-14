package ru.yandex.practicum.mainserver.category.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * класс DTO для работы с категориями
 */

@Data
@Builder
public class CategoryDto {
    @NotNull
    private Long id;
    @NotNull
    private String name;
}
