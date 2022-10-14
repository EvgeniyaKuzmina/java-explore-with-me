package ru.yandex.practicum.mainserver.category;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.mainserver.category.dto.CategoryDto;
import ru.yandex.practicum.mainserver.category.dto.NewCategoryDto;
import ru.yandex.practicum.mainserver.category.model.Category;
import ru.yandex.practicum.mainserver.user.dto.NewUserDto;
import ru.yandex.practicum.mainserver.user.dto.UserDto;
import ru.yandex.practicum.mainserver.user.model.User;

import java.util.Collection;

/**
 * класс описывающий методы для работы с категориями событий
 */


public interface CategoryService {

    // создание категории
    Category createCategory(NewCategoryDto categoryDto);

    //обновление категории
    Category updateCategory(CategoryDto categoryDto);

    // удаление категории по id
    void removeCategory(Long id);

    // получение списка категорий
    Collection<Category> getAllCategory(Pageable pageable);

    // получение категории по id
    Category getCategoryById(Long id);
}
