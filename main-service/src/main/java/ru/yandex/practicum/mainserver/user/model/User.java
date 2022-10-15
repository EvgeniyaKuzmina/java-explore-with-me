package ru.yandex.practicum.mainserver.user.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
/**
 * класс для работы с пользователя
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "users", schema = "public")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @NotNull
    @Column(name = "name",  nullable = false)
    private String name;
    @Email
    @NotNull
    @Column(name = "email",  nullable = false)
    private String email;
    @ElementCollection
    @CollectionTable(name = "compilation_events", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "text")
    private List<Long> compilationId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}