package ru.yandex.practicum.mainserver.category.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * класс DTO для создания новой категории
 */


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NewCategoryDto {
    @NotNull
    private String name;
}
