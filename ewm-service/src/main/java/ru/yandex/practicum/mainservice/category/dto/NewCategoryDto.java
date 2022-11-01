package ru.yandex.practicum.mainservice.category.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

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
    @Length(max = 100)
    private String name;
}
