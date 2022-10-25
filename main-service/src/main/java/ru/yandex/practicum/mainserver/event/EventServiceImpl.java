package ru.yandex.practicum.mainserver.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.mainserver.event.model.Event;
import ru.yandex.practicum.mainserver.event.model.EventParam;
import ru.yandex.practicum.mainserver.exception.ArgumentNotValidException;
import ru.yandex.practicum.mainserver.exception.ConflictException;
import ru.yandex.practicum.mainserver.exception.ObjectNotFountException;
import ru.yandex.practicum.mainserver.status.Status;
import ru.yandex.practicum.mainserver.user.UserService;
import ru.yandex.practicum.mainserver.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * класс реализующий методы для работы с событиями
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final static String EVENT_DATE = "EVENT_DATE";
    private final static String VIEWS = "VIEWS";

    private final EventRepository repository;

    private final UserService userService;


    // создание события
    @Override
    public Event createEvent(Event event, Long userid) {
        event.setCreatedOn(LocalDateTime.now());
        event.setState(Status.PENDING);
        User user = userService.getUserById(userid);
        event.setInitiator(user);
        try {
            log.info("EventServiceImpl: createEvent — Добавлено событие {}.", event);
            return repository.save(event);
        } catch (DataIntegrityViolationException e) {
            log.error("EventServiceImpl: createEvent — Произошла ошибка при сохранении данных");
            throw new RuntimeException("Произошла ошибка при сохранении данных");
        }
    }

    // изменение события инициатором
    @Override
    public Event updateEventByInitiator(Event updEvent, Long userId) {
        validateUserIdAndEventId(updEvent.getId(), userId);
        Event event = getEventById(updEvent.getId());
        LocalDateTime publishedTime = LocalDateTime.now();
        if (event.getState().equals(Status.CANCELED)) {
            event.setState(Status.PENDING);
        }
        if (!event.getState().equals(Status.PENDING)) {
            log.error("EventServiceImpl: updateEventByInitiator — Событие изменить нельзя");
            throw new ConflictException("Событие изменить нельзя");
        }
        if (event.getEventDate().isBefore(publishedTime.plusHours(2))) {
            log.error("EventServiceImpl: updateEventByInitiator — Нельзя изменить событие, дата начала которого ранее текущего времени");
            throw new ConflictException("Нельзя изменить событие, дата начала которого ранее текущего времени");
        }

        return updateEvent(updEvent, updEvent.getId());

    }

    // отмена события инициатором
    @Override
    public Event cancelEventByInitiator(Long eventId, Long userId) {
        validateUserIdAndEventId(eventId, userId);
        Event event = getEventById(eventId);
        if (!event.getState().equals(Status.PENDING)) {
            log.error("EventServiceImpl: cancelEventByInitiator — Событие отменить нельзя");
            throw new ConflictException("Событие отменить нельзя");
        }
        event.setState(Status.CANCELED);
        try {
            log.info("EventServiceImpl: cancelEventByInitiator — Статус события обновлён {}.", event.getState());
            return repository.save(event);
        } catch (DataIntegrityViolationException e) {
            log.error("EventServiceImpl: cancelEventByInitiator — Произошла ошибка при сохранении данных");
            throw new RuntimeException("Произошла ошибка при сохранении данных");
        }
    }

    // получение всех событий инициатора
    @Override
    public Collection<Event> getAllEventsByInitiatorId(Long userId, Pageable page) {
        userService.getUserById(userId); // проверяем что существует пользователь с таким eventId
        return repository.findByInitiatorId(userId, page).toList();
    }

    // получение инициатором события по id
    @Override
    public Event getEventByIdAndInitiatorId(Long eventId, Long userId) {
        validateUserIdAndEventId(eventId, userId);
        return getEventById(eventId);
    }

    // обновление события администратором
    @Override
    public Event updateEventByAdmin(Event updEvent, Long eventId) {
        Event event = getEventById(eventId);

        Optional.ofNullable(updEvent.getRequestModeration()).ifPresent(event::setRequestModeration);
        Optional.ofNullable(updEvent.getLocation()).ifPresent(event::setLocation);

        return updateEvent(updEvent, eventId);
    }

    // публикация события администратором
    @Override
    public Event publishedEventByAdmin(Long eventId) {
        LocalDateTime publishedTime = LocalDateTime.now();
        Event event = getEventById(eventId);
        if (event.getEventDate().isBefore(publishedTime.plusHours(1)) ||
                event.getEventDate().isEqual(publishedTime.plusMinutes(59))) {
            log.error("EventServiceImpl: publishedEventByAdmin — Нельзя опубликовать событие, дата начала которого ранее текущего времени");
            throw new ConflictException("Нельзя опубликовать событие, дата начала которого ранее текущего времени");
        }

        if (!event.getState().equals(Status.PENDING)) {
            log.error("EventServiceImpl: publishedEventByAdmin — Нельзя опубликовать событие");
            throw new ConflictException("Нельзя опубликовать событие");
        }
        event.setState(Status.PUBLISHED);
        event.setPublishedOn(publishedTime);
        try {
            log.info("EventServiceImpl: publishedEventByAdmin — Статус события обновлён {}.", event.getState());
            return repository.save(event);
        } catch (DataIntegrityViolationException e) {
            log.error("EventServiceImpl: publishedEventByAdmin — Произошла ошибка при сохранении данных");
            throw new RuntimeException("Произошла ошибка при сохранении данных");
        }
    }

    // отклонение события администратором
    @Override
    public Event rejectedEventByAdmin(Long eventId) {
        Event event = getEventById(eventId);
        if (event.getState().equals(Status.PUBLISHED)) {
            log.error("EventServiceImpl: rejectedEventByAdmin — Нельзя отменить опубликованное событие");
            throw new ConflictException("Нельзя отменить опубликованное событие");
        }
        event.setState(Status.CANCELED);
        try {
            log.info("EventServiceImpl: rejectedEventByAdmin — Статус события обновлён {}.", event.getState());
            return repository.save(event);
        } catch (DataIntegrityViolationException e) {
            log.error("EventServiceImpl: rejectedEventByAdmin — Произошла ошибка при сохранении данных");
            throw new RuntimeException("Произошла ошибка при сохранении данных");
        }
    }

    // получение события по id
    @Override
    public Event getEventById(Long eventId) {
        Optional<Event> event = repository.findById(eventId);
        event.orElseThrow(() -> {
            log.warn("EventServiceImpl: getEventById — События с указанным eventId {} нет", eventId);
            return new ObjectNotFountException("События с указанным eventId " + eventId + " нет");
        });

        log.info("EventServiceImpl: getEventById — Событие с указанным eventId {} получено", eventId);
        return event.get();
    }

    private void validateUserIdAndEventId(Long eventId, Long userId) {
        userService.getUserById(userId); // проверяем что существует пользователь с таким eventId
        Event event = getEventById(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            log.error("EventServiceImpl: validateUserIdAndEventId — Пользователь с id {} не является инициатором события {}.", userId, event.getId());
            throw new ConflictException(String.format("Пользователь с id %d не является инициатором события %d.",
                    userId, event.getId()));
        }
    }

    // обновление события
    @Override
    public Event updateEvent(Event updEvent, Long eventId) {
        Event event = getEventById(eventId);
        log.info(updEvent.getTitle());
        log.info(event.getTitle());
        Optional.ofNullable(updEvent.getTitle()).ifPresent(event::setTitle);
        Optional.ofNullable(updEvent.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(updEvent.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(updEvent.getEventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(updEvent.getCategory()).ifPresent(event::setCategory);
        Optional.ofNullable(updEvent.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(updEvent.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(updEvent.getConfirmedRequest()).ifPresent(event::setConfirmedRequest);
        try {
            log.info("EventServiceImpl: updateEvent — Событие обновлено {}.", event);
            return repository.save(event);
        } catch (DataIntegrityViolationException e) {
            log.error("EventServiceImpl: updateEvent — Произошла ошибка при сохранении данных");
            throw new RuntimeException("Произошла ошибка при сохранении данных");
        }

    }

    // получения списка событий по указанным параметрам
    @Override
    public Collection<Event> getEventsByAdminParams(EventParam param, Pageable pageable) {

        Specification<Event> specification = null;

        if (param.getUsersId() != null && !param.getUsersId().isEmpty()) {
            specification = Specification.where(specification).and(
                    (root, criteriaQuery, criteriaBuilder) ->
                            criteriaBuilder.in(root.get("initiator").get("id")).value(param.getUsersId())
            );
        }

        if (param.getStates() != null && !param.getStates().isEmpty()) {
            specification = Specification.where(specification).and(
                    (root, criteriaQuery, criteriaBuilder) ->
                            criteriaBuilder.in(root.get("state")).value(param.getStates())
            );
        }

        if (param.getCategoriesId() != null && !param.getCategoriesId().isEmpty()) {
            specification = Specification.where(specification).and(
                    (root, criteriaQuery, criteriaBuilder) ->
                            criteriaBuilder.in(root.get("category").get("id")).value(param.getCategoriesId())
            );
        }

        if (param.getRangeStart() != null) {
            specification = Specification.where(specification).and(
                    (root, criteriaQuery, criteriaBuilder) ->
                            criteriaBuilder.greaterThan(root.get("eventDate").as(LocalDateTime.class), param.getRangeStart())
            );
        }

        if (param.getRangeEnd() != null) {
            specification = Specification.where(specification).and(
                    (root, criteriaQuery, criteriaBuilder) ->
                            criteriaBuilder.lessThan(root.get("eventDate").as(LocalDateTime.class), param.getRangeEnd())
            );
        }

        return repository.findAll(specification, pageable).toList();

    }

    // получение списка событий публичным API по указанным параметрам
    @Override
    public Collection<Event> getEventsByPublicParams(EventParam param, Pageable pageable) {

        Specification<Event> specification = null;

        specification = Specification.where(specification).and(
                (root, criteriaQuery, criteriaBuilder) ->
                        criteriaBuilder.greaterThan(root.get("eventDate").as(LocalDateTime.class), param.getRangeStart())
        );

        if (param.getText() != null && !param.getText().isEmpty()) { //
            Specification<Event> specificationSearch = Specification.where(
                    (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(root.get("description"), "%" + param.getText() + "%")
            );

            specificationSearch = Specification.where(specificationSearch).or(
                    (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(root.get("annotation"), "%" + param.getText() + "%")
            );

            specification = Specification.where(specification)
                    .and(specificationSearch);
        }

        if (param.getPaid() != null) {
            specification = Specification.where(specification).and(
                    (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.in(root.get("paid")).value(param.getPaid())
            );
        }

        if (param.getCategoriesId() != null && !param.getCategoriesId().isEmpty()) {
            specification = Specification.where(specification).and(
                    (root, criteriaQuery, criteriaBuilder) ->
                            criteriaBuilder.in(root.get("category").get("id")).value(param.getCategoriesId())
            );
        }

        if (param.getOnlyAvailable() != null) {
            if (param.getOnlyAvailable()) {
                specification = Specification.where(specification).and(
                        (root, criteriaQuery, criteriaBuilder) ->
                                criteriaBuilder.lessThan(root.get("confirmedRequest"), root.get("participantLimit"))
                );
            }
        }

        if (param.getRangeEnd() != null) {
            specification = Specification.where(specification).and(
                    (root, criteriaQuery, criteriaBuilder) ->
                            criteriaBuilder.lessThan(root.get("eventDate").as(LocalDateTime.class), param.getRangeEnd())
            );
        }
        return makeSort(param.getSort(), specification, pageable);

    }

    // сортировка событий
    private Collection<Event> makeSort(String sort, Specification<Event> specification, Pageable pageable) {
        if (sort != null) {
            return switch (sort) {
                case EVENT_DATE -> repository.findAll(specification, pageable).stream()
                        .sorted(Comparator.comparing(Event::getEventDate))
                        .collect(Collectors.toList());
                case VIEWS -> repository.findAll(specification, pageable).stream()
                        .sorted(Comparator.comparing(Event::getViews))
                        .collect(Collectors.toList());
                default -> throw new ArgumentNotValidException("Введено неверное значение сортировки");
            };
        }
        return repository.findAll(specification, pageable).stream().collect(Collectors.toList());
    }

}
