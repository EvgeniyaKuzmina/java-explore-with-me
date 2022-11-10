package ru.yandex.practicum.mainservice.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.mainservice.client.EventClient;
import ru.yandex.practicum.mainservice.event.EventService;
import ru.yandex.practicum.mainservice.event.comment.CommentService;
import ru.yandex.practicum.mainservice.event.comment.model.Comment;
import ru.yandex.practicum.mainservice.event.dto.EventFullDto;
import ru.yandex.practicum.mainservice.event.dto.EventShortDto;
import ru.yandex.practicum.mainservice.event.mapper.EventMapper;
import ru.yandex.practicum.mainservice.event.model.Event;
import ru.yandex.practicum.mainservice.event.model.EventParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * класс контроллер для работы с публичным API событий
 */
@RequestMapping(path = "/events")
@Slf4j
@RestController
public class EventPublicController {

    private static final String FROM = "0";
    private static final String SIZE = "10";
    private final EventService service;
    private final EventClient client;
    private final CommentService commentService;

    @Autowired
    public EventPublicController(EventService service, EventClient client, CommentService commentService) {
        this.service = service;
        this.client = client;
        this.commentService = commentService;
    }

    @GetMapping
    public Collection<EventShortDto> getAllEvent(@RequestParam(required = false) String text,
                                                 @RequestParam(name = "categories", required = false) List<Long> categoriesId,
                                                 @RequestParam(required = false) Boolean paid,
                                                 @RequestParam(required = false) String rangeStart,
                                                 @RequestParam(required = false) String rangeEnd,
                                                 @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                 @RequestParam(required = false) String sort,
                                                 @RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
                                                 @RequestParam(defaultValue = SIZE) @Positive Integer size) {
        log.info("EventPublicController: getAllEvent — received request to get list of all events by params");
        EventParam param = creatParam(text, categoriesId, paid, rangeStart, rangeEnd, onlyAvailable, sort);

        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        Collection<Event> events = service.getEventsByPublicParams(param, pageable);
        Collection<EventShortDto> eventsShortDto = new ArrayList<>();
        events.forEach(e -> eventsShortDto.add(EventMapper.toEventShortDto(e)));
        return eventsShortDto;
    }

    @GetMapping(value = {"/{id}"})
    public EventFullDto getEventById(@PathVariable Long id, HttpServletRequest request) {
        log.info("EventPublicController: getEventById — received request to get list of event by id");
        Event event = service.getEventById(id);
        client.addStatistic(request);
        Collection<Comment> comments = commentService.getPublishedByEventId(event.getId());
        return EventMapper.toEventFullDto(event, comments);
    }

    private EventParam creatParam(String text,
                                  List<Long> categoriesId,
                                  Boolean paid,
                                  String rangeStart,
                                  String rangeEnd,
                                  Boolean onlyAvailable,
                                  String sort) {

        EventParam param = new EventParam();
        Optional.ofNullable(text).ifPresent(param::setText);
        Optional.ofNullable(categoriesId).ifPresent(param::setCategoriesId);
        Optional.ofNullable(paid).ifPresent(param::setPaid);

        if (rangeStart != null) {
            param.setRangeStart(rangeStart);
        } else {
            param.setRangeStart(LocalDateTime.now());
        }
        Optional.ofNullable(rangeEnd).ifPresent(param::setRangeEnd);
        Optional.ofNullable(onlyAvailable).ifPresent(param::setOnlyAvailable);
        Optional.ofNullable(sort).ifPresent(param::setSort);
        log.info("EventPublicController: creatParam — params of request was converted to EventParam");
        return param;
    }
}
