package ru.yandex.practicum.mainservice.request;

import ru.yandex.practicum.mainservice.event.model.Event;
import ru.yandex.practicum.mainservice.request.model.Request;
import ru.yandex.practicum.mainservice.status.Status;

import java.util.Collection;

/**
 * класс описывающий методы для работы с запросами на участие в событиях
 */
public interface RequestService {

    /**
     * добавление нового запроса
     */
    Request createRequest(Long userId, Long eventId);

    /**
     * отмена своего запроса на участие
     */
    Request cancelRequest(Long userId, Long requestId);

    /**
     * получение списка всех заявок на участие
     */
    Collection<Request> getAllRequestsByUserId(Long userId);

    /**
     * получение заявки на участие по id события
     */
    Request getRequestByUserIdAndEventId(Long userId, Long eventId);

    /**
     * получение заявки по id
     */
    Request getRequestById(Long id);

    /**
     * обновление статуса заявки
     */
    Request updateStatusRequestById(Long id, Status status);

    /**
     * получение списка всех заявок на участие в событии по id события
     */
    Collection<Request> getRequestsByEventId(Event event, Long userId);

    /**
     * подтверждение чужой заявки на участие в событии текущего пользователя
     */
    Request confirmRequestForEvent(Event event, Long userId, Long requestId);

    /**
     * отклонение чужой заявки на участие в событии текущего пользователя
     */
    Request rejectRequestForEvent(Event event, Long userId, Long requestId);
}
