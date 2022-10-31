package ru.yandex.practicum.mainservice.category.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.mainservice.category.CategoryService;
import ru.yandex.practicum.mainservice.category.CategoryServiceImpl;
import ru.yandex.practicum.mainservice.category.mapper.dto.CategoryDto;
import ru.yandex.practicum.mainservice.category.mapper.CategoryMapper;
import ru.yandex.practicum.mainservice.category.model.Category;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.Collection;

/**
 * класс контроллер для работы с публичным API категорий событий
 */
@RestController
@RequestMapping(path = "/categories")
@Slf4j
public class CategoryPublicController {
    private static final String FROM = "0";
    private static final String SIZE = "10";
    private final CategoryService categoryService;

    @Autowired
    public CategoryPublicController(CategoryServiceImpl categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping(value = {"/{catId}"})
    public CategoryDto getCategoryById(@PathVariable @NotNull Long catId) {
        log.info("CategoryPublicController: getCategoryById — получен запрос на получение категории по id");
        Category category = categoryService.getCategoryById(catId);
        return CategoryMapper.toCategoryDto(category);
    }

    @GetMapping
    public Collection<CategoryDto> getAllCategories(@RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
                                                    @RequestParam(defaultValue = SIZE) @Positive Integer size) {
        log.info("CategoryPublicController: getAllCategories — получен запрос на получение списка всех категорий");
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        Collection<CategoryDto> allCategoryDto = new ArrayList<>();
        categoryService.getAllCategory(pageable).forEach(c -> allCategoryDto.add(CategoryMapper.toCategoryDto(c)));
        return allCategoryDto;
    }
}
