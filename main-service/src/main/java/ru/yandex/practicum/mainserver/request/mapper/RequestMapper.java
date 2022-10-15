package ru.yandex.practicum.mainserver.request.mapper;

import ru.yandex.practicum.mainserver.request.dto.RequestDto;
import ru.yandex.practicum.mainserver.request.model.Request;

public class RequestMapper {

    public static RequestDto toRequestDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .status(request.getStatus())
                .created(request.getCreated())
                .requesterId(request.getRequester().getId())
                .eventId(request.getEvent().getId())
                .build();
    }

}
