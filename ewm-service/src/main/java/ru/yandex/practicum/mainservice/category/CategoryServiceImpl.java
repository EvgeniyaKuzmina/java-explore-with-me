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
            log.info("CategoryServiceImpl: createCategory — category was added {}.", category);
            return category;
        } catch (DataIntegrityViolationException e) {
            log.error("CategoryServiceImpl: createCategory — category was name {} already exist",
                    category.getName());
            throw new ConflictException(String.format("Category was name %s already exist",
                    category.getName()));
        }
    }

    @Override
    public Category updateCategory(Category updCategory) {
        Category category = getCategoryById(updCategory.getId()); // проверка, что категория с указанным eventId есть
        Optional.ofNullable(updCategory.getName()).ifPresent(category::setName);

        try {
            log.info("CategoryServiceImpl: updateCategory — category was updated {}.", category);
            return repository.save(category);
        } catch (DataIntegrityViolationException e) {
            log.error("CategoryServiceImpl: updateCategory — category was name {} already exist", updCategory.getName());
            throw new ConflictException(String.format("Category was name %s already exist",
                    updCategory.getName()));
        }
    }

    @Override
    public void removeCategory(Long id) {
        getCategoryById(id);
        repository.deleteById(id);
        log.info("CategoryServiceImpl: removeCategory — category with id {} was deleted", id);
    }

    @Override
    public Collection<Category> getAllCategory(Pageable pageable) {
        Collection<Category> categories = repository.findAll(pageable).toList();
        log.info("CategoryServiceImpl: getAllCategory — list of categories was received");
        return categories;
    }

    @Override
    public Category getCategoryById(Long id) {
        Optional<Category> categoryOpt = repository.findById(id);
        Category category = categoryOpt.orElseThrow(() -> {
            log.warn("CategoryServiceImpl: getCategoryById — category with id {} not exist", id);
            throw new ObjectNotFountException("Category with id " + id + " not exist");
        });
        log.info("CategoryServiceImpl: getCategoryById —  category with id {} was received", id);
        return category;
    }
}
