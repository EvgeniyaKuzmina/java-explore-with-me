package ru.yandex.practicum.mainservice.event.mapper;

import ru.yandex.practicum.mainservice.category.mapper.CategoryMapper;
import ru.yandex.practicum.mainservice.category.model.Category;
import ru.yandex.practicum.mainservice.event.comment.dto.CommentShortDto;
import ru.yandex.practicum.mainservice.event.comment.mapper.CommentMapper;
import ru.yandex.practicum.mainservice.event.comment.model.Comment;
import ru.yandex.practicum.mainservice.event.dto.*;
import ru.yandex.practicum.mainservice.event.location.mapper.LocationMapper;
import ru.yandex.practicum.mainservice.event.model.Event;
import ru.yandex.practicum.mainservice.user.mapper.UserMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class EventMapper {
    private EventMapper() {
    }

    public static EventShortDto toEventShortDto(Event event) {
        EventShortDto eventDto = EventShortDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .paid(event.getPaid())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequest())
                .views(event.getViews())
                .comments(event.getComments())
                .build();
        Optional.ofNullable(event.getEventDate()).ifPresent(eventDto::setEventDate);
        return eventDto;
    }

    public static EventShortDtoForComment toEventShortDtoForComment(Event event) {
        EventShortDtoForComment eventDto = EventShortDtoForComment.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .paid(event.getPaid())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequest())
                .views(event.getViews())
                .build();
        Optional.ofNullable(event.getEventDate()).ifPresent(eventDto::setEventDate);
        return eventDto;
    }

    public static EventFullDto toEventFullDto(Event event, Collection<Comment> comments) {
        Collection<CommentShortDto> commentDto = new ArrayList<>();
        comments.forEach(c -> commentDto.add(CommentMapper.toCommentShortDto(c)));
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
                .confirmedRequests(event.getConfirmedRequest())
                .views(event.getViews())
                .comments(commentDto)
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .build();
        Optional.ofNullable(event.getPublishedOn()).ifPresent(ev::setPublishedOn);
        Optional.ofNullable(event.getCreatedOn()).ifPresent(ev::setCreatedOn);
        Optional.ofNullable(event.getEventDate()).ifPresent(ev::setEventDate);
        return ev;
    }

    public static Event toEventFromAdminUpdDto(AdminUpdateEventRequest eventDto, Category category) {
        Event event = Event.builder()
                .title(eventDto.getTitle())
                .annotation(eventDto.getAnnotation())
                .description(eventDto.getDescription())
                .paid(eventDto.getPaid())
                .createdOn(eventDto.getCreatedOn())
                .category(category)
                .participantLimit(eventDto.getParticipantLimit())
                .requestModeration(eventDto.getRequestModeration())
                .build();
        Optional.ofNullable(eventDto.getEventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(eventDto.getLocation()).ifPresent(dto -> event.setLocation(LocationMapper.toLocation(eventDto.getLocation())));
        return event;
    }

    public static Event toEventFromUpdateDto(UpdateEventDto eventDto, Category category) {
        Event event = Event.builder()
                .id(eventDto.getEventId())
                .title(eventDto.getTitle())
                .annotation(eventDto.getAnnotation())
                .description(eventDto.getDescription())
                .paid(eventDto.getPaid())
                .category(category)
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
