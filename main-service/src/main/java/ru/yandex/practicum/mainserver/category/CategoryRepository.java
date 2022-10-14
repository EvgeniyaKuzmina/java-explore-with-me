package ru.yandex.practicum.mainserver.category;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.mainserver.category.model.Category;
import ru.yandex.practicum.mainserver.user.model.User;

import java.util.List;

/**
 * класс репозиторий для работы с БД категорий событий
 */

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // получение списка категорий с пагинацией
    List<Category> findAllCategories(Pageable pageable);
}
