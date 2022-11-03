package ru.yandex.practicum.mainservice.request.mapper;

import ru.yandex.practicum.mainservice.request.dto.RequestDto;
import ru.yandex.practicum.mainservice.request.model.Request;

import java.util.Optional;

public class RequestMapper {
    private RequestMapper() {}

    public static RequestDto toRequestDto(Request request) {
        RequestDto requestDto = RequestDto.builder()
                .id(request.getId())
                .status(request.getStatus())
                .requester(request.getRequester().getId())
                .event(request.getEvent().getId())
                .build();
        Optional.ofNullable(request.getCreated()).ifPresent(requestDto::setCreated);
        return requestDto;
    }
}
