package ru.yandex.practicum.mainservice.event.comment.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.mainservice.event.comment.CommentService;
import ru.yandex.practicum.mainservice.event.comment.dto.CommentDto;
import ru.yandex.practicum.mainservice.event.comment.mapper.CommentMapper;
import ru.yandex.practicum.mainservice.event.comment.model.Comment;
import ru.yandex.practicum.mainservice.status.Status;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.Collection;

/**
 * класс контроллер для работы админа с API комментариев
 */
@RestController
@RequestMapping(path = "/admin/comments")
@Slf4j
public class CommentAdminController {

    private static final String FROM = "0";
    private static final String SIZE = "10";
    private final CommentService service;

    @Autowired
    public CommentAdminController(CommentService service) {
        this.service = service;
    }

    @PatchMapping(value = {"/{commentId}/publish"})
    public CommentDto publishComment(@PathVariable Long commentId) {
        log.info("CommentAdminController: publishComment — получен запрос на публикацию комментария");
        Comment comment = service.changeStatusForCommentByAdmin(commentId, Status.PUBLISHED);
        return CommentMapper.toCommentDto(comment);
    }

    @PatchMapping(value = {"/{commentId}/reject"})
    public CommentDto rejectComment(@PathVariable Long commentId) {
        log.info("CommentAdminController: rejectComment — получен запрос на отклонение комментария");
        Comment comment = service.changeStatusForCommentByAdmin(commentId, Status.REJECTED);
        return CommentMapper.toCommentDto(comment);
    }

    @GetMapping
    public Collection<CommentDto> getAllCommentsByStatus(@RequestParam(required = false) String state,
                                                         @RequestParam(defaultValue = "desc") String sort,
                                                         @RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
                                                         @RequestParam(defaultValue = SIZE) @Positive Integer size) {
        log.info("CommentAdminController: getAllCommentsByStatus — получен запрос на получение всех комментариев пользователя");
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);

        if (!sort.equalsIgnoreCase("desc") && !sort.equalsIgnoreCase("asc")) {
            log.warn("CommentPublicController: getAllCommentsByStatus — указан неверный формат сортировки");
            throw new IllegalArgumentException("Unknown sort type: " + sort);
        }

        Collection<Comment> comment;
        if (state == null) {
            comment = service.getAllSortedByCreatedDate(sort, pageable);
        } else {
            Status status = Status.from(state);
            if (status != Status.PUBLISHED && status != Status.REJECTED && status != Status.PENDING) {
                log.warn("CommentPublicController: getAllCommentsByStatus — указан неверный статус для получения комментария");
                throw new IllegalArgumentException("Unknown state: " + state);
            }
            comment = service.getByStatusSortedByCreatedDate(status, sort, pageable);
        }

        Collection<CommentDto> commentDto = new ArrayList<>();
        comment.forEach(c -> commentDto.add(CommentMapper.toCommentDto(c)));
        return commentDto;
    }

    @GetMapping(value = {"/{commentId}"})
    public CommentDto getCommentById(@PathVariable Long commentId) {
        log.info("CommentPublicController: getCommentById — получен запрос на получение комментария по id {} ", commentId);
        Comment comment = service.getCommentById(commentId);
        return CommentMapper.toCommentDto(comment);
    }
}
