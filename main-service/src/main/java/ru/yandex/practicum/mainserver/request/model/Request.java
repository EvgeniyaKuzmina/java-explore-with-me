package ru.yandex.practicum.mainserver.request.model;

import lombok.*;
import ru.yandex.practicum.mainserver.event.model.Event;
import ru.yandex.practicum.mainserver.status.Status;
import ru.yandex.practicum.mainserver.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * класс для работы с заявками на участие в мероприятии
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "requests", schema = "public")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;
    @ManyToOne()
    @JoinColumn(name = "requester_id")
    private User requester;
    @NotNull
    @Column(name = "created", nullable = false)
    private LocalDateTime created;
    @ManyToOne()
    @JoinColumn(name = "event_id")
    private Event event;

}
