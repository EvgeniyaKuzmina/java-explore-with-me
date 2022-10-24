package ru.yandex.practicum.mainserver.event.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.mainserver.event.EventService;
import ru.yandex.practicum.mainserver.event.comment.model.Comment;
import ru.yandex.practicum.mainserver.exception.ConflictException;
import ru.yandex.practicum.mainserver.exception.ObjectNotFountException;
import ru.yandex.practicum.mainserver.request.RequestService;
import ru.yandex.practicum.mainserver.request.model.Request;
import ru.yandex.practicum.mainserver.status.Status;
import ru.yandex.practicum.mainserver.user.UserService;

import java.time.LocalDateTime;
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
        try {
            log.info("CommentServiceImpl: addNewComment — Комментарий добавлен {}.", comment);
            return repository.save(comment);
        } catch (DataIntegrityViolationException e) {
            log.error("CommentServiceImpl: addNewComment — Произошла ошибка при сохранении данных");
            throw new RuntimeException("Произошла ошибка при сохранении данных");
        }
    }

    @Override
    public Comment changeCommentByUser(Comment updComment, Long eventId, Long userId) {
        validateUsersRequestAndEvent(userId, eventId);
        Comment comment = getCommentById(updComment.getId());
        comment.setText(updComment.getText());
        try {
            log.info("CommentServiceImpl: addNewComment — Комментарий изменён {}.", comment);
            return repository.save(comment);
        } catch (DataIntegrityViolationException e) {
            log.error("CommentServiceImpl: addNewComment — Произошла ошибка при сохранении данных");
            throw new RuntimeException("Произошла ошибка при сохранении данных");
        }
    }

    @Override
    public Collection<Comment> findAllByEventIdOrderByCreatDesc(Long eventId, Pageable pageable) {
        return repository.findByAuthorIdOrderByCreatedDesc(eventId, pageable).toList();
    }

    @Override
    public Collection<Comment> findAllByEventIdOrderByCreatDesc(Long eventId) {
        return repository.findByAuthorIdOrderByCreatedDesc(eventId);
    }

    @Override
    public void removeComment(Long commentId) {
        getCommentById(commentId); // проверка, что комментарий с указанным id есть
        log.info("CommentServiceImpl: removeComment — Комментарий с указанным id {} удалён", commentId);
        repository.deleteById(commentId);

    }

    @Override
    public Comment getCommentById(Long commentId) {
        Optional<Comment> comment = repository.findById(commentId);
        comment.orElseThrow(() -> {
            log.warn("CommentServiceImpl: getCommentById — Комментария с указанным id {} нет", commentId);
            return new ObjectNotFountException("Комментария с указанным id " + commentId + " нет");
        });

        log.warn("CommentServiceImpl: getCommentById — Комментарий с указанным id {} получен", commentId);
        return comment.get();
    }

    private void validateUsersRequestAndEvent(Long userId, Long eventId) {
        Collection<Request> request = requestService.getAllRequestsByUserId(userId);
        boolean check = false;
        for (Request r : request) {
            if (r.getEvent().getId().equals(eventId) && r.getStatus().equals(Status.CONFIRMED)) {
                if(r.getEvent().getEventDate().isBefore(LocalDateTime.now())) {
                    check = true;
                    break;
                }
            }
        }
        if (!check) {
            log.error("CommentServiceImpl: validateUserAndEvent — Пользователь с id {} не участвовал в событии c id или событие ещё не состоялось {}", userId, eventId);
            throw new ConflictException(String.format("Пользователь с id %d не участвовал в событии c id %d или событие ещё не состоялось", userId, eventId));
        }

    }
}
