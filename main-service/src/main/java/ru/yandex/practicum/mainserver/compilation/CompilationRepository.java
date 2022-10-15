package ru.yandex.practicum.mainserver.compilation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.mainserver.compilation.model.Compilation;

/**
 * класс репозиторий для работы с БД подборок событий
 */


public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    Page<Compilation> findAll(Pageable pageable);
    Page<Compilation> findByPinnedIs(Boolean pinned, Pageable pageable);
}
