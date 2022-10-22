package ru.yandex.practicum.mainserver.request.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.mainserver.request.RequestService;
import ru.yandex.practicum.mainserver.request.dto.RequestDto;
import ru.yandex.practicum.mainserver.request.mapper.RequestMapper;
import ru.yandex.practicum.mainserver.request.model.Request;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;

/**
 * класс контроллер для работы с приватным API заявок на участие
 */

@RestController
@RequestMapping(path = "/users/{userId}/requests")
@Slf4j
public class RequestPrivateController {

    private final RequestService service;

    @Autowired
    public RequestPrivateController(RequestService service) {
        this.service = service;
    }

    // создание заявки
    @PostMapping
    public RequestDto createRequest(@PathVariable @NotNull Long userId, @RequestParam @NotNull Long eventId) {
        return RequestMapper.toRequestDto(service.createRequest(userId, eventId));
    }

    // отмена своего запроса на участие
    @PatchMapping(value = {"/{requestId}/cancel"})
    public RequestDto deleteEventFromCompilation(@Valid @PathVariable @NotNull Long userId, @PathVariable @NotNull Long requestId) {
        Request request = service.cancelRequest(userId, requestId);
        return RequestMapper.toRequestDto(request);
    }


    // получение списка всех заявок на участие по eventId пользователя
    @GetMapping
    public Collection<RequestDto> getAllRequestsByUserId(@PathVariable @NotNull Long userId) {

        Collection<RequestDto> allRequestDto = new ArrayList<>();
        Collection<Request> allRequests = service.getAllRequestsByUserId(userId);

        allRequests.forEach(r -> allRequestDto.add(RequestMapper.toRequestDto(r)));
        return allRequestDto;
    }
}
