package ru.yandex.practicum.mainserver.event.comment.model;

import lombok.*;
import ru.yandex.practicum.mainserver.event.model.Event;
import ru.yandex.practicum.mainserver.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

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
@Table(name = "comments", schema = "public")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(name = "text", nullable = false)
    private String text;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
    @Column(name = "created", nullable = false)
    private LocalDateTime creat;

}
