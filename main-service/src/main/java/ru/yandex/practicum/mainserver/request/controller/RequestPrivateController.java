package ru.yandex.practicum.mainserver.request.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.mainserver.compilation.CompilationService;
import ru.yandex.practicum.mainserver.compilation.CompilationServiceImpl;
import ru.yandex.practicum.mainserver.compilation.dto.CompilationDto;
import ru.yandex.practicum.mainserver.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.mainserver.compilation.mapper.CompilationMapper;
import ru.yandex.practicum.mainserver.compilation.model.Compilation;
import ru.yandex.practicum.mainserver.request.RequestService;
import ru.yandex.practicum.mainserver.request.dto.RequestDto;
import ru.yandex.practicum.mainserver.request.mapper.RequestMapper;
import ru.yandex.practicum.mainserver.request.model.Request;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.Collection;

/**
 * класс контроллер для работы с приватным API заявок на участие
 */

@RestController
@RequestMapping(path = "/users")
@Slf4j
public class RequestPrivateController {

    private final RequestService service;

    @Autowired
    public RequestPrivateController(RequestService service) {
        this.service = service;
    }

    // создание заявки
    @PostMapping(value = {"{userId}/requests"})
    public RequestDto createRequest(@PathVariable @NotNull Long userId, @RequestParam @NotNull Long eventId) {
        return RequestMapper.toRequestDto(service.createRequest(userId, eventId));
    }

    // отмена своего запроса на участие
    @DeleteMapping(value = {"{userId}/requests/{requestId}/cancel"})
    public RequestDto deleteEventFromCompilation(@Valid @PathVariable @NotNull Long userId, @PathVariable @NotNull Long requestId) {
        Request request = service.cancelRequest(userId, requestId);
        return RequestMapper.toRequestDto(request);
    }


    // получение списка всех заявок на участие по id пользователя
    @GetMapping(value = {"{userId}/requests"})
    public Collection<RequestDto> getAllRequestsByUserId(@PathVariable @NotNull Long userId) {

        Collection<RequestDto> allRequestDto = new ArrayList<>();
        Collection<Request> allRequests = service.getAllRequestsByUserId(userId);

        allRequests.forEach(r -> allRequestDto.add(RequestMapper.toRequestDto(r)));
        return allRequestDto;
    }
}
