package ru.yandex.practicum.mainserver.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.mainserver.event.model.Event;
import ru.yandex.practicum.mainserver.status.Status;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * класс репозиторий для работы с БД событий
 */
public interface EventRepository extends JpaRepository<Event, Long> {

    // получение списка событие с пагинацией
    Page<Event> findByInitiatorId(Long userId, Pageable pageable);

    // получение события по id текущего пользователя
    Event findByInitiatorIdAndId(Long eventId, Long userId);

    // получение списка событий администратором по указанным параметрам
    Page<Event> findByInitiatorIdInAndStateInAndCategoryIdInAndEventDateBetween(Collection<Long> initiatorsId,
                                                                                Collection<Status> states,
                                                                                Collection<Long> categoryId,
                                                                                LocalDateTime rangeStart,
                                                                                LocalDateTime rangeEnd,
                                                                                Pageable pageable);
    /**/

    // получение списка событий (публичный запрос) по указанным параметрам

    //criteria

    //QueryDSL
    /*@Query("SELECT e FROM Event e WHERE " +
            "(UPPER (e.description) LIKE %:text% OR UPPER (e.annotation) LIKE %:text%) " +
            "AND e.confirmedRequests < e.participantLimit " +
            "AND e.category in :categoriesId  " +
            "AND e.paid = :paid  " +
            "AND (e.eventDate > :rangeStart AND e.eventDate < :rangeEnd)" +
            "AND e.state = 'PUBLISHED' " +
            "ORDER BY :sort")
    Page<Event> getAllEventsPublicAPI(EventParam param,
                                      Pageable pageable);*/

    Page<Event> findAll(Specification<Event> specification, Pageable pageable);
    /*@Query("SELECT e FROM Event e WHERE " +
            "UPPER (e.description) LIKE '%param.getText().toUpperCase()%' " +
            "OR UPPER (e.annotation) LIKE '%param.getText().toUpperCase()%' " +
    " ")*/
  /*  Page<Event> getAllEventPublicAPI(EventParam param,
                                     Pageable pageable);*/
    //SessionFactory sessionFactory = HibernateUtil.getSessionFactoryOptions();
    //Criteria criteria = session.createCriteria(Event.class);
    // Session session = HibernateUtil.getHibernateSession();
    // CriteriaBuilder cb = session.getCriteriaBuilder();


    // получение списка событий (публичный запрос) по указанным параметрам
    // (с датой проведения между указанными датами), только доступные для записи
    Page<Event> findByStateAndDescriptionContainingIgnoreCaseOrAnnotationContainingIgnoreCaseAndCategoryIdInAndPaidIsAndParticipantLimitLessThanAndEventDateBetween(
            Status state,
            String descriptionText,
            String annotationText,
            Collection<Long> categoryId,
            Boolean paid,
            Integer participantLimit,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Pageable pageable);

    // получение списка событий (публичный запрос) по указанным параметрам
    // (с датой проведения позднее указанной), только доступные для записи
    Page<Event> findByStateAndDescriptionContainingIgnoreCaseOrAnnotationContainingIgnoreCaseAndCategoryIdInAndPaidIsAndParticipantLimitLessThanAndEventDateAfter(
            Status state,
            String descriptionText,
            String annotationText,
            Collection<Long> categoryId,
            Boolean paid,
            Integer participantLimit,
            LocalDateTime rangeStart,
            Pageable pageable);

    // получение списка событий (публичный запрос) по указанным параметрам
    // (не только доступные для записи, с датой проведения между указанными датами)
    Page<Event> findByStateAndDescriptionContainingIgnoreCaseOrAnnotationContainingIgnoreCaseAndCategoryIdInAndPaidIsAndEventDateBetween(
            Status state,
            String descriptionText,
            String annotationText,
            Collection<Long> categoryId,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Pageable pageable);

    // получение списка событий (публичный запрос) по указанным параметрам
    // (не только доступные для записи, с датой проведения позднее указанной),
    Page<Event> findByStateAndDescriptionContainingIgnoreCaseOrAnnotationContainingIgnoreCaseAndCategoryIdInAndPaidIsAndEventDateAfter(
            Status state,
            String descriptionText,
            String annotationText,
            Collection<Long> categoryId,
            Boolean paid,
            LocalDateTime rangeStart,
            Pageable pageable);


}
