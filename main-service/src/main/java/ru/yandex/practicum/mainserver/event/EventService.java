package ru.yandex.practicum.mainserver.event;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.mainserver.event.model.Event;
import ru.yandex.practicum.mainserver.request.model.Request;
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
    Event createEvent(Event event);

    //обновление события
    Event updateEvent(Event event, Long userId);

    // отмена события
    Event cancelEvent(Long eventId, Long userId);

    // получение списка всех событий текущего пользователя
    Collection<Event> getAllEventByUserId(Long userId, Pageable page);

    // получение события текущего пользователя по id
    Event getEventUserById(Long eventId, Long userId);


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

    // получение списка событий с фильтрацией
    Collection<Event> getAllEvent(String text, List<Long> categoriesId, Boolean paid,
                                  Integer participantLimit, LocalDateTime start, LocalDateTime end, String sort,
                                  Pageable page);

}



