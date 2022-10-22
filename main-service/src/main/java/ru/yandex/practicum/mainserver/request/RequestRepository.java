package ru.yandex.practicum.mainserver.request;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.mainserver.request.model.Request;
import ru.yandex.practicum.mainserver.status.Status;

import java.util.List;

/**
 * класс репозиторий для работы с БД запросов на участие в мероприятиях
 */

public interface RequestRepository extends JpaRepository<Request, Long> {

    //получение eventId событий по eventId пользователя
    List<Long> findEventIdByRequesterId(Long id);

    // получение всех заявок по eventId пользователя
    List<Request> findByRequesterId(Long id);

    // получение всех заявок по eventId события с указанным статусом
    List<Request> findByEventIdAndStatus(Long id, Status status);

    // получение всех заявок по eventId события
    List<Request> findByEventId(Long id);

}
