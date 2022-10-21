package ru.yandex.practicum.mainserver.event;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.mainserver.event.model.Event;
import ru.yandex.practicum.mainserver.event.model.EventParam;
import ru.yandex.practicum.mainserver.status.Status;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

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

    // получение события текущего пользователя по id
    Event getEventByIdAndInitiatorId(Long eventId, Long userId);

    // методы для API администратора

    // получение списка событий админом по указанным критериям
    Collection<Event> getAllEventByAdmin(List<Long> usersIds, List<Status> states, List<Long> categoriesId,
                                         LocalDateTime start, LocalDateTime end, Pageable page);

    //обновление события админом
    Event updateEventByAdmin(Event event, Long eventId);

    // публикация события
    Event publishedEventByAdmin(Long eventId);

    // отклонение события
    Event rejectedEventByAdmin(Long eventId);

    // методы для публичного API

    // получение события по id
    Event getEventById(Long eventId);

//    Collection<Event> getAllEvent(EventParam param, Pageable page);
    // получение списка событий с фильтрацией
/*    Collection<Event> getAllEvent(String text, List<Long> categoriesId, Boolean paid,
                                  Integer participantLimit, LocalDateTime start, LocalDateTime end, String sort,
                                  Pageable page);*/

    // обновление всех полей события
    Event updateEvent(Event updEvent);
    // получение списка событий публичным API
    Collection<Event> getEventsByPublicParams(EventParam param, Pageable pageable);

    // получение списка событий админом
    Collection<Event> getEventsByAdminParams(EventParam param, Pageable pageable);

}



