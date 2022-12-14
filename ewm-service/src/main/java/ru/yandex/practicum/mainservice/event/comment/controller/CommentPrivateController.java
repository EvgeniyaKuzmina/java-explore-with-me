package ru.yandex.practicum.mainservice.event.comment.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.mainservice.event.comment.CommentService;
import ru.yandex.practicum.mainservice.event.comment.dto.CommentDto;
import ru.yandex.practicum.mainservice.event.comment.dto.NewCommentDto;
import ru.yandex.practicum.mainservice.event.comment.dto.UpdateCommentDto;
import ru.yandex.practicum.mainservice.event.comment.mapper.CommentMapper;
import ru.yandex.practicum.mainservice.event.comment.model.Comment;
import ru.yandex.practicum.mainservice.status.Status;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.Collection;

/**
 * класс контроллер для работы с приватным API комментариев
 */
@RestController
@RequestMapping(path = "/users/{userId}")
@Slf4j
public class CommentPrivateController {

    private static final String FROM = "0";
    private static final String SIZE = "10";
    private final CommentService service;

    @Autowired
    public CommentPrivateController(CommentService service) {
        this.service = service;
    }

    @PostMapping(value = "/events/{eventId}/comments")
    public CommentDto createComment(@Valid @RequestBody NewCommentDto commentDto,
                                    @PathVariable Long userId,
                                    @PathVariable Long eventId) {
        log.info("CommentPublicController: getAllCommentsByEventId — received request on create comment");
        Comment comment = CommentMapper.fromNewCommentDto(commentDto);
        comment = service.addNewComment(comment, eventId, userId);
        return CommentMapper.toCommentDto(comment);
    }

    @PatchMapping(value = "/comments")
    public CommentDto updateComment(@Valid @RequestBody UpdateCommentDto commentDto,
                                    @PathVariable Long userId) {
        log.info("CommentPublicController: getAllCommentsByEventId — received request on update comment");
        Comment comment = CommentMapper.fromUpdateCommentDto(commentDto);
        comment = service.changeCommentByAuthor(comment, userId);
        return CommentMapper.toCommentDto(comment);
    }

    @DeleteMapping(value = {"/comments/{commentId}"})
    public void removeComment(@PathVariable Long userId, @PathVariable Long commentId) {
        log.info("CommentPublicController: getAllCommentsByEventId — received request on delete comment");
        service.removeComment(commentId, userId);
    }

    @GetMapping(value = {"/comments/{commentId}"})
    public CommentDto getCommentById(@PathVariable Long userId, @PathVariable Long commentId) {
        log.info("CommentPublicController: getAllCommentsByEventId — received request to get comment by id");
        Comment comment = service.getCommentById(commentId, userId);
        return CommentMapper.toCommentDto(comment);
    }

    @GetMapping(value = "/comments")
    public Collection<CommentDto> getAllCommentsByAuthorId(@PathVariable Long userId,
                                                           @RequestParam(required = false) String state,
                                                           @RequestParam(defaultValue = "desc") String sort,
                                                           @RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
                                                           @RequestParam(defaultValue = SIZE) @Positive Integer size) {
        log.info("CommentPublicController: getAllCommentsByEventId — received request to get all comments by user");
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);

        if (!sort.equalsIgnoreCase("desc") && !sort.equalsIgnoreCase("asc")) {
            log.warn("CommentPublicController: getAllCommentsByStatus — Unknown sort type");
            throw new IllegalArgumentException("Unknown sort type: " + sort);
        }

        Collection<Comment> comment;
        if (state == null) {
            comment = service.getAllByAuthorId(userId, pageable);
        } else {
            Status status = Status.from(state);
            if (status != Status.PUBLISHED && status != Status.REJECTED && status != Status.PENDING) {
                log.warn("CommentPublicController: getAllCommentsByEventId — unknown state");
                throw new IllegalArgumentException("Unknown state: " + state);
            }
            comment = service.getAllByAuthorIdAndStatus(userId, status, sort, pageable);
        }

        Collection<CommentDto> commentDto = new ArrayList<>();
        comment.forEach(c -> commentDto.add(CommentMapper.toCommentDto(c)));
        return commentDto;
    }
}
