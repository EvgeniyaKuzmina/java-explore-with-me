package ru.yandex.practicum.mainservice.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.mainservice.category.CategoryService;
import ru.yandex.practicum.mainservice.category.model.Category;
import ru.yandex.practicum.mainservice.event.EventService;
import ru.yandex.practicum.mainservice.event.dto.EventFullDto;
import ru.yandex.practicum.mainservice.event.dto.NewEventDto;
import ru.yandex.practicum.mainservice.event.dto.UpdateEventDto;
import ru.yandex.practicum.mainservice.event.mapper.EventMapper;
import ru.yandex.practicum.mainservice.event.model.Event;
import ru.yandex.practicum.mainservice.exception.ConflictException;
import ru.yandex.practicum.mainservice.request.RequestService;
import ru.yandex.practicum.mainservice.request.dto.RequestDto;
import ru.yandex.practicum.mainservice.request.mapper.RequestMapper;
import ru.yandex.practicum.mainservice.request.model.Request;

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

    @PostMapping
    public EventFullDto createEvent(@Valid @RequestBody NewEventDto eventDto, @PathVariable Long userId) {
        log.info("EventPrivateController: createEvent — получен запрос на создание события");
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

    @PatchMapping
    public EventFullDto updateEvent(@Valid @RequestBody UpdateEventDto eventDto, @PathVariable Long userId) {
        log.info("EventPrivateController: updateEvent — получен запрос на обновление события");
        Category category = categoryService.getCategoryById(eventDto.getCategory());
        Event event = EventMapper.toEventFromUpdateDto(eventDto, category);
        event = service.updateEventByInitiator(event, userId);

        return EventMapper.toEventFullDto(event);
    }

    @GetMapping
    public Collection<EventFullDto> getEventsByInitiator(@PathVariable Long userId, @RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
                                                         @RequestParam(defaultValue = SIZE) @Positive Integer size) {
        log.info("EventPrivateController: getEventsByInitiator — получен запрос от инициатора на списка событий");
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        Collection<Event> event = service.getAllEventsByInitiatorId(userId, pageable);
        Collection<EventFullDto> eventsDto = new ArrayList<>();
        event.forEach(e -> eventsDto.add(EventMapper.toEventFullDto(e)));

        return eventsDto;
    }

    @GetMapping(value = "/{eventId}")
    public EventFullDto getEventByIdAndInitiatorId(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("EventPrivateController: getEventByIdAndInitiatorId — получен запрос от инициатора на получение события по id");
        Event event = service.getEventByIdAndInitiatorId(eventId, userId);

        return EventMapper.toEventFullDto(event);
    }

    @PatchMapping(value = "/{eventId}")
    public EventFullDto cancelEventByInitiator(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("EventPrivateController: cancelEventByInitiator — получен запрос на отмену события");
        Event event = service.cancelEventByInitiator(eventId, userId);

        return EventMapper.toEventFullDto(event);
    }

    @GetMapping(value = "/{eventId}/requests")
    public Collection<RequestDto> getRequestsByEventIdAndInitiatorId(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("EventPrivateController: getRequestsByEventIdAndInitiatorId — " +
                "получен запрос на получение информации о запросах на участие в событии текущего пользователя");
        Event event = service.getEventById(eventId);
        Collection<Request> requests = requestService.getRequestsByEventId(event, userId);
        Collection<RequestDto> requestsDto = new ArrayList<>();
        requests.forEach(r -> requestsDto.add(RequestMapper.toRequestDto(r)));

        return requestsDto;
    }

    @PatchMapping(value = "/{eventId}/requests/{reqId}/confirm")
    public RequestDto confirmRequestToEventByInitiator(@PathVariable Long userId, @PathVariable Long eventId, @PathVariable Long reqId) {
        log.info("EventPrivateController: confirmRequestToEventByInitiator — " +
                "получен запрос на подтверждение чужой заявки на участие в событии текущего пользователя");
        Event event = service.getEventById(eventId);
        Request request = requestService.confirmRequestForEvent(event, userId, reqId);

        return RequestMapper.toRequestDto(request);
    }

    @PatchMapping(value = "/{eventId}/requests/{reqId}/reject")
    public RequestDto rejectRequestToEventByInitiator(@PathVariable Long userId, @PathVariable Long eventId, @PathVariable Long reqId) {
        log.info("EventPrivateController: rejectRequestToEventByInitiator — " +
                "получен запрос на отклонение чужой заявки на участие в событии текущего пользователя");
        Event event = service.getEventById(eventId);
        Request request = requestService.rejectRequestForEvent(event, userId, reqId);

        return RequestMapper.toRequestDto(request);
    }
}
