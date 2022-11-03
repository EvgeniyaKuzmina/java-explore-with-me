package ru.yandex.practicum.mainservice.event.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.mainservice.event.EventService;
import ru.yandex.practicum.mainservice.event.comment.model.Comment;
import ru.yandex.practicum.mainservice.event.model.Event;
import ru.yandex.practicum.mainservice.exception.ConflictException;
import ru.yandex.practicum.mainservice.exception.ObjectNotFountException;
import ru.yandex.practicum.mainservice.request.RequestService;
import ru.yandex.practicum.mainservice.request.model.Request;
import ru.yandex.practicum.mainservice.status.Status;
import ru.yandex.practicum.mainservice.user.UserService;
import ru.yandex.practicum.mainservice.user.model.User;

import java.util.Collection;
import java.util.Optional;

/**
 * класс реализующий методы для работы с комментариями
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository repository;
    private final UserService userService;
    private final RequestService requestService;
    private final EventService eventService;

    @Override
    public Comment addNewComment(Comment comment, Long eventId, Long userId) {
        validateUsersRequestAndEvent(userId, eventId);
        Event event = eventService.getEventById(eventId);
        User user = userService.getUserById(userId);
        comment.setEvent(event);
        comment.setAuthor(user);
        log.info("CommentServiceImpl: addNewComment — Комментарий добавлен {}.", comment);
        return repository.save(comment);
    }

    @Override
    public Comment changeCommentByAuthor(Comment updComment, Long userId) {
        Comment comment = getCommentById(updComment.getId(), userId);
        comment.setText(updComment.getText());
        comment.setStatus(Status.PENDING);
        log.info("CommentServiceImpl: changeCommentByAuthor — Комментарий изменён {}.", comment);
        return repository.save(comment);
    }

    @Override
    public Comment changeCommentStatusByAdmin(Long commentId, Status status) {
        Comment comment = getCommentById(commentId);
        comment.setStatus(status);
        log.info("CommentServiceImpl: changeCommentStatusByAdmin — Статус комментария изменён {}.", comment);
        return repository.save(comment);
    }

    @Override
    public Collection<Comment> findPublishedByEventIdOrderByCreatDesc(Long eventId, Pageable pageable) {
        Collection<Comment> comments = repository.findByEventIdAndStatusOrderByCreatedDesc(eventId, Status.PUBLISHED, pageable).toList();
        log.info("CommentServiceImpl: findByEventIdAndStatusOrderByCreatDesc — получен список комментариев с указанным статусом");
        return comments;
    }

    @Override
    public Collection<Comment> findPublishedByEventIdOrderByCreatDesc(Long eventId) {
        Collection<Comment> comments = repository.findByEventIdAndStatusOrderByCreatedDesc(eventId, Status.PUBLISHED);
        log.info("CommentServiceImpl: findPublishedByEventIdOrderByCreatDesc — " +
                "получен список опубликованных комментариев");
        return comments;
    }

    @Override
    public Collection<Comment> findAllByAuthorIdOrderByCreatDesc(Long authorId, Pageable pageable) {
        Collection<Comment> comments = repository.findByAuthorIdOrderByCreatedDesc(authorId, pageable).toList();
        log.info("CommentServiceImpl: findAllByEventIdOrderByCreatDesc — " +
                "получен список всех комментариев пользователя");
        return comments;
    }

    @Override
    public Collection<Comment> findAllByAuthorAndStatusIdOrderByCreatDesc(Long authorId, Status status,
                                                                          Pageable pageable) {
        Collection<Comment> comments = repository.findByAuthorIdAndStatusOrderByCreatedDesc(authorId, status,
                pageable).toList();
        log.info("CommentServiceImpl: findAllByAuthorAndStatusIdOrderByCreatDesc — " +
                "получен список всех комментариев пользователя с указанным статусом");
        return comments;
    }

    @Override
    public void removeComment(Long commentId, Long userId) {
        getCommentById(commentId, userId);
        log.info("CommentServiceImpl: removeComment — Комментарий с указанным id {} удалён", commentId);
        repository.deleteById(commentId);
    }

    @Override
    public Comment getCommentById(Long commentId) {
        Optional<Comment> comment = repository.findById(commentId);
        comment.orElseThrow(() -> {
            log.warn("CommentServiceImpl: getCommentById — Комментария с указанным id {} нет", commentId);
            throw new ObjectNotFountException("Комментария с указанным id " + commentId + " нет");
        });
        log.warn("CommentServiceImpl: getCommentById — Комментарий с указанным id {} получен", commentId);
        return comment.get();
    }

    @Override
    public Comment getCommentById(Long commentId, Long userId) {
        Optional<Comment> comment = repository.findById(commentId);
        comment.orElseThrow(() -> {
            log.warn("CommentServiceImpl: getCommentById — Комментария с указанным id {} нет", commentId);
            throw new ObjectNotFountException("Комментария с указанным id " + commentId + " нет");
        });
        if (!comment.get().getAuthor().getId().equals(userId)) {
            log.warn("CommentServiceImpl: removeComment — пользователь с id {} не является автором комментария c id {}", userId, commentId);
            throw new ConflictException("Пользователь с  id " + userId + " не является автором комментария c id " + commentId);
        }
        log.warn("CommentServiceImpl: getCommentById — Комментарий с указанным id {} получен", commentId);
        return comment.get();
    }

    private void validateUsersRequestAndEvent(Long userId, Long eventId) {
        Collection<Request> request = requestService.getAllRequestsByUserId(userId);
        boolean check = false;
        for (Request r : request) {
            if (r.getEvent().getId().equals(eventId) && r.getStatus().equals(Status.CONFIRMED)) {
                check = true;
                break;
            }
        }
        if (!check) {
            log.error("CommentServiceImpl: validateUserAndEvent — Пользователь с id {} не является участником события c id {}", userId, eventId);
            throw new ConflictException(String.format("Пользователь с id %d не является участником события c id %d", userId, eventId));
        }
    }
}
