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
import java.util.List;
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


    @Override
    public Event createEvent(Event event, Long userid) {
        event.setCreatedOn(LocalDateTime.now());
        User user = userService.getUserById(userid);
        event.setInitiator(user);
        try {
            log.info("Добавлено событие {}.", event);
            return repository.save(event);
        } catch (DataIntegrityViolationException e) {
            log.error("Произошла ошибка при сохранении данных");
            throw new RuntimeException("Произошла ошибка при сохранении данных");
        }
    }

    @Override
    public Event updateEventByInitiator(Event updEvent, Long userId) {
        validateUserIdAndEventId(updEvent.getId(), userId);
        Event event = getEventById(updEvent.getId());
        LocalDateTime publishedTime = LocalDateTime.now();
        if (event.getState().equals(Status.CANCELED)) {
            event.setState(Status.WAITING);
        }
        if (!event.getState().equals(Status.WAITING)) {
            log.error("Событие изменить нельзя");
            throw new ConflictException("Событие изменить нельзя");
        }
        if (event.getEventDate().isBefore(publishedTime.plusHours(2))) {
            log.error("Нельзя изменить событие, дата начала которого ранее текущего времени");
            throw new ConflictException("Нельзя опуизменить бликовать событие, дата начала которого ранее текущего времени");
        }

        return updateEvent(updEvent);

    }

    @Override
    public Event cancelEventByInitiator(Long eventId, Long userId) {
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
    public Collection<Event> getAllEventsByInitiatorId(Long userId, Pageable page) {
        userService.getUserById(userId); // проверяем что существует пользователь с таким id
        return repository.findByInitiatorId(userId, page).toList();
    }

    @Override
    public Event getEventByIdAndInitiatorId(Long eventId, Long userId) {
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
        Event event = getEventById(eventId);

        Optional.ofNullable(updEvent.getRequestModeration()).ifPresent(event::setRequestModeration);
        Optional.ofNullable(updEvent.getLocation()).ifPresent(event::setLocation);

        return updateEvent(event);
    }


    @Override
    public Event publishedEventByAdmin(Long eventId) {
        LocalDateTime publishedTime = LocalDateTime.now();
        Event event = getEventById(eventId);
        if (event.getEventDate().isBefore(publishedTime.plusHours(1)) ||
                event.getEventDate().isEqual(publishedTime.plusMinutes(59))) {
            log.error("Нельзя опубликовать событие, дата начала которого ранее текущего времени");
            throw new ConflictException("Нельзя опубликовать событие, дата начала которого ранее текущего времени");
        }

        if (!event.getState().equals(Status.WAITING) || event.getEventDate().plusHours(1).isAfter(LocalDateTime.now())) {
            log.error("Нельзя опубликовать событие");
            throw new ConflictException("Нельзя опубликовать событие");
        }
        event.setState(Status.PUBLISHED);
        event.setPublishedOn(publishedTime);
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
            log.error("Нельзя отменить опубликованное событие");
            throw new ConflictException("Нельзя отменить опубликованное событие");
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

  /*  @Override
    public Collection<Event> getAllEvent(EventParam param, Pageable page) {
        return repository.getAllEventsPublicAPI(param, page).toList();
    }*/
   /* @Override
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
    }*/

    private void validateUserIdAndEventId(Long eventId, Long userId) {
        userService.getUserById(userId); // проверяем что существует пользователь с таким id
        Event event = getEventById(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            log.error("Пользователь с id {} не является инициатором события {}.", userId, event.getId());
            throw new ConflictException(String.format("Пользователь с id %d не является инициатором события %d.",
                    userId, event.getId()));
        }
    }

    @Override
    public Event updateEvent(Event updEvent) {
        Event event = getEventById(updEvent.getId());
        Optional.ofNullable(updEvent.getTitle()).ifPresent(event::setTitle);
        Optional.ofNullable(updEvent.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(updEvent.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(updEvent.getEventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(updEvent.getCategory()).ifPresent(event::setCategory);
        Optional.ofNullable(updEvent.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(updEvent.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(updEvent.getConfirmedRequests()).ifPresent(event::setConfirmedRequests);

        try {
            log.info("Событие обновлено {}.", event);
            return repository.save(event);
        } catch (DataIntegrityViolationException e) {
            log.error("Произошла ошибка при сохранении данных");
            throw new RuntimeException("Произошла ошибка при сохранении данных");
        }

    }


    // получение списка событий админом
    @Override
    public Collection<Event> getEventsByAdminParams(EventParam param, Pageable pageable) {


        Specification<Event> specification = null;

        if (param.getUsersId() != null && !param.getUsersId().isEmpty()) {
            specification = Specification.where(specification).and(
                    (root, criteriaQuery, criteriaBuilder) ->
                            criteriaBuilder.in(root.get("initiator").get("id")).value(param.getCategoriesId())
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

    // получение списка событий публичным API
    @Override
    public Collection<Event> getEventsByPublicParams(EventParam param, Pageable pageable) {


        Specification<Event> specification = null;

        specification = Specification.where(specification).and(
                (root, criteriaQuery, criteriaBuilder) ->
                        criteriaBuilder.greaterThan(root.get("eventDate").as(LocalDateTime.class), param.getRangeStart())
        );

        if (param.getText() != null && !param.getText().isEmpty()) {
            specification = Specification.where(specification).and(
                    (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(root.get("description"), "%" + param.getText() + "%")
            );
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
            } else {
                specification = Specification.where(specification).and(
                        (root, criteriaQuery, criteriaBuilder) ->
                                criteriaBuilder.equal(root.get("confirmedRequest"), root.get("participantLimit"))
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

    private Collection<Event> makeSort(String sort, Specification<Event> specification, Pageable pageable) {
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

}
