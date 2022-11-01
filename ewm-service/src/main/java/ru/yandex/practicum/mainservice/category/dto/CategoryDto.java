package ru.yandex.practicum.mainservice.category.dto;

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
public class CategoryDto {

    @NotNull
    private Long id;
    @NotNull
    private String name;
}
