package ru.yandex.practicum.mainservice.event.comment.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.mainservice.event.comment.CommentService;
import ru.yandex.practicum.mainservice.event.comment.dto.CommentShortDto;
import ru.yandex.practicum.mainservice.event.comment.mapper.CommentMapper;
import ru.yandex.practicum.mainservice.event.comment.model.Comment;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.Collection;

/**
 * класс контроллер для работы с публичным API комментариев
 */

@RestController
@RequestMapping(path = "events/{eventId}/comments")
@Slf4j
public class CommentPublicController {

    private static final String FROM = "0";
    private static final String SIZE = "10";
    private final CommentService service;

    @Autowired
    public CommentPublicController(CommentService service) {
        this.service = service;
    }

    @GetMapping
    public Collection<CommentShortDto> getAllCommentsByEventId(@PathVariable Long eventId,
                                                               @RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
                                                               @RequestParam(defaultValue = SIZE) @Positive Integer size) {
        log.info("CommentPublicController: getAllCommentsByEventId — Received request to get all published comments by event");
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        Collection<Comment> comment = service.getPublishedByEventIdWithPagination(eventId, pageable);
        Collection<CommentShortDto> commentDto = new ArrayList<>();
        comment.forEach(c -> commentDto.add(CommentMapper.toCommentShortDto(c)));
        return commentDto;
    }
}
