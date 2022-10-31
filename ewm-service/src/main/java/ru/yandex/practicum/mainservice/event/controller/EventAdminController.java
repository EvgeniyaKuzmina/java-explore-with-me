package ru.yandex.practicum.mainservice.event.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.mainservice.category.CategoryService;
import ru.yandex.practicum.mainservice.category.model.Category;
import ru.yandex.practicum.mainservice.event.EventService;
import ru.yandex.practicum.mainservice.event.dto.AdminUpdateEventRequest;
import ru.yandex.practicum.mainservice.event.dto.EventFullDto;
import ru.yandex.practicum.mainservice.event.mapper.EventMapper;
import ru.yandex.practicum.mainservice.event.model.Event;
import ru.yandex.practicum.mainservice.event.model.EventParam;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * класс контроллер для работы с API администратора событий
 */
@RestController
@RequestMapping(path = "/admin/events")
@Slf4j
public class EventAdminController {

    private static final String FROM = "0";
    private static final String SIZE = "10";
    private final EventService service;
    private final CategoryService categoryService;

    @Autowired
    public EventAdminController(EventService service, CategoryService categoryService) {
        this.service = service;
        this.categoryService = categoryService;
    }

    @GetMapping
    public Collection<EventFullDto> getAllEvent(@RequestParam(name = "users", required = false) List<Long> usersId,
                                                @RequestParam(name = "categories", required = false) List<Long> categoriesId,
                                                @RequestParam(required = false) List<String> states,
                                                @RequestParam(required = false) String rangeStart,
                                                @RequestParam(required = false) String rangeEnd,
                                                @RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
                                                @RequestParam(defaultValue = SIZE) @Positive Integer size) {
        log.info("EventAdminController: getAllEvent — получен запрос на получение списка всех событий");
        EventParam param = creatParam(usersId, categoriesId, states, rangeStart, rangeEnd);

        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        Collection<Event> events = service.getEventsByAdminParams(param, pageable);
        Collection<EventFullDto> eventFullDto = new ArrayList<>();
        events.forEach(e -> eventFullDto.add(EventMapper.toEventFullDto(e)));
        return eventFullDto;
    }

    @PutMapping(value = {"/{eventId}"})
    public EventFullDto updateEventByAdmin(@Valid @RequestBody AdminUpdateEventRequest eventDto, @PathVariable Long eventId) {
        log.info("EventAdminController: updateEventByAdmin — получен запрос на обновление события админом");
        Category category = categoryService.getCategoryById(eventDto.getCategory());
        Event event = EventMapper.toEventFromAdminUpdDto(eventDto, category);
        event = service.updateEventByAdmin(event, eventId);
        return EventMapper.toEventFullDto(event);
    }

    @PatchMapping(value = {"/{eventId}/publish"})
    public EventFullDto publishEvent(@PathVariable Long eventId) {
        log.info("EventAdminController: publishEvent — получен запрос на публикацию события");
        Event event = service.publishedEventByAdmin(eventId);
        return EventMapper.toEventFullDto(event);
    }

    @PatchMapping(value = {"/{eventId}/reject"})
    public EventFullDto rejectEvent(@PathVariable Long eventId) {
        log.info("EventAdminController: rejectEvent — получен запрос на отклонение события");
        Event event = service.rejectedEventByAdmin(eventId);
        return EventMapper.toEventFullDto(event);
    }

    private EventParam creatParam(List<Long> usersId,
                                  List<Long> categoriesId,
                                  List<String> states,
                                  String rangeStart,
                                  String rangeEnd) {

        EventParam param = new EventParam();
        Optional.ofNullable(usersId).ifPresent(param::setUsersId);
        Optional.ofNullable(categoriesId).ifPresent(param::setCategoriesId);
        Optional.ofNullable(states).ifPresent(param::setStates);
        Optional.ofNullable(rangeStart).ifPresent(param::setRangeStart);
        Optional.ofNullable(rangeEnd).ifPresent(param::setRangeEnd);
        log.info("EventAdminController: creatParam — параметры запроса преобразованы в объект EventParam");
        return param;
    }
}
