package ru.yandex.practicum.mainservice.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.mainservice.category.CategoryService;
import ru.yandex.practicum.mainservice.category.model.Category;
import ru.yandex.practicum.mainservice.event.EventService;
import ru.yandex.practicum.mainservice.event.comment.CommentService;
import ru.yandex.practicum.mainservice.event.comment.model.Comment;
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
import java.util.List;

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
    private final CommentService commentService;

    @Autowired
    public EventPrivateController(EventService service, RequestService requestService, CategoryService categoryService, CommentService commentService) {
        this.service = service;
        this.requestService = requestService;
        this.categoryService = categoryService;
        this.commentService = commentService;
    }

    @PostMapping
    public EventFullDto createEvent(@Valid @RequestBody NewEventDto eventDto, @PathVariable Long userId) {
        log.info("EventPrivateController: createEvent — Received request to create event");
        Category category = categoryService.getCategoryById(eventDto.getCategory());
        Event event = EventMapper.toEventFromNewDto(eventDto, category);
        event = service.createEvent(event, userId);
        Collection<Comment> comments = List.of();
        return EventMapper.toEventFullDto(event, comments);
    }

    @PatchMapping
    public EventFullDto updateEvent(@Valid @RequestBody UpdateEventDto eventDto, @PathVariable Long userId) {
        log.info("EventPrivateController: updateEvent — Received request to update event");
        Category category = categoryService.getCategoryById(eventDto.getCategory());
        Event event = EventMapper.toEventFromUpdateDto(eventDto, category);
        event = service.updateEventByInitiator(event, userId);
        Collection<Comment> comments = commentService.getPublishedByEventId(event.getId());
        return EventMapper.toEventFullDto(event, comments);
    }

    @GetMapping
    public Collection<EventFullDto> getEventsByInitiator(@PathVariable Long userId, @RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
                                                         @RequestParam(defaultValue = SIZE) @Positive Integer size) {
        log.info("EventPrivateController: getEventsByInitiator — Received request from author to get list of events");
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        Collection<Event> events = service.getAllEventsByInitiatorId(userId, pageable);
        Collection<EventFullDto> eventsDto = new ArrayList<>();
        Collection<Long> eventsId = new ArrayList<>();
        events.forEach(e -> eventsId.add(e.getId()));

        Collection<Comment> comments = commentService.getPublishedByListEventId(eventsId);
        Collection<Comment> commentsByEventId = new ArrayList<>();
        for (Event e : events) {
            if (!comments.isEmpty()) {
                for (Comment c : comments) {
                    if (e.getId().equals(c.getEvent().getId())) {
                        commentsByEventId.add(c);
                    }
                }
            }
            eventsDto.add(EventMapper.toEventFullDto(e, commentsByEventId));
            commentsByEventId.clear();
        }
        return eventsDto;
    }

    @GetMapping(value = "/{eventId}")
    public EventFullDto getEventByIdAndInitiatorId(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("EventPrivateController: getEventByIdAndInitiatorId — Received request from author to get event by id");
        Event event = service.getEventByIdAndInitiatorId(eventId, userId);
        Collection<Comment> comments = commentService.getPublishedByEventId(event.getId());
        return EventMapper.toEventFullDto(event, comments);
    }

    @PatchMapping(value = "/{eventId}")
    public EventFullDto cancelEventByInitiator(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("EventPrivateController: cancelEventByInitiator — Received request to cancel event");
        Event event = service.cancelEventByInitiator(eventId, userId);
        Collection<Comment> comments = commentService.getPublishedByEventId(event.getId());
        return EventMapper.toEventFullDto(event, comments);
    }

    @GetMapping(value = "/{eventId}/requests")
    public Collection<RequestDto> getRequestsByEventIdAndInitiatorId(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("EventPrivateController: getRequestsByEventIdAndInitiatorId — " +
                "Received request to get participation requests in events current user");
        Event event = service.getEventById(eventId);
        Collection<Request> requests = requestService.getRequestsByEventId(event, userId);
        Collection<RequestDto> requestsDto = new ArrayList<>();
        requests.forEach(r -> requestsDto.add(RequestMapper.toRequestDto(r)));

        return requestsDto;
    }

    @PatchMapping(value = "/{eventId}/requests/{reqId}/confirm")
    public RequestDto confirmRequestToEventByInitiator(@PathVariable Long userId, @PathVariable Long eventId, @PathVariable Long reqId) {
        log.info("EventPrivateController: confirmRequestToEventByInitiator — " +
                "Received request to confirm participation request in the event of the current user");
        Event event = service.getEventById(eventId);
        Request request = requestService.confirmRequestForEvent(event, userId, reqId);

        return RequestMapper.toRequestDto(request);
    }

    @PatchMapping(value = "/{eventId}/requests/{reqId}/reject")
    public RequestDto rejectRequestToEventByInitiator(@PathVariable Long userId, @PathVariable Long eventId, @PathVariable Long reqId) {
        log.info("EventPrivateController: rejectRequestToEventByInitiator — " +
                "Received request to reject participation request in the event of the current user");
        Event event = service.getEventById(eventId);
        Request request = requestService.rejectRequestForEvent(event, userId, reqId);

        return RequestMapper.toRequestDto(request);
    }
}
