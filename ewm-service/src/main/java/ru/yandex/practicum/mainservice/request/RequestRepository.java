package ru.yandex.practicum.mainservice.request;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.mainservice.request.model.Request;
import ru.yandex.practicum.mainservice.status.Status;

import java.util.List;

/**
 * класс репозиторий для работы с БД запросов на участие в мероприятиях
 */
public interface RequestRepository extends JpaRepository<Request, Long> {

    /**
     * получение id событий по id создателя запроса на участие
     */
    List<Long> findEventIdByRequesterId(Long id);

    /**
     * получение всех заявок по id пользователя
     */
    List<Request> findByRequesterId(Long id);

    /**
     * получение всех заявок по id события с указанным статусом
     */
    List<Request> findByEventIdAndStatus(Long id, Status status);

    /**
     * получение всех заявок по id события
     */
    List<Request> findByEventId(Long id);
}
