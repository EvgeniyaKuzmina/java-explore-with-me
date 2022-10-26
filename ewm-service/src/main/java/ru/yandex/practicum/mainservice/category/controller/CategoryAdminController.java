package ru.yandex.practicum.mainservice.category.controller;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.mainservice.category.CategoryService;
import ru.yandex.practicum.mainservice.category.CategoryServiceImpl;
import ru.yandex.practicum.mainservice.category.dto.CategoryDto;
import ru.yandex.practicum.mainservice.category.dto.NewCategoryDto;
import ru.yandex.practicum.mainservice.category.mapper.CategoryMapper;
import ru.yandex.practicum.mainservice.category.model.Category;

import javax.validation.Valid;

/**
 * класс контроллер для работы с API категорий событий
 */

@RestController
@RequestMapping(path = "/admin/categories")
@Slf4j
@Validated
public class CategoryAdminController {

    private final CategoryService service;

    @Autowired
    public CategoryAdminController(CategoryServiceImpl service) {
        this.service = service;
    }

    // создание категории
    @PostMapping
    public CategoryDto createCategory(@Valid @RequestBody NewCategoryDto categoryDto) {
        Category category = CategoryMapper.toCategoryFromNewCategoryDto(categoryDto);
        log.warn(category.toString());
        return CategoryMapper.toCategoryDto(service.createCategory(category));
    }

    // обновление категории
    @PatchMapping
    public CategoryDto updateCategory(@Valid @RequestBody CategoryDto categoryDto) {
        Category category = CategoryMapper.toCategory(categoryDto);
        category = service.updateCategory(category);
        return CategoryMapper.toCategoryDto(category);
    }

    // удаление категории по eventId
    @DeleteMapping(value = {"/{id}"})
    public void removeUser(@PathVariable @NonNull Long id) {
        service.removeCategory(id);
    }


}