package ru.yandex.practicum.mainserver.request;

import ru.yandex.practicum.mainserver.event.model.Event;
import ru.yandex.practicum.mainserver.request.model.Request;
import ru.yandex.practicum.mainserver.status.Status;

import java.util.Collection;
import java.util.List;

/**
 * класс описывающий методы для работы с запросами на участие в событиях
 */

public interface RequestService {

    // добавление нового запроса
    Request createRequest(Long userID, Long eventId);

    // отмена своего запроса на участие
    Request cancelRequest(Long userId, Long requestId);

    // получение списка всех заявок на участие
    Collection<Request> getAllRequestsByUserId(Long userId);

    // получение заявки по id
    Request getRequestById(Long id);

    // получение всех заявок по id события
    Collection<Request> getRequestsByEventIdAndStatus(Long id, Status status);

    // обновление заявки
    Request updateStatusRequestById(Long id, Status status);

    // получение списка всех заявок на id события
    Collection<Request> getRequestsByEventId(Event event, Long userId);

    //подтверждение чужой заявки на участие в событии текущего пользователя
    Request confirmRequestForEvent(Event event, Long userId, Long requestId);

    //отклонение чужой заявки на участие в событии текущего пользователя
    Request rejectRequestForEvent(Event event, Long userId, Long requestId);

}
