package ru.yandex.practicum.mainservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.mainservice.client.EventClient;
import ru.yandex.practicum.mainservice.client.ViewStats;
import ru.yandex.practicum.mainservice.event.model.Event;
import ru.yandex.practicum.mainservice.event.model.EventParam;
import ru.yandex.practicum.mainservice.exception.ArgumentNotValidException;
import ru.yandex.practicum.mainservice.exception.ConflictException;
import ru.yandex.practicum.mainservice.exception.ObjectNotFountException;
import ru.yandex.practicum.mainservice.status.Status;
import ru.yandex.practicum.mainservice.user.UserService;
import ru.yandex.practicum.mainservice.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
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

    private static final String EVENT_DATE = "EVENT_DATE";
    private static final String VIEWS = "VIEWS";
    private static final String URI = "/events/";
    private final EventRepository repository;
    private final UserService userService;
    private final EventClient client;


    @Override
    public Event createEvent(Event event, Long userid) {
        event.setCreatedOn(LocalDateTime.now());
        event.setState(Status.PENDING);
        User user = userService.getUserById(userid);
        event.setInitiator(user);
        log.info("EventServiceImpl: createEvent — Добавлено событие {}.", event);
        return repository.save(event);
    }

    @Override
    public Event updateEventByInitiator(Event updEvent, Long userId) {
        validateUserIdAndEventId(updEvent.getId(), userId);
        Event event = getEventById(updEvent.getId());
        LocalDateTime publishedTime = LocalDateTime.now();
        if (event.getState() == Status.CANCELED) {
            event.setState(Status.PENDING);
        }
        if (event.getState() != Status.PENDING) {
            log.error("EventServiceImpl: updateEventByInitiator — Событие изменить нельзя");
            throw new ConflictException("Событие изменить нельзя");
        }
        if (event.getEventDate().isBefore(publishedTime.plusHours(2))) {
            log.error("EventServiceImpl: updateEventByInitiator — Нельзя изменить событие, дата начала которого ранее текущего времени");
            throw new ConflictException("Нельзя изменить событие, дата начала которого ранее текущего времени");
        }
        event = updateEvent(updEvent, updEvent.getId());
        log.info("EventServiceImpl: updateEventByInitiator — событие обновлено");
        return event;
    }

    @Override
    public Event cancelEventByInitiator(Long eventId, Long userId) {
        validateUserIdAndEventId(eventId, userId);
        Event event = getEventById(eventId);
        if (event.getState() != Status.PENDING) {
            log.error("EventServiceImpl: cancelEventByInitiator — Событие отменить нельзя");
            throw new ConflictException("Событие отменить нельзя");
        }
        event.setState(Status.CANCELED);
        log.info("EventServiceImpl: cancelEventByInitiator — событие отменено");
        return updViewInEvent(event);
    }

    @Override
    public Collection<Event> getAllEventsByInitiatorId(Long userId, Pageable page) {
        userService.getUserById(userId);
        Collection<Event> events = repository.findByInitiatorId(userId, page).toList();
        events.forEach(this::updViewInEvent);
        log.info("EventServiceImpl: getAllEventsByInitiatorId —  события получены");
        return events;
    }

    @Override
    public Event getEventByIdAndInitiatorId(Long eventId, Long userId) {
        validateUserIdAndEventId(eventId, userId);
        Event event = getEventById(eventId);
        log.info("EventServiceImpl: getEventByIdAndInitiatorId —  получено событие по id");
        return updViewInEvent(event);
    }

    @Override
    public Event updateEventByAdmin(Event updEvent, Long eventId) {
        Event event = getEventById(eventId);

        Optional.ofNullable(updEvent.getRequestModeration()).ifPresent(event::setRequestModeration);
        Optional.ofNullable(updEvent.getLocation()).ifPresent(event::setLocation);
        event = updateEvent(updEvent, eventId);
        log.info("EventServiceImpl: updateEventByAdmin —  событие обновлено админом");
        return event;
    }

    @Override
    public Event publishedEventByAdmin(Long eventId) {
        LocalDateTime publishedTime = LocalDateTime.now();
        Event event = getEventById(eventId);
        if (event.getEventDate().isBefore(publishedTime.plusHours(1)) ||
                event.getEventDate().isEqual(publishedTime.plusMinutes(59))) {
            log.error("EventServiceImpl: publishedEventByAdmin — Нельзя опубликовать событие, дата начала которого ранее текущего времени");
            throw new ConflictException("Нельзя опубликовать событие, дата начала которого ранее текущего времени");
        }

        if (event.getState() != Status.PENDING) {
            log.error("EventServiceImpl: publishedEventByAdmin — Нельзя опубликовать событие");
            throw new ConflictException("Нельзя опубликовать событие");
        }
        event.setState(Status.PUBLISHED);
        event.setPublishedOn(publishedTime);
        log.info("EventServiceImpl: publishedEventByAdmin —  событие опубликовано");
        return updViewInEvent(event);
    }

    @Override
    public Event rejectedEventByAdmin(Long eventId) {
        Event event = getEventById(eventId);
        if (event.getState() == Status.PUBLISHED) {
            log.error("EventServiceImpl: rejectedEventByAdmin — Нельзя отменить опубликованное событие");
            throw new ConflictException("Нельзя отменить опубликованное событие");
        }
        event.setState(Status.CANCELED);
        log.info("EventServiceImpl: rejectedEventByAdmin —  событие отклонено админом");
        return updViewInEvent(event);
    }

    @Override
    public Event getEventById(Long eventId) {
        Optional<Event> event = repository.findById(eventId);
        event.orElseThrow(() -> {
            log.warn("EventServiceImpl: getEventById — События с указанным eventId {} нет", eventId);
            return new ObjectNotFountException("События с указанным eventId " + eventId + " нет");
        });

        log.info("EventServiceImpl: getEventById — Событие с указанным eventId {} получено", eventId);
        return updViewInEvent(event.get());
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

    @Override
    public Event updateEvent(Event updEvent, Long eventId) {
        Event event = getEventById(eventId);

        Optional.ofNullable(updEvent.getTitle()).ifPresent(event::setTitle);
        Optional.ofNullable(updEvent.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(updEvent.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(updEvent.getEventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(updEvent.getCategory()).ifPresent(event::setCategory);
        Optional.ofNullable(updEvent.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(updEvent.getViews()).ifPresent(event::setViews);
        Optional.ofNullable(updEvent.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(updEvent.getConfirmedRequest()).ifPresent(event::setConfirmedRequest);
        return updViewInEvent(event);
    }

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
        Collection<Event> events = repository.findAll(specification, pageable).toList();
        events.forEach(this::updViewInEvent);
        log.info("EventServiceImpl: getEventsByAdminParams — событие с указанными параметрами получено");
        return events;
    }

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
            if (Boolean.TRUE.equals(param.getOnlyAvailable())) {
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
        Collection<Event> events = makeSort(param.getSort(), specification, pageable);
        events.forEach(this::updViewInEvent);
        log.info("EventServiceImpl: getEventsByPublicParams — событие с указанными параметрами получено");
        return events;
    }

    private Collection<Event> makeSort(String sort, Specification<Event> specification, Pageable pageable) {
        if (sort != null) {
            switch (sort) {
                case EVENT_DATE:
                    return repository.findAll(specification, pageable).stream()
                            .sorted(Comparator.comparing(Event::getEventDate))
                            .collect(Collectors.toList());
                case VIEWS:
                    return repository.findAll(specification, pageable).stream()
                            .sorted(Comparator.comparing(Event::getViews))
                            .collect(Collectors.toList());
                default:
                    throw new ArgumentNotValidException("Введено неверное значение сортировки");
            }
        }
        return repository.findAll(specification, pageable).stream().collect(Collectors.toList());
    }

    // вызов метода для получения статистики по просмотрам
    private Collection<ViewStats> getStatisticView(Collection<String> uris) {
        LocalDateTime start = LocalDateTime.now().minusMonths(1L);
        LocalDateTime end = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String startString = start.format(formatter);
        String endString = end.format(formatter);
        return client.getStatistic(startString, endString, uris, true);
    }

    private Event updViewInEvent(Event event) {
        log.error(event.toString());
        Collection<String> uris = Collections.singleton(URI + event.getId());
        Collection<ViewStats> viewStats = getStatisticView(uris);

        if (!viewStats.isEmpty()) {
            viewStats.forEach(vs -> event.setViews(vs.getHits()));
        }
        log.info("EventServiceImpl: updViewInEvent — количество просмотров события получено");
        return repository.save(event);
    }
}
