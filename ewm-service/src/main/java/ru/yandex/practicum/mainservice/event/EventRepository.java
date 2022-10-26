package ru.yandex.practicum.mainservice.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.mainservice.event.model.Event;

/**
 * класс репозиторий для работы с БД событий
 */
public interface EventRepository extends JpaRepository<Event, Long> {

    // получение списка событие с пагинацией
    Page<Event> findByInitiatorId(Long userId, Pageable pageable);

    // получение списка событий по указанным параметрам

    Page<Event> findAll(Specification<Event> specification, Pageable pageable);


}
