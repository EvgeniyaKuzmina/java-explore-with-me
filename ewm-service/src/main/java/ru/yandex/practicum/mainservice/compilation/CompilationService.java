package ru.yandex.practicum.mainservice.compilation;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.mainservice.compilation.model.Compilation;

import java.util.Collection;

/**
 * интерфейс описывающий методы для работы с подборками событий
 */
public interface CompilationService {

    /**
     * создание новой подборки
     */
    Compilation createCompilation(Compilation compilation);

    /**
     * обновление подборки
     */
    Compilation updateCompilation(Compilation compilation, Long id);

    /**
     * добавление события в подборку
     */
    Compilation addEventToCompilation(Long eventId, Long compId);

    /**
     * закрепление подборки на главной странице
     */
    Compilation pinCompilation(Boolean pin, Long compId);

    /**
     * удаление события из подборки
     */
    Compilation deleteEventFromCompilation(Long eventId, Long compId);

    /**
     * удаление подборки с главной страницы
     */
    Compilation unpinCompilation(Boolean pin, Long id);

    /**
     * удаление подборки
     */
    void removeCompilation(Long id);

    /**
     * получение списка всех подборок без указания параметра title
     */
    Collection<Compilation> getAllCompilations(Pageable pageable);

    /**
     * получение списка всех подборок с указанием параметра title
     */
    Collection<Compilation> getAllCompilationsByPinned(Boolean pinned, Pageable pageable);

    /**
     * получение подборки по id
     */
    Compilation getCompilationById(Long id);
}
