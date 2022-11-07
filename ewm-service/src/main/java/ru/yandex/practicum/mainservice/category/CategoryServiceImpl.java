package ru.yandex.practicum.mainservice.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.mainservice.category.model.Category;
import ru.yandex.practicum.mainservice.exception.ConflictException;
import ru.yandex.practicum.mainservice.exception.ObjectNotFountException;

import java.util.Collection;
import java.util.Optional;

/**
 * класс реализующий методы для работы с категориями событий
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;

    @Override
    public Category createCategory(Category category) {
        try {
            category = repository.save(category);
            log.info("CategoryServiceImpl: createCategory — Добавлена категория {}.", category);
            return category;
        } catch (DataIntegrityViolationException e) {
            log.error("CategoryServiceImpl: createCategory — Категория с таким названием {} уже существует.",
                    category.getName());
            throw new ConflictException(String.format("Категория с таким названием %s уже существует.",
                    category.getName()));
        }
    }

    @Override
    public Category updateCategory(Category updCategory) {
        Category category = getCategoryById(updCategory.getId()); // проверка, что категория с указанным eventId есть
        Optional.ofNullable(updCategory.getName()).ifPresent(category::setName);

        try {
            log.info("CategoryServiceImpl: updateCategory — Обновлена категория {}.", category);
            return repository.save(category);
        } catch (DataIntegrityViolationException e) {
            log.error("CategoryServiceImpl: updateCategory — Категория с таким названием {} уже существует.", updCategory.getName());
            throw new ConflictException(String.format("Категория с таким названием %s уже существует.",
                    updCategory.getName()));
        }
    }

    @Override
    public void removeCategory(Long id) {
        getCategoryById(id);
        repository.deleteById(id);
        log.info("CategoryServiceImpl: removeCategory — Категория с указанным id {} удалена", id);
    }

    @Override
    public Collection<Category> getAllCategory(Pageable pageable) {
        Collection<Category> categories = repository.findAll(pageable).toList();
        log.info("CategoryServiceImpl: getAllCategory — получен список всех категорий");
        return categories;
    }

    @Override
    public Category getCategoryById(Long id) {
        Optional<Category> categoryOpt = repository.findById(id);
        Category category = categoryOpt.orElseThrow(() -> {
            log.warn("CategoryServiceImpl: getCategoryById — Категории с указанным id {} нет", id);
            throw new ObjectNotFountException("Категории с указанным id " + id + " нет");
        });
        log.info("CategoryServiceImpl: getCategoryById — Категория с указанным id {} получена", id);
        return category;
    }
}
