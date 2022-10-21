package ru.yandex.practicum.mainserver.event.mapper;

import ru.yandex.practicum.mainserver.category.mapper.CategoryMapper;
import ru.yandex.practicum.mainserver.category.model.Category;
import ru.yandex.practicum.mainserver.event.dto.*;
import ru.yandex.practicum.mainserver.event.location.mapper.LocationMapper;
import ru.yandex.practicum.mainserver.event.model.Event;
import ru.yandex.practicum.mainserver.user.mapper.UserMapper;

import java.util.Optional;

public class EventMapper {

    /*@Autowired
    public CategoryAdminController(CategoryServiceImpl service) {
        this.service = service;
    }*/

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
        EventFullDto ev = EventFullDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .state(event.getState())
                .paid(event.getPaid())
                .location(LocationMapper.toLocationDto(event.getLocation()))
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .views(event.getViews())
                .comments(event.getComments())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .build();
        Optional.ofNullable(event.getPublishedOn()).ifPresent(ev::setPublishedOn);
        Optional.ofNullable(event.getCreatedOn()).ifPresent(ev::setCreatedOn);
        Optional.ofNullable(event.getEventDate()).ifPresent(ev::setEventDate);
        return ev;
    }

    public static Event toEventFromAdminUpdDto(AdminUpdateEventRequest eventDto) {
        Event event = Event.builder()
                .title(eventDto.getTitle())
                .annotation(eventDto.getAnnotation())
                .description(eventDto.getDescription())
                .paid(eventDto.getPaid())
                .createdOn(eventDto.getCreatedOn())
                .location(LocationMapper.toLocation(eventDto.getLocation()))
                .category(CategoryMapper.toCategory(eventDto.getCategory()))
                .participantLimit(eventDto.getParticipantLimit())
                .requestModeration(eventDto.getRequestModeration())
                .build();
        Optional.ofNullable(eventDto.getEventDate()).ifPresent(event::setEventDate);
        return event;
    }

    public static Event toEventFromUpdateDto(UpdateEventDto eventDto) {
        Event event = Event.builder()
                .id(eventDto.getId())
                .title(eventDto.getTitle())
                .annotation(eventDto.getAnnotation())
                .description(eventDto.getDescription())
                .paid(eventDto.getPaid())
                .category(CategoryMapper.toCategory(eventDto.getCategory()))
                .participantLimit(eventDto.getParticipantLimit())
                .build();
        Optional.ofNullable(eventDto.getEventDate()).ifPresent(event::setEventDate);
        return event;
    }

    public static Event toEventFromNewDto(NewEventDto eventDto, Category category) {
        Event event = Event.builder()
                .title(eventDto.getTitle())
                .annotation(eventDto.getAnnotation())
                .description(eventDto.getDescription())
                .paid(eventDto.getPaid())
                .category(category)
                .participantLimit(eventDto.getParticipantLimit())
                .requestModeration(eventDto.getRequestModeration())
                .location(LocationMapper.toLocation(eventDto.getLocation()))
                .build();
        Optional.ofNullable(eventDto.getEventDate()).ifPresent(event::setEventDate);
        return event;
    }
}
