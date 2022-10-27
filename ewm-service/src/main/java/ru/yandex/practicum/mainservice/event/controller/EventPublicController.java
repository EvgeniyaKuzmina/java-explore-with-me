package ru.yandex.practicum.mainservice.event.controller;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.mainservice.client.EndpointHit;
import ru.yandex.practicum.mainservice.client.EventClient;
import ru.yandex.practicum.mainservice.event.EventService;
import ru.yandex.practicum.mainservice.event.dto.EventFullDto;
import ru.yandex.practicum.mainservice.event.dto.EventShortDto;
import ru.yandex.practicum.mainservice.event.mapper.EventMapper;
import ru.yandex.practicum.mainservice.event.model.Event;
import ru.yandex.practicum.mainservice.event.model.EventParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
@Validated
public class EventPublicController {

    private static final String FROM = "0";
    private static final String SIZE = "10";
    private final EventService service;
    private final EventClient client;

    @Autowired
    public EventPublicController(EventService service, EventClient client) {
        this.service = service;
        this.client = client;
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

        EventParam param = creatParam(text, categoriesId, paid, rangeStart, rangeEnd, onlyAvailable, sort);

        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        Collection<Event> events = service.getEventsByPublicParams(param, pageable);
        log.info(events.toString());
        Collection<EventShortDto> eventsShortDto = new ArrayList<>();
        events.forEach(e -> eventsShortDto.add(EventMapper.toEventShortDto(e)));
        return eventsShortDto;
    }


    // получение подробной информации о событии по его eventId
    @GetMapping(value = {"/{id}"})
    public EventFullDto getUserById(@PathVariable Long id, HttpServletRequest request) throws UnsupportedEncodingException {
        EndpointHit endpointHit = EndpointHit.builder()
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(encodingTime())
                .build();

        Event event = service.getEventById(id);
        client.addStatistic(endpointHit); // сохранение статистики в сервисе статистики
        return EventMapper.toEventFullDto(event);
    }

    // преобразование параметров запроса в объект EventParam
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

        return param;
    }

    private byte[] encodingTime() {
        LocalDateTime timestamp = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestampStr  = timestamp.format(formatter);
        return timestampStr.getBytes(StandardCharsets.UTF_8);
    }
}
