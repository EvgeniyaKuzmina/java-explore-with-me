package ru.yandex.practicum.mainserver.event.comment;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.mainserver.event.comment.model.Comment;

import java.util.Collection;

/**
 * класс описывающий методы для работы с комментариями
 */

public interface CommentService {

    // добавление нового комментария
    Comment addNewComment(Comment comment, Long eventId, Long userId);

    // изменение комментария пользователем
    Comment changeCommentByUser(Comment comment, Long eventId, Long userId);

    // получение списка комментариев по id события отсортированные по дате создания от более раннего к более позднему
    Collection<Comment> findAllByEventIdOrderByCreatDesc(Long eventId, Pageable pageable);

    Collection<Comment> findAllByEventIdOrderByCreatDesc(Long eventId);

    // удаление комментария
    void removeComment(Long commentId);

    // получение комментария по id
    Comment getCommentById(Long commentId);
}
