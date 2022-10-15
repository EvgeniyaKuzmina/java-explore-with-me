package ru.yandex.practicum.mainserver.request;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.mainserver.request.model.Request;

import java.util.List;

/**
 * класс репозиторий для работы с БД запросов на участие в мероприятиях
 */

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Long> findEventIdByRequesterId(Long id);

    List<Request> findByRequesterId(Long id);

}
