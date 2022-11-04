package ru.yandex.practicum.mainservice.event.comment;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.mainservice.event.comment.model.Comment;
import ru.yandex.practicum.mainservice.status.Status;

import java.util.Collection;

/**
 * класс описывающий методы для работы с комментариями
 */
public interface CommentService {

    /**
     * добавление нового комментария
     */
    Comment addNewComment(Comment comment, Long eventId, Long userId);

    /**
     * изменение комментария создателем
     */
    Comment changeCommentByAuthor(Comment comment, Long userId);

    /**
     * изменение статуса комментария админом
     */
    Comment changeStatusForCommentByAdmin(Long commentId, Status status);

    /**
     * получение списка комментариев с указанным статусом и по id события отсортированные по дате создания от более раннего к более позднему, с пагинацией
     */
    Collection<Comment> findPublishedByEventIdWithPagination(Long eventId, Pageable pageable);

    /**
     * получение списка комментариев по id создателя, отсортированные по дате создания от более раннего к более позднему, с пагинацией
     */
    Collection<Comment> findAllByAuthorId(Long eventId, Pageable pageable);

    /**
     * получение списка комментариев статусу и по id создателя, отсортированные по дате создания от более раннего к более позднему, с пагинацией
     */
    Collection<Comment> findAllByAuthorIdAndStatus(Long authorId, Status status, Pageable pageable);

    /**
     * получение списка комментариев по статусу, отсортированные по дате создания от более раннего к более позднему, с пагинацией
     */
    Collection<Comment> findByStatusIdSortedByCreatedDate(Status status, String sort, Pageable pageable);

    /**
     * получение списка комментариев, отсортированные по дате с пагинацией
     */
    Collection<Comment> findAllSortedByCreatedDate(String sort, Pageable pageable);

    /**
     * получение списка опубликованных комментариев по id события отсортированные по дате создания от более раннего к более позднему
     */
    Collection<Comment> findPublishedByEventId(Long eventId);

    /**
     * получение списка опубликованных комментариев по id событий отсортированные по дате создания от более раннего к более позднему
     */
    Collection<Comment> findPublishedByListEventId(Collection<Long> eventId);

    /**
     * удаление комментария
     */
    void removeComment(Long commentId, Long userId);

    /**
     * получение комментария создателем по id
     */
    Comment getCommentById(Long commentId, Long userId);

    /**
     * получение комментария по id
     */
    Comment getCommentById(Long commentId);
}
