package ru.yandex.practicum.mainservice.event.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.mainservice.event.comment.model.Comment;
import ru.yandex.practicum.mainservice.status.Status;

import java.util.Collection;

/**
 * класс репозиторий для работы с БД комментариев
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * получение списка комментариев по id создателя с указанным статусом, отсортированные по дате создания по убыванию с пагинацией
     */
    Page<Comment> findByAuthorIdAndStatusOrderByCreatedDesc(Long authorId, Status status, Pageable pageable);

    /**
     * получение списка комментариев по id создателя с указанным статусом, отсортированные по дате создания по возрастанию с пагинацией
     */
    Page<Comment> findByAuthorIdAndStatusOrderByCreatedAsc(Long authorId, Status status, Pageable pageable);

     /**
     * получение списка комментариев по указанному статусу и по id события, отсортированные по дате создания по убыванию
     */
    Collection<Comment> findByEventIdAndStatusOrderByCreatedDesc(Long eventId, Status status);

    /**
     * получение списка комментариев по указанному статусу и по id событий, отсортированные по дате создания по убыванию
     */
    Collection<Comment> findByEventIdInAndStatusOrderByCreatedDesc(Collection<Long> eventId, Status status);

    /**
     * получение списка комментариев по указанному статусу и по id события, отсортированные по дате создания по убыванию с пагинацией
     */
    Page<Comment> findByEventIdAndStatusOrderByCreatedDesc(Long eventId, Status status, Pageable pageable);

    /**
     * получение списка комментариев по id создателя, отсортированные по дате создания по убыванию с пагинацией
     */
    Page<Comment> findByAuthorIdOrderByCreatedDesc(Long authorId, Pageable pageable);

    /**
     * получение списка комментариев по указанному статусу, отсортированные по дате создания по убыванию
     */
    Page<Comment> findByStatusOrderByCreatedDesc(Status status, Pageable pageable);

    /**
     * получение списка комментариев по указанному статусу, отсортированные по дате создания по увеличению
     */
    Page<Comment> findByStatusOrderByCreatedAsc(Status status, Pageable pageable);

    /**
     * получение списка комментариев с пагинацией
     */
    Page<Comment> findAll(Pageable pageable);
}
