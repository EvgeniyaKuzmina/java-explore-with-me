package ru.yandex.practicum.mainservice.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.mainservice.event.EventService;
import ru.yandex.practicum.mainservice.event.model.Event;
import ru.yandex.practicum.mainservice.exception.ConflictException;
import ru.yandex.practicum.mainservice.exception.ObjectNotFountException;
import ru.yandex.practicum.mainservice.request.model.Request;
import ru.yandex.practicum.mainservice.status.Status;
import ru.yandex.practicum.mainservice.user.UserService;
import ru.yandex.practicum.mainservice.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        Event event = eventService.getEventById(eventId);
        User user = userService.getUserById(userId);
        List<Request> requests = repository.findByRequesterId(userId);
        List<Long> ids = new ArrayList<>();
        requests.forEach(r -> ids.add(r.getEvent().getId()));

        validateDate(ids, event, userId, eventId);

        Request request = Request.builder()
                .created(LocalDateTime.now())
                .requester(user)
                .event(event)
                .build();
        if (event.getRequestModeration().equals(Boolean.FALSE)) {
            request.setStatus(Status.CONFIRMED);
        } else {
            request.setStatus(Status.PENDING);
        }

        log.info("RequestServiceImpl: createRequest — Запрос на участие добавлен: {}.", request);
        return repository.save(request);
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

        if (event.getState() != Status.PUBLISHED) {
            log.error("RequestServiceImpl: validateDate — Нельзя участвовать в неопубликованном событии");
            throw new ConflictException("Нельзя участвовать в неопубликованном событии");
        }

        if (event.getConfirmedRequest() != null && event.getConfirmedRequest().equals(event.getParticipantLimit())) {
            log.error("RequestServiceImpl: validateDate — Достигнут лимит запросов на участие ");
            throw new ConflictException("Достигнут лимит запросов на участие ");
        }
    }

    @Override
    public Request cancelRequest(Long userId, Long requestId) {
        userService.getUserById(userId);
        Request request = getRequestById(requestId);
        if (!request.getRequester().getId().equals(userId)) {
            log.error("RequestServiceImpl: cancelRequest — Пользователь с id {} не оставлял заявку на участие c id {}", userId, requestId);
            throw new ConflictException(String.format("Пользователь с id %d не оставлял заявку на участие c id %d", userId, requestId));
        }

        request.setStatus(Status.CANCELED);
        log.info("RequestServiceImpl: cancelRequest —Пользователя с id {} удалил заявку на участие с id {}", userId, requestId);
        return request;
    }

    @Override
    public Collection<Request> getAllRequestsByUserId(Long userId) {
        Collection<Request> requests = repository.findByRequesterId(userId);
        log.info("RequestServiceImpl: getAllRequestsByUserId — заявки по id создателя заявки получены");
        return requests;
    }

    @Override
    public Request getRequestById(Long requestId) {
        Optional<Request> requestOpt = repository.findById(requestId);
        Request request = requestOpt.orElseThrow(() -> {
            log.warn("RequestServiceImpl: getRequestById — Заявки с указанным id {} нет", requestId);
            return new ObjectNotFountException("Заявки с указанным id " + requestId + " нет");
        });

        log.info("RequestServiceImpl: getRequestById — Заявка с указанным id {} получена", requestId);
        return request;
    }

    @Override
    public List<Request> getRequestsByEventIdAndStatus(Long eventId, Status status) {
        List<Request> requests = repository.findByEventIdAndStatus(eventId, Status.CONFIRMED);
        log.info("RequestServiceImpl: getRequestsByEventIdAndStatus — заявки по id события и статусу события получены");
        return requests;
    }

    @Override
    public List<Request> getRequestsByEventId(Event event, Long userId) {
        validateUserIdAndEventId(event, userId);
        List<Request> requests = repository.findByEventId(event.getId());
        log.info("RequestServiceImpl: getRequestsByEventId — заявки по id события получены");
        return requests;
    }

    @Override
    public Request updateStatusRequestById(Long requestId, Status status) {
        Request request = getRequestById(requestId);
        request.setStatus(status);
        log.info("RequestServiceImpl: updateStatusRequestById — Статус обновлён {}.", request);
        return repository.save(request);
    }

    @Override
    public Request confirmRequestForEvent(Event event, Long userId, Long requestId) {
        validateUserIdAndEventId(event, userId);
        getRequestById(requestId);
        if (event.getParticipantLimit() == 0 || event.getRequestModeration().equals(Boolean.FALSE)) {
            log.error("RequestServiceImpl: confirmRequestForEvent — Подтверждение заявки не требуется");
            return getRequestById(requestId);
        }

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
        event = eventService.updateEvent(event, event.getId());

        // отменяем все заявки в статусе ожидания, если при подтверждении текущей заявки лимит заявок исчерпан
        if (event.getParticipantLimit().equals(confirmedRequests)) {
            repository.updateStatusWhereEventIdAnsStatusPending(Status.REJECTED, event, Status.PENDING);
        }

        log.info("RequestServiceImpl: confirmRequestForEvent — заявки на событие подтверждена");
        return request;
    }

    @Override
    public Request rejectRequestForEvent(Event event, Long userId, Long requestId) {
        validateUserIdAndEventId(event, userId);
        getRequestById(requestId); // проверяем что заявка с указанным eventId существует
        log.info("RequestServiceImpl: rejectRequestForEvent — заявка на событие отклонена");
        return updateStatusRequestById(requestId, Status.REJECTED);
    }

    /**
     * проверка, что указанный пользователь userId является создателем события
     */
    private void validateUserIdAndEventId(Event event, Long userId) {
        userService.getUserById(userId); // проверяем что существует пользователь с таким eventId
        if (!event.getInitiator().getId().equals(userId)) {
            log.error("RequestServiceImpl: validateUserIdAndEventId — Пользователь с id {} не является инициатором события {}.", userId, event.getId());
            throw new ConflictException(String.format("Пользователь с id %d не является инициатором события %d.",
                    userId, event.getId()));
        }
    }
}
