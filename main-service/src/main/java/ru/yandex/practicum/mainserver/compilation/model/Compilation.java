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
    @ElementCollection
    @CollectionTable(name = "events", joinColumns = @JoinColumn(name = "compilations_id"))
    @Column(name = "id")
    private List<Long> eventsId;
}
