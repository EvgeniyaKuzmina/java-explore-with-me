package ru.yandex.practicum.mainserver.request;

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
    List<Request> getRequestsByEventIdAndStatus(Long id, Status status);

    // обновление заявки
    Request updateStatusRequestById(Long id, Status status);
}
