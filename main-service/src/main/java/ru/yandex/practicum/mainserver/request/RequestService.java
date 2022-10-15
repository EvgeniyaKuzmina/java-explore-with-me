package ru.yandex.practicum.mainserver.request;

import ru.yandex.practicum.mainserver.request.model.Request;

import java.util.Collection;

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
}
