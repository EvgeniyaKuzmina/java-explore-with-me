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
        Collection<Request> requests = repository.findByRequesterId(userId);
        Collection<Long> ids = new ArrayList<>();
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
        request = repository.save(request);
        log.info("RequestServiceImpl: createRequest — request to participation was added {}.", request);
        return request;
    }

    private void validateDate(Collection<Long> requests, Event event, Long userId, Long eventId) {
        if (requests.contains(eventId)) {
            log.error("RequestServiceImpl: validateDate — not possible to add again request to participation in the same event");
            throw new ConflictException("Not possible to add again request to participation in the same event");
        }

        if (event.getInitiator().getId().equals(userId)) {
            log.error("RequestServiceImpl: validateDate — author of event can not add request to participation in own event");
            throw new ConflictException("Author of event can not add request to participation in own event");
        }

        if (event.getState() != Status.PUBLISHED) {
            log.error("RequestServiceImpl: validateDate — not possible take part in not published event");
            throw new ConflictException("Not possible take part in not published event");
        }

        if (event.getConfirmedRequest() != null && event.getConfirmedRequest().equals(event.getParticipantLimit())) {
            log.error("RequestServiceImpl: validateDate — limit of request to participation has been reached");
            throw new ConflictException("Limit of request to participation has been reached");
        }
    }

    @Override
    public Request cancelRequest(Long userId, Long requestId) {
        userService.getUserById(userId);
        Request request = getRequestById(requestId);
        if (!request.getRequester().getId().equals(userId)) {
            log.error("RequestServiceImpl: cancelRequest — user with id {} did not leave request to participate in event with id {}",
                    userId, requestId);
            throw new ConflictException(String.format("User with id %d did not leave request to participate in event with id %d",
                    userId, requestId));
        }

        request.setStatus(Status.CANCELED);
        request = repository.save(request);
        log.info("RequestServiceImpl: cancelRequest — user with id {} deleted request to participate in event with id {}",
                userId, requestId);
        return request;
    }

    @Override
    public Collection<Request> getAllRequestsByUserId(Long userId) {
        Collection<Request> requests = repository.findByRequesterId(userId);
        log.info("RequestServiceImpl: getAllRequestsByUserId — requests to participate by author id was received");
        return requests;
    }

    @Override
    public Request getRequestByUserIdAndEventId(Long userId, Long eventId) {
        Request request = repository.findByRequesterIdAndEventId(userId, eventId);
        log.info("RequestServiceImpl: getRequestByUserIdAndEventId — request to participate by author id and event id was received");
        return request;
    }

    @Override
    public Request getRequestById(Long requestId) {
        Optional<Request> requestOpt = repository.findById(requestId);
        Request request = requestOpt.orElseThrow(() -> {
            log.warn("RequestServiceImpl: getRequestById — request to participate with id {} not exist", requestId);
            return new ObjectNotFountException("Request to participate with id " + requestId + " not exist");
        });

        log.info("RequestServiceImpl: getRequestById — request to participate by id {} was received", requestId);
        return request;
    }

    @Override
    public Collection<Request> getRequestsByEventId(Event event, Long userId) {
        validateUserIdAndEventId(event, userId);
        Collection<Request> requests = repository.findByEventId(event.getId());
        log.info("RequestServiceImpl: getRequestsByEventId — requests to participate by event id was received");
        return requests;
    }

    @Override
    public Request updateStatusRequestById(Long requestId, Status status) {
        Request request = getRequestById(requestId);
        request.setStatus(status);
        request = repository.save(request);
        log.info("RequestServiceImpl: updateStatusRequestById —status was updated {}.", request);
        return request;
    }

    @Override
    public Request confirmRequestForEvent(Event event, Long userId, Long requestId) {
        validateUserIdAndEventId(event, userId);
        getRequestById(requestId);
        if (event.getParticipantLimit() == 0 || event.getRequestModeration().equals(Boolean.FALSE)) {
            log.error("RequestServiceImpl: confirmRequestForEvent — confirmation of request to participate is not required");
            return getRequestById(requestId);
        }

        Integer confirmedRequests = event.getConfirmedRequest();
        if (confirmedRequests == null) {
            confirmedRequests = 0;
        }

        if (event.getParticipantLimit().equals(confirmedRequests)) {
            log.info("RequestServiceImpl: confirmRequestForEvent — all seats for the event are occupied, the request to participate is rejected");
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

        log.info("RequestServiceImpl: confirmRequestForEvent — request to participate was confirmed");
        return request;
    }

    @Override
    public Request rejectRequestForEvent(Event event, Long userId, Long requestId) {
        validateUserIdAndEventId(event, userId);
        getRequestById(requestId); // проверяем что заявка с указанным eventId существует
        log.info("RequestServiceImpl: rejectRequestForEvent — request to participate was rejected");
        return updateStatusRequestById(requestId, Status.REJECTED);
    }

    /**
     * проверка, что указанный пользователь userId является создателем события
     */
    private void validateUserIdAndEventId(Event event, Long userId) {
        userService.getUserById(userId); // проверяем что существует пользователь с таким eventId
        if (!event.getInitiator().getId().equals(userId)) {
            log.error("RequestServiceImpl: validateUserIdAndEventId — user with id {} is not author of event with id {}.", userId, event.getId());
            throw new ConflictException(String.format("User with id id %d is not author of event with id  %d.",
                    userId, event.getId()));
        }
    }
}
