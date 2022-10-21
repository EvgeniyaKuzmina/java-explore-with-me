package ru.yandex.practicum.mainserver.compilation.model;

import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;
import ru.yandex.practicum.mainserver.event.model.Event;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

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
@Table(name = "compilations", schema = "public")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(name = "title",  nullable = false)
    private String title;
    @Column(name = "pinned")
    private Boolean pinned;
    @NotNull
    @Column(name = "compilation_event_id")
    private Long compilationEventId;
}
