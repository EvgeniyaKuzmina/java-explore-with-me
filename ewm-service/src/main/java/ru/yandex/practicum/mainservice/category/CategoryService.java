package ru.yandex.practicum.mainservice.category;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.mainservice.category.model.Category;

import java.util.Collection;

/**
 * класс описывающий методы для работы с категориями событий
 */


@Service
public interface CategoryService {

    // создание категории
    Category createCategory(Category category);

    //обновление категории
    Category updateCategory(Category category);

    // удаление категории по eventId
    void removeCategory(Long id);

    // получение списка категорий
    Collection<Category> getAllCategory(Pageable pageable);

    // получение категории по eventId
    Category getCategoryById(Long id);
}
