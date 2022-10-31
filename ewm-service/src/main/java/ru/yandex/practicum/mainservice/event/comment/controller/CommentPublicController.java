package ru.yandex.practicum.mainserver.event.comment.controller;



import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.mainserver.event.comment.CommentService;
import ru.yandex.practicum.mainserver.event.comment.dto.CommentDto;
import ru.yandex.practicum.mainserver.event.comment.mapper.CommentMapper;
import ru.yandex.practicum.mainserver.event.comment.model.Comment;

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

    // получение списка всех комментариев по id события
    @GetMapping
    public Collection<CommentDto> getAllCommentsByEventId(@PathVariable Long eventId,
                                                          @RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
                                                          @RequestParam(defaultValue = SIZE) @Positive Integer size) {


        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        Collection<Comment> comment = service.findAllByEventIdOrderByCreatDesc(eventId, pageable);
        Collection<CommentDto> commentDto = new ArrayList<>();
        comment.forEach(c -> commentDto.add(CommentMapper.toCommentDto(c)));
        return commentDto;
    }
}
