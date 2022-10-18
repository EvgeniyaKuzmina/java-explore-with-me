package ru.yandex.practicum.mainserver.event.mapper;

import ru.yandex.practicum.mainserver.category.mapper.CategoryMapper;
import ru.yandex.practicum.mainserver.event.dto.AdminUpdateEventRequest;
import ru.yandex.practicum.mainserver.event.dto.EventFullDto;
import ru.yandex.practicum.mainserver.event.dto.EventShortDto;
import ru.yandex.practicum.mainserver.event.dto.UpdateEventDto;
import ru.yandex.practicum.mainserver.event.location.mapper.LocationMapper;
import ru.yandex.practicum.mainserver.event.model.Event;
import ru.yandex.practicum.mainserver.user.mapper.UserMapper;

public class EventMapper {

    public static EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .paid(event.getPaid())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .views(event.getViews())
                .comments(event.getComments())
                .build();
    }

    public static EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .state(event.getState())
                .paid(event.getPaid())
                .createdOn(event.getCreatedOn())
                .eventDate(event.getEventDate())
                .publishedOn(event.getPublishedOn())
                .location(LocationMapper.toLocationDto(event.getLocation()))
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .views(event.getViews())
                .comments(event.getComments())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .build();
    }

    public static Event toEventFromAdminUpdDto(AdminUpdateEventRequest eventDto) {
        return Event.builder()
                .title(eventDto.getTitle())
                .annotation(eventDto.getAnnotation())
                .description(eventDto.getDescription())
                .paid(eventDto.getPaid())
                .createdOn(eventDto.getCreatedOn())
                .eventDate(eventDto.getEventDate())
                .location(LocationMapper.toLocation(eventDto.getLocation()))
                .category(CategoryMapper.toCategory(eventDto.getCategory()))
                .participantLimit(eventDto.getParticipantLimit())
                .requestModeration(eventDto.getRequestModeration())
                .build();
    }

    public static Event toEventFromUpdateDto(UpdateEventDto eventDto) {
        return Event.builder()
                .id(eventDto.getId())
                .title(eventDto.getTitle())
                .annotation(eventDto.getAnnotation())
                .description(eventDto.getDescription())
                .paid(eventDto.getPaid())
                .eventDate(eventDto.getEventDate())
                .category(CategoryMapper.toCategory(eventDto.getCategory()))
                .participantLimit(eventDto.getParticipantLimit())
                .build();
    }
}
