package ru.yandex.practicum.mainserver.category.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.mainserver.category.CategoryService;
import ru.yandex.practicum.mainserver.category.CategoryServiceImpl;
import ru.yandex.practicum.mainserver.category.dto.CategoryDto;
import ru.yandex.practicum.mainserver.category.mapper.CategoryMapper;
import ru.yandex.practicum.mainserver.category.model.Category;

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


    // получение категории по Id
    @GetMapping(value = {"/{catId}"})
    public CategoryDto getCategoryById(@PathVariable @NotNull Long catId) {
        Category category = categoryService.getCategoryById(catId);
        return CategoryMapper.toCategoryDto(category);
    }

    // получение списка всех категорий
    @GetMapping
    public Collection<CategoryDto> getAllCategories(@RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
                                                    @RequestParam(defaultValue = SIZE) @Positive Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        Collection<CategoryDto> allCategoryDto = new ArrayList<>();

        categoryService.getAllCategory(pageable).forEach(c -> allCategoryDto.add(CategoryMapper.toCategoryDto(c)));
        return allCategoryDto;
    }
}
