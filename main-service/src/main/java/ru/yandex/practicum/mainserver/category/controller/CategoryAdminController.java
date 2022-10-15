package ru.yandex.practicum.mainserver.category.controller;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.mainserver.category.CategoryService;
import ru.yandex.practicum.mainserver.category.CategoryServiceImpl;
import ru.yandex.practicum.mainserver.category.dto.CategoryDto;
import ru.yandex.practicum.mainserver.category.dto.NewCategoryDto;
import ru.yandex.practicum.mainserver.category.mapper.CategoryMapper;
import ru.yandex.practicum.mainserver.category.model.Category;

import javax.validation.Valid;

/**
 *  класс контроллер для работы с API категорий событий
 */

@RestController
@RequestMapping(path = "/admin/categories")
@Slf4j
public class CategoryAdminController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryAdminController(CategoryServiceImpl categoryService) {
        this.categoryService = categoryService;
    }

    // создание категории
    @PostMapping
    public CategoryDto createUser(@Valid @RequestBody NewCategoryDto categoryDto) {
        return CategoryMapper.toCategoryDto(categoryService.createCategory(categoryDto));
    }

    // обновление категории
    @PatchMapping
    public CategoryDto updateUser(@Valid @RequestBody CategoryDto categoryDto) {
        Category category = categoryService.updateCategory(categoryDto);
        return CategoryMapper.toCategoryDto(category);
    }

    // удаление категории по id
    @DeleteMapping(value = {"/{id}"})
    public void removeUser(@PathVariable @NonNull Long id) {
        categoryService.removeCategory(id);
    }


}
