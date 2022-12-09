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
import java.util.*;
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
        LocalDateTime publishedTime = LocalDateTime.now();
        if (event.getEventDate().isBefore(publishedTime.plusHours(2))) {
            log.error("not possible create event if event date  earlier current data time");
            throw new ConflictException("Not possible create event if event date  earlier current data time");
        }
        event.setCreatedOn(LocalDateTime.now());
        event.setState(Status.PENDING);
        User user = userService.getUserById(userid);
        event.setInitiator(user);
        event = repository.save(event);
        log.info("EventServiceImpl: createEvent — event was added {}.", event);
        return event;
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
            log.error("EventServiceImpl: updateEventByInitiator — not possible to change event");
            throw new ConflictException("Not possible to change event");
        }
        if (event.getEventDate().isBefore(publishedTime.plusHours(2))) {
            log.error("EventServiceImpl: updateEventByInitiator — not possible to change event if event date earlier current date and time");
            throw new ConflictException("Not possible to change event if event date earlier current date and time");
        }
        event = updateEvent(updEvent, updEvent.getId());
        log.info("EventServiceImpl: updateEventByInitiator — event was changed");
        return event;
    }

    @Override
    public Event cancelEventByInitiator(Long eventId, Long userId) {
        validateUserIdAndEventId(eventId, userId);
        Event event = getEventById(eventId);
        if (event.getState() != Status.PENDING) {
            log.error("EventServiceImpl: cancelEventByInitiator — not possible to cancel event");
            throw new ConflictException("Not possible to cancel event");
        }
        event.setState(Status.CANCELED);
        log.info("EventServiceImpl: cancelEventByInitiator — event was canceled");
        return updViewInEvent(event);
    }

    @Override
    public Collection<Event> getAllEventsByInitiatorId(Long userId, Pageable page) {
        userService.getUserById(userId);
        Collection<Event> events = repository.findByInitiatorId(userId, page).toList();
        log.info("EventServiceImpl: getAllEventsByInitiatorId — events was received");
        return updViewInEventList(events);
    }

    @Override
    public Event getEventByIdAndInitiatorId(Long eventId, Long userId) {
        validateUserIdAndEventId(eventId, userId);
        Event event = getEventById(eventId);
        log.info("EventServiceImpl: getEventByIdAndInitiatorId — received event by id");
        return updViewInEvent(event);
    }

    @Override
    public Event updateEventByAdmin(Event updEvent, Long eventId) {
        Event event = getEventById(eventId);

        Optional.ofNullable(updEvent.getRequestModeration()).ifPresent(event::setRequestModeration);
        Optional.ofNullable(updEvent.getLocation()).ifPresent(event::setLocation);
        event = updateEvent(updEvent, eventId);
        log.info("EventServiceImpl: updateEventByAdmin —  event was updated by admin");
        return event;
    }

    @Override
    public Event publishedEventByAdmin(Long eventId) {
        LocalDateTime publishedTime = LocalDateTime.now();
        Event event = getEventById(eventId);
        if (event.getEventDate().isBefore(publishedTime.plusHours(1)) ||
                event.getEventDate().isEqual(publishedTime.plusMinutes(59))) {
            log.error("EventServiceImpl: publishedEventByAdmin — not possible to publish event if event date earlier current date and time");
            throw new ConflictException("Not possible to publish event if event date earlier current date and time");
        }

        if (event.getState() != Status.PENDING) {
            log.error("EventServiceImpl: publishedEventByAdmin — not possible to publish event");
            throw new ConflictException("Not possible to publish event");
        }
        event.setState(Status.PUBLISHED);
        event.setPublishedOn(publishedTime);
        log.info("EventServiceImpl: publishedEventByAdmin — event was published");
        return updViewInEvent(event);
    }

    @Override
    public Event rejectedEventByAdmin(Long eventId) {
        Event event = getEventById(eventId);
        if (event.getState() == Status.PUBLISHED) {
            log.error("EventServiceImpl: rejectedEventByAdmin — not possible to cancel published event");
            throw new ConflictException("Not possible to cancel published event");
        }
        event.setState(Status.CANCELED);
        log.info("EventServiceImpl: rejectedEventByAdmin — event was canceled by admin");
        return updViewInEvent(event);
    }

    @Override
    public Event getEventById(Long eventId) {
        Optional<Event> eventOpt = repository.findById(eventId);
        Event event = eventOpt.orElseThrow(() -> {
            log.warn("EventServiceImpl: getEventById — event with id {} does not exist", eventId);
            return new ObjectNotFountException("Event with id " + eventId + " does not exist");
        });

        log.info("EventServiceImpl: getEventById — event with id {} was gotten", eventId);
        return updViewInEvent(event);
    }

    @Override
    public Collection<Event> getEventByIdIn(Collection<Long> eventId) {
        Collection<Event> events = repository.findByIdIn(eventId);
        log.info("EventServiceImpl: getEventByIdIn — received events by required ids");
        return events;
    }

    private void validateUserIdAndEventId(Long eventId, Long userId) {
        userService.getUserById(userId); // проверяем что существует пользователь с таким eventId
        Event event = getEventById(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            log.error("EventServiceImpl: validateUserIdAndEventId — user with id {} does not author of event with id {}.", userId, event.getId());
            throw new ConflictException(String.format("User with id %d does not author of event with id %d.",
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
        log.info("EventServiceImpl: getEventsByAdminParams — event with required params was gotten");
        return updViewInEventList(events);
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
        log.info("EventServiceImpl: getEventsByPublicParams — event with required params was gotten");
        return updViewInEventList(events);
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
                    log.warn("EventServiceImpl: makeSort — invalid sorting value entered");
                    throw new ArgumentNotValidException("Invalid sorting value entered");
            }
        }
        return repository.findAll(specification, pageable).stream().collect(Collectors.toList());
    }

    /**
     * вызов метода для получения статистики по просмотрам
     */
    private Collection<ViewStats> getStatisticView(Collection<String> uris) {
        LocalDateTime start = LocalDateTime.now().minusMonths(1L);
        LocalDateTime end = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String startString = start.format(formatter);
        String endString = end.format(formatter);
        Collection<ViewStats> viewStats = client.getStatistic(startString, endString, uris, true);
        log.info("views statistics was received");
        return viewStats;
    }

    private Collection<Event> updViewInEventList(Collection<Event> event) {
        Collection<String> uris = new ArrayList<>();
        event.forEach(e -> uris.add(URI + e.getId()));

        Collection<ViewStats> viewStats = getStatisticView(uris);
        if (!viewStats.isEmpty()) {
            for (ViewStats vs : viewStats) {
                Long idFromUri = Long.valueOf(vs.getUri().split("/")[2]);
                for (Event e : event) {
                    if (idFromUri.equals(e.getId())) {
                        e.setViews(vs.getHits());
                    }
                }
            }
        }
        log.info("EventServiceImpl: updViewInEventList — number of event views was received");
        return event;
    }

    private Event updViewInEvent(Event event) {
        Collection<String> uris = Collections.singleton(URI + event.getId());
        Collection<ViewStats> viewStats = getStatisticView(uris);

        if (!viewStats.isEmpty()) {
            viewStats.forEach(vs -> event.setViews(vs.getHits()));
        }
        log.info("EventServiceImpl: updViewInEvent — number of event views was received");
        return repository.save(event);
    }
}
