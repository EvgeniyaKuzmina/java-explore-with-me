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
            log.info("RequestServiceImpl: Запрос на участие добавлен: {}.", request);
            return repository.save(request);
        } catch (DataIntegrityViolationException e) {
            log.error("RequestServiceImpl: Внутренняя ошибка сервера.");
            throw new RuntimeException("Внутренняя ошибка сервера.");
        }

    }

    private void validateDate(List<Long> requests, Event event, Long userId, Long eventId) {
        if (requests.contains(eventId)) {
            log.error("RequestServiceImpl: validateDate — Нельзя добавить повторно запрос на участие в одном и том же событии");
            throw new ConflictException("Нельзя добавить повторно запрос на участие в одном и том же событии");
        }

        if (event.getInitiator().getId().equals(userId)) {
            log.error("RequestServiceImpl: validateDate — Инициатор события не может добавить запрос на участие в своём событии");
            throw new ConflictException("Инициатор события не может добавить запрос на участие в своём событии");
        }

        if (!event.getState().equals(Status.PUBLISHED)) {
            log.error("RequestServiceImpl: validateDate — Нельзя участвовать в неопубликованном событии");
            throw new ConflictException("Нельзя участвовать в неопубликованном событии");
        }

        if (event.getConfirmedRequest() != null && event.getConfirmedRequest().equals(event.getParticipantLimit())) {
            log.error("RequestServiceImpl: validateDate — Достигнут лимит запросов на участие ");
            throw new ConflictException("Достигнут лимит запросов на участие ");
        }
    }

    // отмена своего запроса на участие
    @Override
    public Request cancelRequest(Long userId, Long requestId) {
        userService.getUserById(userId);
        Request request = getRequestById(requestId);
        if (!request.getRequester().getId().equals(userId)) {
            log.error("RequestServiceImpl: cancelRequest — Пользователь с eventId {} не оставлял заявку на участие c eventId {}", userId, requestId);
            throw new ConflictException(String.format("Пользователь с eventId %d не оставлял заявку на участие c eventId %d", userId, requestId));
        }
        log.warn("RequestServiceImpl: cancelRequest —Пользователя с eventId {} удалил заявку на участие с eventId {}", userId, requestId);
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
    public Request getRequestById(Long eventId) {
        Optional<Request> request = repository.findById(eventId);
        request.orElseThrow(() -> {
            log.warn("RequestServiceImpl: getAllRequestsByUserId — Заявки с указанным eventId {} нет", eventId);
            return new ObjectNotFountException("Заявки с указанным eventId " + eventId + " нет");
        });

        log.warn("RequestServiceImpl: getAllRequestsByUserId — Заявка с указанным eventId {} получена", eventId);
        return request.get();
    }

    // получение списка заявок по id события и статусу события
    @Override
    public List<Request> getRequestsByEventIdAndStatus(Long eventId, Status status) {
        return repository.findByEventIdAndStatus(eventId, Status.CONFIRMED);
    }

    // получение списка заявок по id события
    @Override
    public List<Request> getRequestsByEventId(Event event, Long userId) {
        validateUserIdAndEventId(event, userId);
        return repository.findByEventId(event.getId());
    }

    //  обновление статуса события по id
    @Override
    public Request updateStatusRequestById(Long requestId, Status status) {
        Request request = getRequestById(requestId);
        request.setStatus(status);
        try {
            log.info("RequestServiceImpl: updateStatusRequestById — Статус обновлён {}.", request);
            return repository.save(request);
        } catch (DataIntegrityViolationException e) {
            log.error("RequestServiceImpl: updateStatusRequestById — Внутренняя ошибка сервера.");
            throw new RuntimeException("Внутренняя ошибка сервера.");
        }
    }

    // подтверждение заявки на событие
    @Override
    public Request confirmRequestForEvent(Event event, Long userId, Long requestId) {
        validateUserIdAndEventId(event, userId);
        getRequestById(requestId); // проверяем что заявка с указанным eventId существует
        if (event.getParticipantLimit() == 0 || event.getRequestModeration().equals(false)) {
            log.error("RequestServiceImpl: confirmRequestForEvent — Подтверждение заявки не требуется");
            return getRequestById(requestId);
        }

        // получаем количество подтвержденных заявок по событию
        Integer confirmedRequests = event.getConfirmedRequest();
        if (confirmedRequests == null) {
            confirmedRequests = 0;
        }

        if (event.getParticipantLimit().equals(confirmedRequests)) {
            log.info("RequestServiceImpl: confirmRequestForEvent — Все места на событие заняты, заявка отклонена ");
            return rejectRequestForEvent(event, userId, requestId);
        }

        Request request = updateStatusRequestById(requestId, Status.CONFIRMED);
        ++confirmedRequests;
        event.setConfirmedRequest(confirmedRequests);
        eventService.updateEvent(event, event.getId());

        // отменяем все заявки в статусе ожидания, если при подтверждении текущей заявки лимит заявок исчерпан
        if (event.getParticipantLimit().equals(confirmedRequests)) {
            List<Request> requests = getRequestsByEventIdAndStatus(event.getId(), Status.PENDING);
            for (Request r : requests) {
                rejectRequestForEvent(event, userId, r.getId());
            }
        }
        return request;
    }

    // отклонение заявки на событие
    @Override
    public Request rejectRequestForEvent(Event event, Long userId, Long requestId) {
        validateUserIdAndEventId(event, userId);
        getRequestById(requestId); // проверяем что заявка с указанным eventId существует
        return updateStatusRequestById(requestId, Status.REJECTED);
    }

    // проверка, что указанный пользователь userId является создателем события
    private void validateUserIdAndEventId(Event event, Long userId) {
        userService.getUserById(userId); // проверяем что существует пользователь с таким eventId
        if (!event.getInitiator().getId().equals(userId)) {
            log.error("RequestServiceImpl: validateUserIdAndEventId — Пользователь с id {} не является инициатором события {}.", userId, event.getId());
            throw new ConflictException(String.format("Пользователь с id %d не является инициатором события %d.",
                    userId, event.getId()));
        }
    }
}
