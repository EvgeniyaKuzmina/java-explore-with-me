package ru.yandex.practicum.mainserver.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.mainserver.event.EventService;
import ru.yandex.practicum.mainserver.event.dto.EventFullDto;
import ru.yandex.practicum.mainserver.event.model.Event;
import ru.yandex.practicum.mainserver.request.dto.RequestDto;
import ru.yandex.practicum.mainserver.request.mapper.RequestMapper;
import ru.yandex.practicum.mainserver.request.model.Request;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    public Collection<EventFullDto> getAllEvent(@RequestParam(required = false) String text,
                                                @RequestParam(name = "categories", required = false) List<Long> categoriesId,
                                                @RequestParam(required = false) Boolean paid,
                                                @RequestParam(required = false) LocalDateTime rangeStart,
                                                @RequestParam(required = false) LocalDateTime rangeEnd,
                                                @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                @RequestParam(required = false) String sort,
                                                @RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
                                                @RequestParam(defaultValue = SIZE) @Positive Integer size) {

        Collection<RequestDto> allRequestDto = new ArrayList<>();
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        Collection<Event> allEvents = service.getAllEvent(text, categoriesId, paid, rangeStart, );

        allRequests.forEach(r -> allRequestDto.add(RequestMapper.toRequestDto(r)));
        return allRequestDto;
    }
}
