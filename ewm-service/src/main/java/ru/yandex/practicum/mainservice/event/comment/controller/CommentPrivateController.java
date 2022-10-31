package ru.yandex.practicum.mainserver.event.comment.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.mainserver.event.comment.CommentService;
import ru.yandex.practicum.mainserver.event.comment.dto.CommentDto;
import ru.yandex.practicum.mainserver.event.comment.dto.NewCommentDto;
import ru.yandex.practicum.mainserver.event.comment.dto.UpdateCommentDto;
import ru.yandex.practicum.mainserver.event.comment.mapper.CommentMapper;
import ru.yandex.practicum.mainserver.event.comment.model.Comment;

import javax.validation.Valid;

/**
 * класс контроллер для работы с приватным API комментариев
 */

@RestController
@RequestMapping(path = "/users/{userId}")
@Slf4j
public class CommentPrivateController {


    private final CommentService service;

    @Autowired
    public CommentPrivateController(CommentService service) {
        this.service = service;
    }

    // создание комментария
    @PostMapping(value = "/events/{eventId}/comments")
    public CommentDto createComment(@Valid @RequestBody NewCommentDto commentDto,
                                    @PathVariable Long userId,
                                    @PathVariable Long eventId) {
        Comment comment = CommentMapper.fromNewCommentDto(commentDto);
        comment = service.addNewComment(comment, eventId, userId);
        return CommentMapper.toCommentDto(comment);
    }

    // изменение комментария
    @PatchMapping(value = "/events/{eventId}/comments")
    public CommentDto updateComment(@Valid @RequestBody UpdateCommentDto commentDto,
                                    @PathVariable Long eventId,
                                    @PathVariable Long userId) {
        Comment comment = CommentMapper.fromUpdateCommentDto(commentDto);
        comment = service.changeCommentByUser(comment, eventId, userId);
        return CommentMapper.toCommentDto(comment);
    }

    // удаление комментария
    @DeleteMapping(value = {"/comments/{commentId}"})
    public void removeComment(@PathVariable Long userId, @PathVariable Long commentId) {
        service.removeComment(commentId, userId);
    }

    // получение комментария по id
    @GetMapping(value = {"/comments/{commentId}"})
    public CommentDto getCommentById(@PathVariable Long userId, @PathVariable Long commentId) {
        Comment comment = service.getCommentById(commentId, userId);
        return CommentMapper.toCommentDto(comment);
    }
}
