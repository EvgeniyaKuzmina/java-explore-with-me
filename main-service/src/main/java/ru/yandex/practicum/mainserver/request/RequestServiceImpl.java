package ru.yandex.practicum.mainserver.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.mainserver.event.EventService;
import ru.yandex.practicum.mainserver.event.model.Event;
import ru.yandex.practicum.mainserver.exception.ConflictException;
import ru.yandex.practicum.mainserver.exception.ObjectNotFountException;
import ru.yandex.practicum.mainserver.request.model.Request;
import ru.yandex.practicum.mainserver.status.Status;
import ru.yandex.practicum.mainserver.user.UserService;
import ru.yandex.practicum.mainserver.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * класс реализующий методы для работы с заявками на участие
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository repository;
    private final UserService userService;
    private final EventService eventService;

    @Override
    public Request createRequest(Long userId, Long eventId) {
        List<Long> requests = repository.findEventIdByRequesterId(userId);
        Event event = eventService.getEventById(eventId);
        User user = userService.getUserById(userId);

        validateDate(requests, event, userId, eventId);

        Request request = Request.builder()
                .created(LocalDateTime.now())
                .requester(user)
                .event(event)
                .build();
        if (event.getRequestModeration().equals(false)) {
            request.setStatus(Status.CONFIRMED);
        } else {
            request.setStatus(Status.PENDING);
        }
        try {
            log.info("Запрос на участие добавлен: {}.", request);
            return repository.save(request);
        } catch (DataIntegrityViolationException e) {
            log.error("Внутренняя ошибка сервера.");
            throw new RuntimeException("Внутренняя ошибка сервера.");
        }

    }

    private void validateDate(List<Long> requests, Event event, Long userId, Long eventId) {
        if (requests.contains(eventId)) {
            log.error("Нельзя добавить повторно запрос на участие в одном и том же событии");
            throw new ConflictException("Нельзя добавить повторно запрос на участие в одном и том же событии");
        }

        if (event.getInitiator().getId().equals(userId)) {
            log.error("Инициатор события не может добавить запрос на участие в своём событии");
            throw new ConflictException("Инициатор события не может добавить запрос на участие в своём событии");
        }

        if (!event.getState().equals(Status.PUBLISHED)) {
            log.error("Нельзя участвовать в неопубликованном событии");
            throw new ConflictException("Нельзя участвовать в неопубликованном событии");
        }

        if (event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            log.error("Достигнут лимит запросов на участие ");
            throw new ConflictException("Достигнут лимит запросов на участие ");
        }
    }

    // отмена своего запроса на участие
    @Override
    public Request cancelRequest(Long userId, Long requestId) {
        userService.getUserById(userId);
        Request request = getRequestById(requestId);
        if (!request.getRequester().getId().equals(userId)) {
            log.error("Пользователь с id {} не оставлял заявку на участие c id {}", userId, requestId);
            throw new ConflictException(String.format("Пользователь с id %d не оставлял заявку на участие c id %d", userId, requestId));
        }
        log.warn("Пользователя с id {} удалил заявку на участие с id {}", userId, requestId);
        request.setStatus(Status.CANCELED);
        return request;

    }

    // получение списка всех заявок на участие по id пользователя
    @Override
    public Collection<Request> getAllRequestsByUserId(Long userId) {
        return repository.findByRequesterId(userId);
    }

    // получение заявки по Id
    @Override
    public Request getRequestById(Long id) {
        Optional<Request> request = repository.findById(id);
        request.orElseThrow(() -> {
            log.warn("Заявки с указанным id {} нет", id);
            return new ObjectNotFountException("Заявки с указанным id " + id + " нет");
        });

        log.warn("Заявка с указанным id {} получена", id);
        return request.get();
    }

    @Override
    public List<Request> getRequestsByEventIdAndStatus(Long eventId, Status status) {
        return repository.findByEventIdAndStatus(eventId, Status.CONFIRMED);
    }

    @Override
    public List<Request> getRequestsByEventId(Event event, Long userId) {
        validateUserIdAndEventId(event, userId);
        return repository.findByEventId(event.getId());
    }

    @Override
    public Request updateStatusRequestById(Long requestId, Status status) {
        Request request = getRequestById(requestId);
        request.setStatus(status);
        try {
            log.info("Статус обновлён {}.", request);
            return repository.save(request);
        } catch (DataIntegrityViolationException e) {
            log.error("Внутренняя ошибка сервера.");
            throw new RuntimeException("Внутренняя ошибка сервера.");
        }
    }

    @Override
    public Request confirmRequestForEvent(Event event, Long userId, Long requestId) {
        validateUserIdAndEventId(event, userId);
        getRequestById(requestId); // проверяем что заявка с указанным id существует
        if (event.getParticipantLimit() == 0 || event.getRequestModeration().equals(false)) {
            log.error("Подтверждение заявки не требуется");
            return getRequestById(requestId);
        }

        // получаем количество подтвержденных заявок по событию
        Integer confirmedRequests = event.getConfirmedRequests();
        if (confirmedRequests == null) {
            confirmedRequests = 0;
        }

        if (event.getParticipantLimit().equals(confirmedRequests)) {
            log.info("Все места на событие заняты, заявка отклонена ");
            return rejectRequestForEvent(event, userId, requestId);
        }

        Request request = updateStatusRequestById(requestId, Status.CONFIRMED);
        ++confirmedRequests;
        event.setConfirmedRequests(confirmedRequests);
        eventService.updateEvent(event);

        // отменяем все заявки в статусе ожидания, если при подтверждении текущей заявки лимит заявок исчерпан
        if (event.getParticipantLimit().equals(confirmedRequests)) {
            List<Request> requests = getRequestsByEventIdAndStatus(event.getId(), Status.PENDING);
            for (Request r : requests) {
                rejectRequestForEvent(event, userId, r.getId());
            }
        }
        return request;
    }

    @Override
    public Request rejectRequestForEvent(Event event, Long userId, Long requestId) {
        validateUserIdAndEventId(event, userId);
        getRequestById(requestId); // проверяем что заявка с указанным id существует
        return updateStatusRequestById(requestId, Status.REJECTED);
    }

   /* @Override
    public Collection<Request> getRequestsByEventId(Event event, Long userId) {
        validateUserIdAndEventId(event, userId);
        return getRequestsByEventId(event.getId());
    }*/

    private void validateUserIdAndEventId(Event event, Long userId) {
        userService.getUserById(userId); // проверяем что существует пользователь с таким id
        if (!event.getInitiator().getId().equals(userId)) {
            log.error("Пользователь с id {} не является инициатором события {}.", userId, event.getId());
            throw new ConflictException(String.format("Пользователь с id %d не является инициатором события %d.",
                    userId, event.getId()));
        }
    }
}
