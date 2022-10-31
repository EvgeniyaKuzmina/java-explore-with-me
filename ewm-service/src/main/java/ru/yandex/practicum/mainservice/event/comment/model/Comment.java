package ru.yandex.practicum.mainservice.event.comment.model;

import lombok.*;
import ru.yandex.practicum.mainservice.event.model.Event;
import ru.yandex.practicum.mainservice.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * класс для работы с комментариями
 */

@Builder
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(name = "text", nullable = false)
    private String text;
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return text.equals(comment.text) && event.equals(comment.event) && author.equals(comment.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, event, author);
    }
}
