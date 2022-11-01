package ru.yandex.practicum.mainservice.category.mapper;

import ru.yandex.practicum.mainservice.category.dto.CategoryDto;
import ru.yandex.practicum.mainservice.category.dto.NewCategoryDto;
import ru.yandex.practicum.mainservice.category.model.Category;

public class CategoryMapper {

    public static CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category toCategory(CategoryDto categoryDto) {
        return Category.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .build();
    }

    public static Category toCategoryFromNewCategoryDto(NewCategoryDto categoryDto) {
        return Category.builder()
                .name(categoryDto.getName())
                .build();
    }
}
