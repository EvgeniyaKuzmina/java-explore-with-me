package ru.yandex.practicum.mainserver.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.mainserver.event.model.Event;
import ru.yandex.practicum.mainserver.request.model.Request;
import ru.yandex.practicum.mainserver.user.model.User;

/**
 * класс репозиторий для работы с БД событий
 */
public interface EventRepository extends JpaRepository<Event, Long> {

    // получение списка событие с пагинацией
    Page<Event> findByInitiatorId(Long userId, Pageable pageable);

    // получение события по id текущего пользователя
    Event findByInitiatorIdAndId(Long eventId, Long userId);

}
