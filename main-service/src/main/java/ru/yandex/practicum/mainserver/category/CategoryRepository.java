package ru.yandex.practicum.mainserver.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.mainserver.category.model.Category;

/**
 * класс репозиторий для работы с БД категорий событий
 */

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // получение списка категорий с пагинацией

    Page<Category> findAll(Pageable pageable);
}
