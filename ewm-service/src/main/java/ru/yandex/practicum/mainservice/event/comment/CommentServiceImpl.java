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
        comment = repository.save(comment);
        log.info("CommentServiceImpl: addNewComment — New comment added {}.", comment);
        return comment;
    }

    @Override
    public Comment changeCommentByAuthor(Comment updComment, Long userId) {
        Comment comment = getCommentById(updComment.getId(), userId);
        validateUsersRequestAndEvent(userId, comment.getEvent().getId());
        comment.setText(updComment.getText());
        comment.setStatus(Status.PENDING);
        comment = repository.save(comment);
        log.info("CommentServiceImpl: changeCommentByAuthor — Comment was changed {}.", comment);
        return comment;
    }

    @Override
    public Comment changeStatusForCommentByAdmin(Long commentId, Status status) {
        Comment comment = getCommentById(commentId);
        comment.setStatus(status);
        comment = repository.save(comment);
        log.info("CommentServiceImpl: changeStatusForCommentByAdmin — Comment's status was changed {}.", comment);
        return comment;
    }

    @Override
    public Collection<Comment> getPublishedByEventIdWithPagination(Long eventId, Pageable pageable) {
        Collection<Comment> comments = repository.findByEventIdAndStatusOrderByCreatedDesc(eventId, Status.PUBLISHED, pageable).toList();
        log.info("CommentServiceImpl: getPublishedByEventIdWithPagination — Received list of comments with required status");
        return comments;
    }

    @Override
    public Collection<Comment> getPublishedByEventId(Long eventId) {
        Collection<Comment> comments = repository.findByEventIdAndStatusOrderByCreatedDesc(eventId, Status.PUBLISHED);
        log.info("CommentServiceImpl: getPublishedByEventId — " +
                "Received list of published comments to event with id {}", eventId);
        return comments;
    }

    @Override
    public Collection<Comment> getPublishedByListEventId(Collection<Long> eventId) {
        Collection<Comment> comments = repository.findByEventIdInAndStatusOrderByCreatedDesc(eventId, Status.PUBLISHED);
        log.info("CommentServiceImpl: getPublishedByListEventId — " +
                "Received list of published comments to events with ids {}", eventId);
        return comments;
    }

    @Override
    public Collection<Comment> getAllByAuthorId(Long authorId, Pageable pageable) {
        Collection<Comment> comments = repository.findByAuthorIdOrderByCreatedDesc(authorId, pageable).toList();
        log.info("CommentServiceImpl: getAllByAuthorId — Received list of all user's comments");
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
                "Received list of all user's comments with required status");
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
                "Received list of all comments with required status");
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
                "Received list of all comments with sorting");
        return comments;
    }

    @Override
    public void removeComment(Long commentId, Long userId) {
        getCommentById(commentId, userId);
        repository.deleteById(commentId);
        log.info("CommentServiceImpl: removeComment — Comment with id {} was deleted", commentId);
    }

    @Override
    public Comment getCommentById(Long commentId) {
        Optional<Comment> commentOpt = repository.findById(commentId);
        Comment comment = commentOpt.orElseThrow(() -> {
            log.warn("CommentServiceImpl: getCommentById — Comment with id {} does  not exist", commentId);
            throw new ObjectNotFountException("Comment with id " + commentId + " does  not exist");
        });
        log.warn("CommentServiceImpl: getCommentById — Comment with id {} was received", commentId);
        return comment;
    }

    @Override
    public Comment getCommentById(Long commentId, Long userId) {
        Optional<Comment> commentOpt = repository.findById(commentId);
        Comment comment = commentOpt.orElseThrow(() -> {
            log.warn("CommentServiceImpl: getCommentById — Comment with id {} does  not exist", commentId);
            throw new ObjectNotFountException("Comment with id " + commentId + " does  not exist");
        });
        if (!comment.getAuthor().getId().equals(userId)) {
            log.warn("CommentServiceImpl: getCommentById — User with id {} does not author of comment with id {}",
                    userId, commentId);
            throw new ConflictException("User with  id " + userId + " does not author of comment with id " + commentId);
        }
        log.warn("CommentServiceImpl: getCommentById — Comment with id {} was received", commentId);
        return comment;
    }

    private void validateUsersRequestAndEvent(Long userId, Long eventId) {
        Optional<Request> requestOpt = Optional.ofNullable(requestService.getRequestByUserIdAndEventId(userId, eventId));
        Request request = requestOpt.orElseThrow(() -> {
            log.warn("CommentServiceImpl: validateUsersRequestAndEvent — " +
                    "User with id {} does not submit application for participation in event with id {}", userId, eventId);
            throw new ConflictException(String.format("User with id %d does not submit application " +
                    "for participation in event with id %d", userId, eventId));
        });

        if (!request.getStatus().equals(Status.CONFIRMED)) {
            log.error("CommentServiceImpl: validateUsersRequestAndEvent — " +
                    "User cannot put comment if application for participation was not confirmed");
            throw new ConflictException("User cannot put comment if application for participation was not confirmed");
        }
    }
}
