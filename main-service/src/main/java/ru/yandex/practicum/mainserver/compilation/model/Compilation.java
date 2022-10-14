package ru.yandex.practicum.mainserver.compilation.model;

import lombok.*;
import ru.yandex.practicum.mainserver.event.model.Event;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * класс для работы с подборками событий
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "compilations_events", schema = "public")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(name = "title")
    private String title;
    @NotNull
    @Column(name = "pinned")
    private Boolean pinned;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
}
