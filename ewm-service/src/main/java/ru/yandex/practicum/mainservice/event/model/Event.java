package ru.yandex.practicum.mainservice.event.model;

import lombok.*;
import ru.yandex.practicum.mainservice.category.model.Category;
import ru.yandex.practicum.mainservice.event.location.Location;
import ru.yandex.practicum.mainservice.status.Status;
import ru.yandex.practicum.mainservice.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Set;


/**
 * класс для работы с событием
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(name = "title", nullable = false)
    private String title;
    @NotNull
    @Column(name = "description", nullable = false)
    private String description;
    @NotNull
    @Column(name = "annotation", nullable = false)
    private String annotation;
    @JoinColumn(name = "state")
    @Enumerated(EnumType.STRING)
    private Status state;
    @Column(name = "paid", nullable = false)
    private Boolean paid;
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    @NotNull
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    @Column(name = "views")
    @Builder.Default
    private Long views = 0L;
    @OneToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;
    @OneToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @ElementCollection
    @CollectionTable(name = "comments", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "text")
    private Set<String> comments;
    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;
    @Column(name = "confirmed_requests")
    @Builder.Default
    private Integer confirmedRequest = 0;
    @Column(name = "participant_limit", nullable = false)
    private Integer participantLimit;
    @Column(name = "request_moderation", nullable = false)
    private Boolean requestModeration;

    public void setEventDate(String eventDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.eventDate = LocalDateTime.parse(eventDate, formatter);
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return id.equals(event.id) &&
                title.equals(event.title) &&
                description.equals(event.description) &&
                annotation.equals(event.annotation) &&
                initiator.equals(event.initiator) &&
                category.equals(event.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, annotation, initiator, category);
    }
}
