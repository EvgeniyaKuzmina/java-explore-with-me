package ru.yandex.practicum.mainserver.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.mainserver.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.mainserver.compilation.mapper.CompilationMapper;
import ru.yandex.practicum.mainserver.compilation.model.Compilation;
import ru.yandex.practicum.mainserver.exception.ConflictException;
import ru.yandex.practicum.mainserver.exception.ObjectNotFountException;

import java.util.Collection;
import java.util.List;
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

    @Override
    public Compilation createCompilation(NewCompilationDto compilationDto) {
        Compilation compilation = CompilationMapper.toCompilationFromNewCompilationDto(compilationDto);
        log.info(compilation.toString());
        // try {
            log.info("Добавлена подборка {}.", compilation);
            return repository.save(compilation);
        /*} catch (DataIntegrityViolationException e) {
            log.error("Подборка с таким названием {} уже существует.", compilation.getTitle());
            throw new ConflictException(String.format("Подборка с таким названием %s уже существует.",
                    compilation.getTitle()));
        }*/
    }

    @Override
    public Compilation updateCompilation(Compilation compilation, Long id) {
        try {
            log.info("Обновлена подборка {}.", compilation);
            return repository.save(compilation);
        } catch (DataIntegrityViolationException e) {
            log.error("Подборка с таким названием {} уже существует.", compilation.getTitle());
            throw new ConflictException(String.format("Подборка с таким названием %s уже существует.",
                    compilation.getTitle()));
        }
    }

    @Override
    public Compilation addEventToCompilation(Long eventId, Long compId) {
        Compilation compilation = getCompilationById(compId); // проверка, что подборка с указанным id есть

        List<Long> eventsId = compilation.getEventsId();

        compilation.getEventsId().forEach(id -> {
            if (Objects.equals(id, eventId)) {
                log.error("В указанной подборке уже есть событие с id {}.", id);
                throw new ConflictException(String.format("В указанной подборке уже есть событие с id %d.", id));
            }
        });
        eventsId.add(eventId);
        compilation.setEventsId(eventsId);

        return updateCompilation(compilation, compId);
    }

    @Override
    public Compilation pinCompilation(Boolean pin, Long compId) {
        Compilation compilation = getCompilationById(compId); // проверка, что подборка с указанным id есть

        if (compilation.getPinned().equals(pin)) {
            log.error("Подборка уже закреплена на главной странице");
            throw new ConflictException("Подборка уже закреплена на главной странице");
        }
        compilation.setPinned(pin);

        return updateCompilation(compilation, compId);
    }

    @Override
    public Compilation unpinCompilation(Boolean pin, Long compId) {
        Compilation compilation = getCompilationById(compId); // проверка, что подборка с указанным id есть
        // обновляем данные

        if (compilation.getPinned().equals(pin)) {
            log.error("Подборка уже откреплена с главной страницы");
            throw new ConflictException("Подборка уже откреплена с главной страницы");
        }
        compilation.setPinned(pin);

        return updateCompilation(compilation, compId);
    }

    @Override
    public Compilation deleteEventFromCompilation(Long eventId, Long compId) {
        Compilation compilation = getCompilationById(compId); // проверка, что подборка с указанным id есть

        List<Long> eventsId = compilation.getEventsId();
        compilation.getEventsId().forEach(id -> {
            if (!Objects.equals(id, eventId)) {
                log.error("В указанной подборке нет события с id {}.", id);
                throw new ConflictException(String.format("В указанной подборке нет события с id %d.", id));
            }
        });
        eventsId.remove(eventId);
        compilation.setEventsId(eventsId);

        return updateCompilation(compilation, compId);
    }

    @Override
    public void removeCompilation(Long id) {
        getCompilationById(id); // проверка, что пользователь с указанным id есть
        log.warn("Подборка с указанным id {} удалена", id);
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
            log.warn("Подборки с указанным id {} нет", id);
            return new ObjectNotFountException("Подборки с указанным id " + id + " нет");
        });

        log.warn("Подборка с указанным id {} получена", id);
        return compilation.get();
    }
}
