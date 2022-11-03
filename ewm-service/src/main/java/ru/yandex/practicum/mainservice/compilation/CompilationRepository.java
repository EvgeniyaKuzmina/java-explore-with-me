package ru.yandex.practicum.mainservice.compilation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.mainservice.compilation.model.Compilation;

/**
 * класс репозиторий для работы с БД подборок событий
 */
public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    /**
     * получение списка всех подборок с пагинацией
     */
    Page<Compilation> findAll(Pageable pageable);

    /**
     * получение списка всех подборок закреплённых или не закреплённых на главной странице
     */
    Page<Compilation> findByPinnedIs(Boolean pinned, Pageable pageable);
}
