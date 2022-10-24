package ru.yandex.practicum.mainserver.event.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.mainserver.event.comment.model.Comment;

import java.util.Collection;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // получение списка комментариев по id создателя комментария, отсортированные по дате создания по убыванию
    Collection<Comment> findByAuthorIdOrderByCreatedDesc(Long itemId);

    // получение списка комментариев по id создателя комментария, отсортированные по дате создания по убыванию с пагинацией
    Page<Comment> findByAuthorIdOrderByCreatedDesc(Long itemId, Pageable pageable);
}
