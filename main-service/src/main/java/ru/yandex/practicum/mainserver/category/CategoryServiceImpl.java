package ru.yandex.practicum.mainserver.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.mainserver.category.dto.CategoryDto;
import ru.yandex.practicum.mainserver.category.dto.NewCategoryDto;
import ru.yandex.practicum.mainserver.category.mapper.CategoryMapper;
import ru.yandex.practicum.mainserver.category.model.Category;
import ru.yandex.practicum.mainserver.exception.ConflictException;
import ru.yandex.practicum.mainserver.exception.ObjectNotFountException;

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
            log.info("Добавлена категория {}.", category);
            return repository.save(category);
        } catch (DataIntegrityViolationException e) {
            log.error("Категория с таким названием {} уже существует.", category.getName());
            throw new ConflictException(String.format("Категория с таким названием %s уже существует.",
                    category.getName()));
        }
    }

    @Override
    public Category updateCategory(CategoryDto categoryDto) {
        Category category = getCategoryById(categoryDto.getId()); // проверка, что категория с указанным id есть
        // обновляем данные
        Optional.ofNullable(categoryDto.getName()).ifPresent(category::setName);

        try {
            log.info("Обновлена категория {}.", category);
            return repository.save(category);
        } catch (DataIntegrityViolationException e) {
            log.error("Категория с таким названием {} уже существует.", categoryDto.getName());
            throw new ConflictException(String.format("Категория с таким названием %s уже существует.",
                    categoryDto.getName()));
        }
    }

    //Обратите внимание: с категорией не должно быть связано ни одного события.
    @Override
    public void removeCategory(Long id) {
        Category category = getCategoryById(id); // проверка, что категория с указанным id есть

        try {
            repository.deleteById(id);
            log.warn("Категория с указанным id {} удалена", id);
        } catch (DataIntegrityViolationException e) {
            log.error("Категория {} связана с событием.", category.getName());
            throw new ConflictException(String.format("Категория %s связана с событием.",
                    category.getName()));
            //TODO создать метод в репозитории события для получения событий по id категории
        }
    }

    @Override
    public Collection<Category> getAllCategory(Pageable pageable) {
        return repository.findAll(pageable).toList();
    }

    @Override
    public Category getCategoryById(Long id) {
        Optional<Category> category = repository.findById(id);
        category.orElseThrow(() -> {
            log.warn("Категории с указанным id {} нет", id);
            throw new ObjectNotFountException("Категории с указанным id " + id + " нет");
        });

        log.warn("Категория с указанным id {} получена", id);
        return category.get();
    }
}
