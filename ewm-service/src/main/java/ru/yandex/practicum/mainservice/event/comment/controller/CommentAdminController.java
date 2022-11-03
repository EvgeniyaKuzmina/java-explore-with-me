package ru.yandex.practicum.mainservice.event.comment.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.mainservice.event.comment.CommentService;
import ru.yandex.practicum.mainservice.event.comment.dto.CommentDto;
import ru.yandex.practicum.mainservice.event.comment.mapper.CommentMapper;
import ru.yandex.practicum.mainservice.event.comment.model.Comment;
import ru.yandex.practicum.mainservice.event.dto.EventFullDto;
import ru.yandex.practicum.mainservice.event.mapper.EventMapper;
import ru.yandex.practicum.mainservice.event.model.Event;
import ru.yandex.practicum.mainservice.status.Status;

import java.util.Collection;

/**
 * класс контроллер для работы админа с API комментариев
 */
@RestController
@RequestMapping(path = "/admin/comments")
@Slf4j
public class CommentAdminController {

    private final CommentService service;

    @Autowired
    public CommentAdminController(CommentService service) {
        this.service = service;
    }

    @PatchMapping(value = {"/{commentId}/publish"})
    public CommentDto publishComment(@PathVariable Long commentId) {
        log.info("CommentAdminController: publishComment — получен запрос на публикацию комментария");
        Comment comment = service.changeCommentStatusByAdmin(commentId, Status.PUBLISHED);
        return CommentMapper.toCommentDto(comment);
    }

    @PatchMapping(value = {"/{commentId}/reject"})
    public CommentDto rejectComment(@PathVariable Long commentId) {
        log.info("CommentAdminController: rejectComment — получен запрос на отклонение комментария");
        Comment comment = service.changeCommentStatusByAdmin(commentId, Status.REJECTED);
        return CommentMapper.toCommentDto(comment);
    }
}
