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
            log.info("CompilationServiceImpl: createCompilation — added collection of events {}.", compilation);
            return compilation;
        } catch (DataIntegrityViolationException e) {
            log.error("CompilationServiceImpl: createCompilation — collection of events with name {} already exist",
                    compilation.getTitle());
            throw new ConflictException(String.format("Collection of events with name %s already exist",
                    compilation.getTitle()));
        }
    }

    @Override
    public Compilation updateCompilation(Compilation compilation, Long id) {
        try {
            compilation = repository.save(compilation);
            log.info("CompilationServiceImpl: updateCompilation — collection of events was updated {}.", compilation);
            return compilation;
        } catch (DataIntegrityViolationException e) {
            log.error("CompilationServiceImpl: updateCompilation — collection of events with name {} already exist", compilation.getTitle());
            throw new ConflictException(String.format("Collection of events with name %s already exist",
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
                log.error("CompilationServiceImpl: addEventToCompilation — in current compilation event with id {} already exist", eventId);
                throw new ConflictException(String.format("In current compilation event with %d already exist", eventId));
            }
        });
        events.add(event);
        compilation.setEvents(events);
        compilation = updateCompilation(compilation, compId);

        log.info("CompilationServiceImpl: addEventToCompilation — event was added to compilation");
        return compilation;
    }

    @Override
    public Compilation pinCompilation(Boolean pin, Long compId) {
        Compilation compilation = getCompilationById(compId); // проверка, что подборка с указанным eventId есть

        if (compilation.getPinned().equals(pin)) {
            log.error("CompilationServiceImpl: pinCompilation — compilation is already pinned on the main page");
            throw new ConflictException("Compilation is already pinned on the main page");
        }
        compilation.setPinned(pin);
        compilation = updateCompilation(compilation, compId);

        log.info("CompilationServiceImpl: pinCompilation — compilation pinned on the main page");
        return compilation;
    }

    @Override
    public Compilation unpinCompilation(Boolean pin, Long compId) {
        Compilation compilation = getCompilationById(compId); // проверка, что подборка с указанным eventId есть

        if (compilation.getPinned().equals(pin)) {
            log.error("CompilationServiceImpl: unpinCompilation — compilation not pinned on the main page");
            throw new ConflictException("Compilation not pinned on the main page");
        }
        compilation.setPinned(pin);
        compilation = updateCompilation(compilation, compId);

        log.info("CompilationServiceImpl: unpinCompilation — compilation unpinned from the main page");
        return compilation;
    }

    @Override
    public Compilation deleteEventFromCompilation(Long eventId, Long compId) {
        Compilation compilation = getCompilationById(compId); // проверка, что подборка с указанным eventId есть
        Event event = eventService.getEventById(eventId);
        Collection<Event> events = compilation.getEvents();

        events.forEach(e -> {
            if (!Objects.equals(e.getId(), eventId)) {
                log.error("CompilationServiceImpl: deleteEventFromCompilation — event with id {} not exist in current compilation ", eventId);
                throw new ConflictException(String.format("Event with id %d not exist in current compilation", eventId));
            }
        });
        events.remove(event);
        compilation.setEvents(events);
        compilation = updateCompilation(compilation, compId);

        log.info("CompilationServiceImpl: deleteEventFromCompilation — event was deleted from compilation");
        return compilation;
    }

    @Override
    public void removeCompilation(Long id) {
        getCompilationById(id);
        repository.deleteById(id);
        log.info("CompilationServiceImpl: removeCompilation — compilation with id {} was deleted", id);
    }

    @Override
    public Collection<Compilation> getAllCompilations(Pageable pageable) {
        Collection<Compilation> compilations = repository.findAll(pageable).toList();
        log.info("CompilationServiceImpl: getAllCompilations — list of compilations was received");
        return compilations;
    }

    @Override
    public Collection<Compilation> getAllCompilationsByPinned(Boolean pinned, Pageable pageable) {
        Collection<Compilation> compilations = repository.findByPinnedIs(pinned, pageable).toList();
        log.info("CompilationServiceImpl: getAllCompilationsByPinned — list of pinned compilations was received");
        return compilations;
    }

    @Override
    public Compilation getCompilationById(Long id) {
        Optional<Compilation> compilationOpt = repository.findById(id);
        Compilation compilation = compilationOpt.orElseThrow(() -> {
            log.error("CompilationServiceImpl: getCompilationById — compilation with id {} not exist", id);
            return new ObjectNotFountException("Compilation with id " + id + " not exist");
        });

        log.info("CompilationServiceImpl: getCompilationById — compilation with id {} was received", id);
        return compilation;
    }
}
