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

        //Collection<Event> events = eventService.getAllEventByIds(compilation.getEvents());

        try {
            log.info("CompilationServiceImpl: createCompilation — Добавлена подборка {}.", compilation);
            return repository.save(compilation);
        } catch (DataIntegrityViolationException e) {
            log.error("CompilationServiceImpl: createCompilation — Подборка с таким названием {} уже существует.", compilation.getTitle());
            throw new ConflictException(String.format("Подборка с таким названием %s уже существует.",
                    compilation.getTitle()));
        }
    }

    @Override
    public Compilation updateCompilation(Compilation compilation, Long id) {
        try {
            log.info("CompilationServiceImpl: updateCompilation — Обновлена подборка {}.", compilation);
            return repository.save(compilation);
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

        return updateCompilation(compilation, compId);
    }

    @Override
    public Compilation pinCompilation(Boolean pin, Long compId) {
        Compilation compilation = getCompilationById(compId); // проверка, что подборка с указанным eventId есть

        if (compilation.getPinned().equals(pin)) {
            log.error("CompilationServiceImpl: pinCompilation — Подборка уже закреплена на главной странице");
            throw new ConflictException("Подборка уже закреплена на главной странице");
        }
        compilation.setPinned(pin);

        return updateCompilation(compilation, compId);
    }

    @Override
    public Compilation unpinCompilation(Boolean pin, Long compId) {
        Compilation compilation = getCompilationById(compId); // проверка, что подборка с указанным eventId есть
        // обновляем данные

        if (compilation.getPinned().equals(pin)) {
            log.error("CompilationServiceImpl: unpinCompilation — Подборка не закреплена на главной странице");
            throw new ConflictException("Подборка не закреплена на главной странице");
        }
        compilation.setPinned(pin);

        return updateCompilation(compilation, compId);
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

        return updateCompilation(compilation, compId);
    }

    @Override
    public void removeCompilation(Long id) {
        getCompilationById(id); // проверка, что пользователь с указанным eventId есть
        log.warn("CompilationServiceImpl: removeCompilation — Подборка с указанным eventId {} удалена", id);
        repository.deleteById(id);
    }

    @Override
    public Collection<Compilation> getAllCompilations(Pageable pageable) {
        return repository.findAll(pageable).toList();
    }

    @Override
    public Collection<Compilation> getAllCompilationsWithTitle(Boolean pinned, Pageable pageable) {
        return repository.findByPinnedIs(pinned, pageable).toList();
    }

    @Override
    public Compilation getCompilationById(Long id) {
        Optional<Compilation> compilation = repository.findById(id);
        compilation.orElseThrow(() -> {
            log.warn("CompilationServiceImpl: getCompilationById — Подборки с указанным id {} нет", id);
            return new ObjectNotFountException("Подборки с указанным eventId " + id + " нет");
        });

        log.warn("CompilationServiceImpl: getCompilationById — Подборка с указанным id {} получена", id);
        return compilation.get();
    }
}
