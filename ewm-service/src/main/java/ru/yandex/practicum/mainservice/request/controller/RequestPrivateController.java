package ru.yandex.practicum.mainservice.request.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.mainservice.request.RequestService;
import ru.yandex.practicum.mainservice.request.dto.RequestDto;
import ru.yandex.practicum.mainservice.request.mapper.RequestMapper;
import ru.yandex.practicum.mainservice.request.model.Request;

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

    @PostMapping
    public RequestDto createRequest(@PathVariable @NotNull Long userId, @RequestParam @NotNull Long eventId) {
        log.info("RequestPrivateController: createRequest — received request to create request to participation");
        return RequestMapper.toRequestDto(service.createRequest(userId, eventId));
    }

    @PatchMapping(value = {"/{requestId}/cancel"})
    public RequestDto deleteRequest(@Valid @PathVariable @NotNull Long userId, @PathVariable @NotNull Long requestId) {
        log.info("RequestPrivateController: deleteRequest — received request to delete request to participation");
        Request request = service.cancelRequest(userId, requestId);
        return RequestMapper.toRequestDto(request);
    }

    @GetMapping
    public Collection<RequestDto> getAllRequestsByUserId(@PathVariable @NotNull Long userId) {
        log.info("RequestPrivateController: deleteRequest — received request to get list of requests to participation by author id");
        Collection<RequestDto> allRequestDto = new ArrayList<>();
        Collection<Request> allRequests = service.getAllRequestsByUserId(userId);
        allRequests.forEach(r -> allRequestDto.add(RequestMapper.toRequestDto(r)));
        return allRequestDto;
    }
}
