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
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

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
        validateUsersRequestAndEvent(userId, comment.getEvent().getId());
        comment.setText(updComment.getText());
        comment.setStatus(Status.PENDING);
        log.info("CommentServiceImpl: changeCommentByAuthor — Комментарий изменён {}.", comment);
        return repository.save(comment);
    }

    @Override
    public Comment changeStatusForCommentByAdmin(Long commentId, Status status) {
        Comment comment = getCommentById(commentId);
        comment.setStatus(status);
        log.info("CommentServiceImpl: changeStatusForCommentByAdmin — Статус комментария изменён {}.", comment);
        return repository.save(comment);
    }

    @Override
    public Collection<Comment> getPublishedByEventIdWithPagination(Long eventId, Pageable pageable) {
        Collection<Comment> comments = repository.findByEventIdAndStatusOrderByCreatedDesc(eventId, Status.PUBLISHED, pageable).toList();
        log.info("CommentServiceImpl: getPublishedByEventIdWithPagination — получен список комментариев с указанным статусом");
        return comments;
    }

    @Override
    public Collection<Comment> getPublishedByEventId(Long eventId) {
        Collection<Comment> comments = repository.findByEventIdAndStatusOrderByCreatedDesc(eventId, Status.PUBLISHED);
        log.info("CommentServiceImpl: getPublishedByEventId — " +
                "получен список опубликованных комментариев к событию с id {}", eventId);
        return comments;
    }

    @Override
    public Collection<Comment> getPublishedByListEventId(Collection<Long> eventId) {
        Collection<Comment> comments = repository.findByEventIdInAndStatusOrderByCreatedDesc(eventId, Status.PUBLISHED);
        log.info("CommentServiceImpl: getPublishedByListEventId — " +
                "получен список опубликованных комментариев к указанным событиям c id {}", eventId);
        return comments;
    }

    @Override
    public Collection<Comment> getAllByAuthorId(Long authorId, Pageable pageable) {
        Collection<Comment> comments = repository.findByAuthorIdOrderByCreatedDesc(authorId, pageable).toList();
        log.info("CommentServiceImpl: getAllByAuthorId — " +
                "получен список всех комментариев пользователя");
        return comments;
    }

    @Override
    public Collection<Comment> getAllByAuthorIdAndStatus(Long authorId, Status status, String sort, Pageable pageable) {
        Collection<Comment> comments;
        if (sort.equalsIgnoreCase("desc")) {
            comments = repository.findByAuthorIdAndStatusOrderByCreatedDesc(authorId, status, pageable).toList();
        } else {
            comments = repository.findByAuthorIdAndStatusOrderByCreatedAsc(authorId, status, pageable).toList();
        }
        log.info("CommentServiceImpl: getAllByAuthorIdAndStatus — " +
                "получен список всех комментариев пользователя с указанным статусом");
        return comments;
    }

    @Override
    public Collection<Comment> getByStatusSortedByCreatedDate(Status status, String sort, Pageable pageable) {
        Collection<Comment> comments;
        if (sort.equalsIgnoreCase("desc")) {
            comments = repository.findByStatusOrderByCreatedDesc(status, pageable).toList();
        } else {
            comments = repository.findByStatusOrderByCreatedAsc(status, pageable).toList();
        }
        log.info("CommentServiceImpl: getByStatusSortedByCreatedDate — " +
                "получен список всех комментариев с указанным статусом");
        return comments;
    }

    @Override
    public Collection<Comment> getAllSortedByCreatedDate(String sort, Pageable pageable) {
        Collection<Comment> comments;
        if (sort.equalsIgnoreCase("desc")) {
            comments = repository.findAll(pageable).stream()
                    .sorted(Comparator.comparing(Comment::getCreated, Comparator.reverseOrder()))
                    .collect(Collectors.toList());
        } else {
            comments = repository.findAll(pageable).stream()
                    .sorted(Comparator.comparing(Comment::getCreated))
                    .collect(Collectors.toList());
        }
        log.info("CommentServiceImpl: getAllSortedByCreatedDate — " +
                "получен список всех комментариев c сортировкой");
        return comments;
    }

    @Override
    public void removeComment(Long commentId, Long userId) {
        getCommentById(commentId, userId);
        repository.deleteById(commentId);
        log.info("CommentServiceImpl: removeComment — Комментарий с указанным id {} удалён", commentId);
    }

    @Override
    public Comment getCommentById(Long commentId) {
        Optional<Comment> commentOpt = repository.findById(commentId);
        Comment comment = commentOpt.orElseThrow(() -> {
            log.warn("CommentServiceImpl: getCommentById — Комментария с указанным id {} нет", commentId);
            throw new ObjectNotFountException("Комментария с указанным id " + commentId + " нет");
        });
        log.warn("CommentServiceImpl: getCommentById — Комментарий с указанным id {} получен", commentId);
        return comment;
    }

    @Override
    public Comment getCommentById(Long commentId, Long userId) {
        Optional<Comment> commentOpt = repository.findById(commentId);
        Comment comment = commentOpt.orElseThrow(() -> {
            log.warn("CommentServiceImpl: getCommentById — Комментария с указанным id {} нет", commentId);
            throw new ObjectNotFountException("Комментария с указанным id " + commentId + " нет");
        });
        if (!comment.getAuthor().getId().equals(userId)) {
            log.warn("CommentServiceImpl: getCommentById — пользователь с id {} не является автором комментария c id {}", userId, commentId);
            throw new ConflictException("Пользователь с  id " + userId + " не является автором комментария c id " + commentId);
        }
        log.warn("CommentServiceImpl: getCommentById — Комментарий с указанным id {} получен", commentId);
        return comment;
    }

    private void validateUsersRequestAndEvent(Long userId, Long eventId) {
        Optional<Request> requestOpt = Optional.ofNullable(requestService.getRequestByUserIdAndEventId(userId, eventId));
        Request request = requestOpt.orElseThrow(() -> {
            log.warn("CommentServiceImpl: validateUsersRequestAndEvent — Пользователь с id {} не оставлял заявку на участие в событии c id {}", userId, eventId);
            throw new ConflictException(String.format("Пользователь с id %d не оставлял заявку на участие в событии c id %d", userId, eventId));
        });

        if (!request.getStatus().equals(Status.CONFIRMED)) {
            log.error("CommentServiceImpl: validateUsersRequestAndEvent — пользователь не может оставить комментарий, если заявка на участие не подтверждена");
            throw new ConflictException("пользователь не может оставить комментарий, если заявка на участие не подтверждена");
        }
    }
}
