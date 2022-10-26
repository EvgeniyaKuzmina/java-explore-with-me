package ru.yandex.practicum.mainservice.event;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.mainservice.event.model.Event;
import ru.yandex.practicum.mainservice.event.model.EventParam;

import java.util.Collection;

/**
 * класс описывающий методы для работы с событиями
 */

public interface EventService {

    // методы для приватного API

    // добавление нового события
    Event createEvent(Event event, Long userid);

    //обновление события пользователем (создателем) события
    Event updateEventByInitiator(Event event, Long userId);

    // отмена события создателем события
    Event cancelEventByInitiator(Long eventId, Long userId);

    // получение списка всех событий текущего пользователя
    Collection<Event> getAllEventsByInitiatorId(Long userId, Pageable page);

    // получение события текущего пользователя по eventId
    Event getEventByIdAndInitiatorId(Long eventId, Long userId);

    // методы для API администратора

    //обновление события админом
    Event updateEventByAdmin(Event event, Long eventId);

    // публикация события
    Event publishedEventByAdmin(Long eventId);

    // отклонение события
    Event rejectedEventByAdmin(Long eventId);

    // методы для публичного API

    // получение события по eventId
    Event getEventById(Long eventId);

    // обновление всех полей события
    Event updateEvent(Event updEvent, Long eventId);

    // получение списка событий публичным API
    Collection<Event> getEventsByPublicParams(EventParam param, Pageable pageable);

    // получение списка событий админом
    Collection<Event> getEventsByAdminParams(EventParam param, Pageable pageable);

}



