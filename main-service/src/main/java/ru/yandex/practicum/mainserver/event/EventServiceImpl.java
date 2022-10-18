package ru.yandex.practicum.mainserver.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.mainserver.event.model.Event;
import ru.yandex.practicum.mainserver.exception.ConflictException;
import ru.yandex.practicum.mainserver.request.RequestService;
import ru.yandex.practicum.mainserver.request.model.Request;
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

    private final RequestService requestService;

    @Override
    public Event createEvent(Event event) {
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

        Optional.ofNullable(updEvent.getTitle()).ifPresent(event::setTitle);
        Optional.ofNullable(updEvent.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(updEvent.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(updEvent.getEventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(updEvent.getCategory()).ifPresent(event::setCategory);
        Optional.ofNullable(updEvent.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(updEvent.getParticipantLimit()).ifPresent(event::setParticipantLimit);

        try {
            log.info("Событие обновлено {}.", event);
            return repository.save(updEvent);
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
        return event;
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
    public Request confirmRequestForEvent(Long eventId, Long userId, Long requestId) {
        validateUserIdAndEventId(eventId, userId);
        Event event = getEventById(eventId);
        requestService.getRequestById(requestId); // проверяем что заявка с указанным id существует
        if (event.getParticipantLimit() == 0 || event.getRequestModeration().equals(false)) {
            log.error("Подтверждение заявки не требуется");
            return requestService.getRequestById(requestId);
        }

        // получаем количество подтвержденных заявок по событию
        int countRequestsToEvent = requestService.getRequestsByEventIdAndStatus(eventId, Status.CONFIRMED).size();
        if (event.getParticipantLimit().equals(countRequestsToEvent)) {
            return rejectRequestForEvent(eventId, userId, requestId);
        }
        Request request = requestService.updateStatusRequestById(requestId, Status.CONFIRMED);

        // отменяем все заявки в статусе ожидания, если при подтверждении текущей заявки лимит заявок исчерпан
        if (event.getParticipantLimit().equals(countRequestsToEvent + 1)) {
            List<Request> requests = requestService.getRequestsByEventIdAndStatus(eventId, Status.PENDING);
           for (Request r : requests) {
               rejectRequestForEvent(eventId, userId, r.getId());
           }

        }
        return request;
    }

    @Override
    public Request rejectRequestForEvent(Long eventId, Long userId, Long requestId) {
        validateUserIdAndEventId(eventId, userId);
        requestService.getRequestById(requestId); // проверяем что заявка с указанным id существует
        return requestService.updateStatusRequestById(requestId, Status.REJECTED);
    }

    @Override
    public Collection<Request> getRequestsByUserId(Long eventId, Long userId) {
        return null;
    }

    @Override
    public Collection<Event> getAllEventByAdmin(List<Long> usersIds, List<String> states, List<Long> categoriesId, LocalDateTime start, LocalDateTime end, Pageable page) {
        return null;
    }

    @Override
    public Event updateEventByAdmin(Event event, Long eventId) {
        return null;
    }

    @Override
    public Event publishedEventByAdmin(Long eventId) {
        return null;
    }

    @Override
    public Event rejectedEventByAdmin(Long eventId) {
        return null;
    }

    @Override
    public Event getEventById(Long eventId) {
        return null;
    }

    @Override
    public Collection<Event> getAllEvent(String text, List<Long> categoriesId, Boolean paid, LocalDateTime start, LocalDateTime end, String sort, Boolean onlyAvailable, Pageable page) {
        return null;
    }

    private void validateUserIdAndEventId(Long eventId, Long userId) {
        userService.getUserById(userId); // проверяем что существует пользователь с таким id
        Event event = getEventById(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            log.error("Пользователь с id {} не является инициатором события {}.", userId, eventId);
            throw new ConflictException(String.format("Пользователь с id %d не является инициатором события %d.",
                    userId, eventId));
        }
    }
}
