package ru.yandex.practicum.mainserver.category.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * класс DTO для создания новой категории
 */

@Data
@Builder
public class NewCategoryDto {
    @NotNull
    private String name;
}
