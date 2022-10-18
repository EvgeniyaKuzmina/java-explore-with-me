package ru.yandex.practicum.mainserver.request;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.mainserver.request.model.Request;
import ru.yandex.practicum.mainserver.status.Status;

import java.util.List;

/**
 * класс репозиторий для работы с БД запросов на участие в мероприятиях
 */

public interface RequestRepository extends JpaRepository<Request, Long> {

    //получение id событий по id пользователя
    List<Long> findEventIdByRequesterId(Long id);

    // получение всех заявок по id пользователя
    List<Request> findByRequesterId(Long id);

    // получение всех заявок по id события с указанным статусом
    List<Request> findByEventIdAndStatus(Long id, Status status);

}
