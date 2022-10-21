package ru.yandex.practicum.mainserver.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.mainserver.event.model.EventParam;
import ru.yandex.practicum.mainserver.event.EventService;
import ru.yandex.practicum.mainserver.event.dto.EventShortDto;
import ru.yandex.practicum.mainserver.event.mapper.EventMapper;
import ru.yandex.practicum.mainserver.event.model.Event;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.*;

/**
 * класс контроллер для работы с публичным API событий
 */

@RestController
@RequestMapping(path = "/events")
@Slf4j
public class EventPublicController {

    private static final String FROM = "0";
    private static final String SIZE = "10";
    private final EventService service;

    @Autowired
    public EventPublicController(EventService service) {
        this.service = service;
    }


    // получение списка всех событий на участие
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

        EventParam param =  creatParam(text, categoriesId, paid, rangeStart, rangeEnd, onlyAvailable, sort);

        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        Collection<Event> events = service.getEventsByPublicParams(param, pageable);
        Collection<EventShortDto> eventsShortDto = new ArrayList<>();
        events.forEach(e -> eventsShortDto.add(EventMapper.toEventShortDto(e)));
        return eventsShortDto;
    }


    // получение информации о событии по его id
    @GetMapping(value = {"/{id}"})
    public EventShortDto getUserById(@PathVariable Long id) {
        Event event = service.getEventById(id);
        return EventMapper.toEventShortDto(event);
    }

    // преобразование параметров запроса в объект EventParam
    private EventParam creatParam (String text,
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
            Optional.of(rangeStart).ifPresent(param::setRangeStart);
        } else {
            param.setRangeStart(LocalDateTime.now());
        }
        Optional.ofNullable(rangeEnd).ifPresent(param::setRangeEnd);
        Optional.ofNullable(onlyAvailable).ifPresent(param::setOnlyAvailable);
        Optional.ofNullable(sort).ifPresent(param::setSort);


        return param;

    }
}
