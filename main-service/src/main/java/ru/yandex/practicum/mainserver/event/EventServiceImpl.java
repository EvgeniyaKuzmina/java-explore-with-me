package ru.yandex.practicum.mainserver.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.mainserver.event.model.Event;
import ru.yandex.practicum.mainserver.exception.ConflictException;
import ru.yandex.practicum.mainserver.exception.ObjectNotFountException;
import ru.yandex.practicum.mainserver.status.Status;
import ru.yandex.practicum.mainserver.user.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * класс реализующий методы для работы с событиями
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository repository;

    private final UserService userService;


    @Override
    public Event createEvent(Event event) {
        event.setCreatedOn(LocalDateTime.now());
        try {
            log.info("Добавлено событие {}.", event);
            return repository.save(event);
        } catch (DataIntegrityViolationException e) {
            log.error("Произошла ошибка при сохранении данных");
            throw new RuntimeException("Произошла ошибка при сохранении данных");
        }
    }

    @Override
    public Event updateEvent(Event updEvent, Long userId) {
        validateUserIdAndEventId(updEvent.getId(), userId);
        Event event = getEventById(updEvent.getId());

        if (event.getState().equals(Status.CANCELED)) {
            event.setState(Status.WAITING);
        }
        if (!event.getState().equals(Status.WAITING)) {
            log.error("Событие изменить нельзя");
            throw new ConflictException("Событие изменить нельзя");
        }

        event = updateEvent(updEvent);
        try {
            log.info("Событие обновлено {}.", event);
            return repository.save(event);
        } catch (DataIntegrityViolationException e) {
            log.error("Произошла ошибка при сохранении данных");
            throw new RuntimeException("Произошла ошибка при сохранении данных");
        }
    }

    @Override
    public Event cancelEvent(Long eventId, Long userId) {
        validateUserIdAndEventId(eventId, userId);
        Event event = getEventById(eventId);
        if (!event.getState().equals(Status.WAITING)) {
            log.error("Событие отменить нельзя");
            throw new ConflictException("Событие отменить нельзя");
        }
        event.setState(Status.CANCELED);
        try {
            log.info("Статус события обновлён {}.", event.getState());
            return repository.save(event);
        } catch (DataIntegrityViolationException e) {
            log.error("Произошла ошибка при сохранении данных");
            throw new RuntimeException("Произошла ошибка при сохранении данных");
        }
    }

    @Override
    public Collection<Event> getAllEventByUserId(Long userId, Pageable page) {
        userService.getUserById(userId); // проверяем что существует пользователь с таким id
        return repository.findByInitiatorId(userId, page).toList();
    }

    @Override
    public Event getEventUserById(Long eventId, Long userId) {
        validateUserIdAndEventId(eventId, userId);
        return repository.findByInitiatorIdAndId(eventId, userId);
    }


    @Override
    public Collection<Event> getAllEventByAdmin(List<Long> usersIds, List<Status> states, List<Long> categoriesId,
                                                LocalDateTime start, LocalDateTime end, Pageable page) {
        try {
            return repository.findByInitiatorIdInAndStateInAndCategoryIdInAndEventDateBetween(usersIds, states, categoriesId,
                    start, end, page).toList();
        } catch (DataIntegrityViolationException e) {
            log.error("Произошла ошибка при получении данных");
            throw new RuntimeException("Произошла ошибка при получении данных");
        }
    }

    @Override
    public Event updateEventByAdmin(Event updEvent, Long eventId) {
        Event event = updateEvent(updEvent);

        Optional.ofNullable(updEvent.getRequestModeration()).ifPresent(event::setRequestModeration);
        Optional.ofNullable(updEvent.getLocation()).ifPresent(event::setLocation);

        try {
            log.info("Событие обновлено {}.", event);
            return repository.save(event);
        } catch (DataIntegrityViolationException e) {
            log.error("Произошла ошибка при сохранении данных");
            throw new RuntimeException("Произошла ошибка при сохранении данных");
        }
    }


    @Override
    public Event publishedEventByAdmin(Long eventId) {
        Event event = getEventById(eventId);
        if (!event.getState().equals(Status.WAITING) || event.getEventDate().plusHours(1).isAfter(LocalDateTime.now())) {
            log.error("Нельзя опубликовать событие");
            throw new ConflictException("Нельзя опубликовать событие");
        }
        event.setState(Status.PUBLISHED);
        event.setPublishedOn(LocalDateTime.now());
        try {
            log.info("Статус события обновлён {}.", event.getState());
            return repository.save(event);
        } catch (DataIntegrityViolationException e) {
            log.error("Произошла ошибка при сохранении данных");
            throw new RuntimeException("Произошла ошибка при сохранении данных");
        }
    }

    @Override
    public Event rejectedEventByAdmin(Long eventId) {
        Event event = getEventById(eventId);
        if (event.getState().equals(Status.PUBLISHED)) {
            log.error("Нельзя отменить событие");
            throw new ConflictException("Нельзя отменить событие");
        }
        event.setState(Status.REJECTED);
        try {
            log.info("Статус события обновлён {}.", event.getState());
            return repository.save(event);
        } catch (DataIntegrityViolationException e) {
            log.error("Произошла ошибка при сохранении данных");
            throw new RuntimeException("Произошла ошибка при сохранении данных");
        }
    }

    @Override
    public Event getEventById(Long eventId) {
        Optional<Event> event = repository.findById(eventId);
        event.orElseThrow(() -> {
            log.warn("События с указанным id {} нет", eventId);
            return new ObjectNotFountException("События с указанным id " + eventId + " нет");
        });

        log.warn("Событие с указанным id {} получено", eventId);
        return event.get();
    }

    @Override
    public Collection<Event> getAllEvent(String text, List<Long> categoriesId, Boolean paid,
                                         @Nullable Integer participantLimit, LocalDateTime start, @Nullable LocalDateTime end, String sort,
                                         Pageable page) {
        try {
            if (participantLimit == null && end == null) {
                return repository.findByStateAndDescriptionContainingIgnoreCaseOrAnnotationContainingIgnoreCaseAndCategoryIdInAndPaidIsAndEventDateAfter(
                        Status.PUBLISHED, text, text, categoriesId,
                        paid, start, page).toList();
            }
            if (participantLimit == null) {
                return repository.findByStateAndDescriptionContainingIgnoreCaseOrAnnotationContainingIgnoreCaseAndCategoryIdInAndPaidIsAndEventDateBetween(
                        Status.PUBLISHED, text, text, categoriesId,
                        paid, start, end, page).toList();
            }
            if (end == null) {
                return repository.findByStateAndDescriptionContainingIgnoreCaseOrAnnotationContainingIgnoreCaseAndCategoryIdInAndPaidIsAndParticipantLimitLessThanAndEventDateAfter(
                        Status.PUBLISHED, text, text, categoriesId,
                        paid, participantLimit, start, page).toList();
            }
            return repository.findByStateAndDescriptionContainingIgnoreCaseOrAnnotationContainingIgnoreCaseAndCategoryIdInAndPaidIsAndParticipantLimitLessThanAndEventDateBetween(
                    Status.PUBLISHED, text, text, categoriesId,
                    paid, participantLimit, start, end, page).toList();

        } catch (DataIntegrityViolationException e) {
            log.error("Произошла ошибка при получении данных");
            throw new RuntimeException("Произошла ошибка при получении данных");
        }
    }

    private void validateUserIdAndEventId(Long eventId, Long userId) {
        userService.getUserById(userId); // проверяем что существует пользователь с таким id
        Event event = getEventById(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            log.error("Пользователь с id {} не является инициатором события {}.", userId, event.getId());
            throw new ConflictException(String.format("Пользователь с id %d не является инициатором события %d.",
                    userId, event.getId()));
        }
    }

    private Event updateEvent(Event updEvent) {
        Event event = getEventById(updEvent.getId());
        Optional.ofNullable(updEvent.getTitle()).ifPresent(event::setTitle);
        Optional.ofNullable(updEvent.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(updEvent.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(updEvent.getEventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(updEvent.getCategory()).ifPresent(event::setCategory);
        Optional.ofNullable(updEvent.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(updEvent.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        return event;
    }

}
