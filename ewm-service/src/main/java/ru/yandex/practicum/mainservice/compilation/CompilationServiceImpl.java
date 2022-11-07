package ru.yandex.practicum.mainservice.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.mainservice.compilation.model.Compilation;
import ru.yandex.practicum.mainservice.event.EventService;
import ru.yandex.practicum.mainservice.event.model.Event;
import ru.yandex.practicum.mainservice.exception.ConflictException;
import ru.yandex.practicum.mainservice.exception.ObjectNotFountException;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;


/**
 * класс реализующий методы для работы с подборками событий
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository repository;
    private final EventService eventService;

    @Override
    public Compilation createCompilation(Compilation compilation) {
        try {
            compilation = repository.save(compilation);
            log.info("CompilationServiceImpl: createCompilation — Добавлена подборка {}.", compilation);
            return compilation;
        } catch (DataIntegrityViolationException e) {
            log.error("CompilationServiceImpl: createCompilation — Подборка с таким названием {} уже существует.",
                    compilation.getTitle());
            throw new ConflictException(String.format("Подборка с таким названием %s уже существует.",
                    compilation.getTitle()));
        }
    }

    @Override
    public Compilation updateCompilation(Compilation compilation, Long id) {
        try {
            compilation = repository.save(compilation);
            log.info("CompilationServiceImpl: updateCompilation — Обновлена подборка {}.", compilation);
            return compilation;
        } catch (DataIntegrityViolationException e) {
            log.error("CompilationServiceImpl: updateCompilation — Подборка с таким названием {} уже существует.", compilation.getTitle());
            throw new ConflictException(String.format("Подборка с таким названием %s уже существует.",
                    compilation.getTitle()));
        }
    }

    @Override
    public Compilation addEventToCompilation(Long eventId, Long compId) {
        Compilation compilation = getCompilationById(compId); // проверка, что подборка с указанным eventId есть
        Event event = eventService.getEventById(eventId);
        Collection<Event> events = compilation.getEvents();

        events.forEach(e -> {
            if (Objects.equals(e.getId(), eventId)) {
                log.error("CompilationServiceImpl: addEventToCompilation — В указанной подборке уже есть событие с eventId {}.", eventId);
                throw new ConflictException(String.format("В указанной подборке уже есть событие с eventId %d.", eventId));
            }
        });
        events.add(event);
        compilation.setEvents(events);
        compilation = updateCompilation(compilation, compId);

        log.info("CompilationServiceImpl: addEventToCompilation — событие добавлено в подборку");
        return compilation;
    }

    @Override
    public Compilation pinCompilation(Boolean pin, Long compId) {
        Compilation compilation = getCompilationById(compId); // проверка, что подборка с указанным eventId есть

        if (compilation.getPinned().equals(pin)) {
            log.error("CompilationServiceImpl: pinCompilation — Подборка уже закреплена на главной странице");
            throw new ConflictException("Подборка уже закреплена на главной странице");
        }
        compilation.setPinned(pin);
        compilation = updateCompilation(compilation, compId);

        log.info("CompilationServiceImpl: pinCompilation — подборка закреплена на главной странице");
        return compilation;
    }

    @Override
    public Compilation unpinCompilation(Boolean pin, Long compId) {
        Compilation compilation = getCompilationById(compId); // проверка, что подборка с указанным eventId есть

        if (compilation.getPinned().equals(pin)) {
            log.error("CompilationServiceImpl: unpinCompilation — Подборка не закреплена на главной странице");
            throw new ConflictException("Подборка не закреплена на главной странице");
        }
        compilation.setPinned(pin);
        compilation = updateCompilation(compilation, compId);

        log.info("CompilationServiceImpl: unpinCompilation — подборка откреплена с главной странице");
        return compilation;
    }

    @Override
    public Compilation deleteEventFromCompilation(Long eventId, Long compId) {
        Compilation compilation = getCompilationById(compId); // проверка, что подборка с указанным eventId есть
        Event event = eventService.getEventById(eventId);
        Collection<Event> events = compilation.getEvents();

        events.forEach(e -> {
            if (!Objects.equals(e.getId(), eventId)) {
                log.error("CompilationServiceImpl: deleteEventFromCompilation — В указанной подборке нет события с eventId {}.", eventId);
                throw new ConflictException(String.format("В указанной подборке нет события с eventId %d.", eventId));
            }
        });
        events.remove(event);
        compilation.setEvents(events);
        compilation = updateCompilation(compilation, compId);

        log.info("CompilationServiceImpl: deleteEventFromCompilation — событие удалено из подборки");
        return compilation;
    }

    @Override
    public void removeCompilation(Long id) {
        getCompilationById(id);
        repository.deleteById(id);
        log.warn("CompilationServiceImpl: removeCompilation — Подборка с указанным id {} удалена", id);
    }

    @Override
    public Collection<Compilation> getAllCompilations(Pageable pageable) {
        Collection<Compilation> compilations = repository.findAll(pageable).toList();
        log.warn("CompilationServiceImpl: getAllCompilations — список подборок получен");
        return compilations;
    }

    @Override
    public Collection<Compilation> getAllCompilationsByPinned(Boolean pinned, Pageable pageable) {
        Collection<Compilation> compilations = repository.findByPinnedIs(pinned, pageable).toList();
        log.warn("CompilationServiceImpl: getAllCompilationsByPinned — список подборок получен");
        return compilations;
    }

    @Override
    public Compilation getCompilationById(Long id) {
        Optional<Compilation> compilationOpt = repository.findById(id);
        Compilation compilation = compilationOpt.orElseThrow(() -> {
            log.warn("CompilationServiceImpl: getCompilationById — Подборки с указанным id {} нет", id);
            return new ObjectNotFountException("Подборки с указанным eventId " + id + " нет");
        });

        log.info("CompilationServiceImpl: getCompilationById — Подборка с указанным id {} получена", id);
        return compilation;
    }
}
