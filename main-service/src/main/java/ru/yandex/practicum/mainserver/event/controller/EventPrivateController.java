package ru.yandex.practicum.mainserver.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.mainserver.category.CategoryService;
import ru.yandex.practicum.mainserver.category.model.Category;
import ru.yandex.practicum.mainserver.event.EventService;
import ru.yandex.practicum.mainserver.event.dto.EventFullDto;
import ru.yandex.practicum.mainserver.event.dto.NewEventDto;
import ru.yandex.practicum.mainserver.event.dto.UpdateEventDto;
import ru.yandex.practicum.mainserver.event.mapper.EventMapper;
import ru.yandex.practicum.mainserver.event.model.Event;
import ru.yandex.practicum.mainserver.exception.ConflictException;
import ru.yandex.practicum.mainserver.request.RequestService;
import ru.yandex.practicum.mainserver.request.dto.RequestDto;
import ru.yandex.practicum.mainserver.request.mapper.RequestMapper;
import ru.yandex.practicum.mainserver.request.model.Request;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

/**
 * класс контроллер для работы с приватным API событий
 */

@RestController
@RequestMapping(path = "/users/{userId}/events")
@Slf4j
public class EventPrivateController {

    private static final String FROM = "0";
    private static final String SIZE = "10";
    private final EventService service;
    private final RequestService requestService;
    private final CategoryService categoryService;

    @Autowired
    public EventPrivateController(EventService service, RequestService requestService, CategoryService categoryService) {
        this.service = service;
        this.requestService = requestService;
        this.categoryService = categoryService;
    }

    // создание события
    @PostMapping
    public EventFullDto createEvent(@Valid @RequestBody NewEventDto eventDto, @PathVariable Long userId) {
        Category category = categoryService.getCategoryById(eventDto.getCategory());
        Event event = EventMapper.toEventFromNewDto(eventDto, category);
        LocalDateTime publishedTime = LocalDateTime.now();
        if (event.getEventDate().isBefore(publishedTime.plusHours(2))) {
            log.error("Нельзя опубликовать событие, дата начала которого ранее текущего времени");
            throw new ConflictException("Нельзя опубликовать событие, дата начала которого ранее текущего времени");
        }

        event = service.createEvent(event, userId);

        return EventMapper.toEventFullDto(event);
    }

    // изменение события
    @PatchMapping
    public EventFullDto updateEvent(@Valid @RequestBody UpdateEventDto eventDto, @PathVariable Long userId) {
        Category category = categoryService.getCategoryById(eventDto.getCategory());
        Event event = EventMapper.toEventFromUpdateDto(eventDto, category);
        event = service.updateEventByInitiator(event, userId);

        return EventMapper.toEventFullDto(event);
    }

    // получение событий, добавленных текущим пользователем
    @GetMapping
    public Collection<EventFullDto> getEventsByInitiator(@PathVariable Long userId, @RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
                                                         @RequestParam(defaultValue = SIZE) @Positive Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        Collection<Event> event = service.getAllEventsByInitiatorId(userId, pageable);
        Collection<EventFullDto> eventsDto = new ArrayList<>();
        event.forEach(e -> eventsDto.add(EventMapper.toEventFullDto(e)));
        return eventsDto;
    }

    // получение информации о событии по eventId, добавленным текущим пользователем
    @GetMapping(value = "/{eventId}")
    public EventFullDto getEventByIdAndInitiatorId(@PathVariable Long userId, @PathVariable Long eventId) {

        Event event = service.getEventByIdAndInitiatorId(eventId, userId);
        log.info(event.toString());
        return EventMapper.toEventFullDto(event);
    }

    // отмена события добавленного текущим пользователем
    @PatchMapping(value = "/{eventId}")
    public EventFullDto cancelEventByInitiator(@PathVariable Long userId, @PathVariable Long eventId) {
        Event event = service.cancelEventByInitiator(eventId, userId);

        return EventMapper.toEventFullDto(event);
    }

    // получение информации о запросах на участие в событии текущего пользователя
    @GetMapping(value = "/{eventId}/requests")
    public Collection<RequestDto> getERequestsByEventIdAndInitiatorId(@PathVariable Long userId, @PathVariable Long eventId) {
        Event event = service.getEventById(eventId);
        Collection<Request> requests = requestService.getRequestsByEventId(event, userId);
        Collection<RequestDto> requestsDto = new ArrayList<>();
        requests.forEach(r -> requestsDto.add(RequestMapper.toRequestDto(r)));
        return requestsDto;
    }

    // подтверждение чужой заявки на участие в событии текущего пользователя
    @PatchMapping(value = "/{eventId}/requests/{reqId}/confirm")
    public RequestDto confirmRequestToEventByInitiator(@PathVariable Long userId, @PathVariable Long eventId, @PathVariable Long reqId) {
        Event event = service.getEventById(eventId);
        Request request = requestService.confirmRequestForEvent(event, userId, reqId);

        return RequestMapper.toRequestDto(request);
    }

    // отклонение чужой заявки на участие в событии текущего пользователя
    @PatchMapping(value = "/{eventId}/requests/{reqId}/reject")
    public RequestDto rejectRequestToEventByInitiator(@PathVariable Long userId, @PathVariable Long eventId, @PathVariable Long reqId) {
        Event event = service.getEventById(eventId);
        Request request = requestService.rejectRequestForEvent(event, userId, reqId);

        return RequestMapper.toRequestDto(request);
    }
}
