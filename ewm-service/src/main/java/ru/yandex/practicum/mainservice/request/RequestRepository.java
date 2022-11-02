package ru.yandex.practicum.mainservice.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.mainservice.event.model.Event;
import ru.yandex.practicum.mainservice.request.model.Request;
import ru.yandex.practicum.mainservice.status.Status;

import java.util.List;

/**
 * класс репозиторий для работы с БД запросов на участие в мероприятиях
 */
public interface RequestRepository extends JpaRepository<Request, Long> {

    /**
     * получение всех заявок по id создателя
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

    /**
     * изменение статуса всех заявок с указанным статусом и указанным id event
     */
    @Modifying
    @Query("update Request r set r.status = ?1 where r.event = ?2 and r.status = ?3")
    @Transactional
    void updateStatusWhereEventIdAnsStatusPending(Status newStatus, Event event, Status oldStatus);
}
