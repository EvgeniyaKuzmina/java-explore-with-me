package ru.yandex.practicum.statserver.statistic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.statserver.statistic.dto.ViewStats;
import ru.yandex.practicum.statserver.statistic.model.Statistic;

import java.time.LocalDateTime;
import java.util.Collection;

public interface StatisticRepository extends JpaRepository<Statistic, Long> {

    // получение информации о просмотрах с уникальным ip
    @Query("select new ru.yandex.practicum.statserver.statistic.dto.ViewStats(s.app, s.uri, count(distinct s.ip)) " +
            "from Statistic as s " +
            "where s.uri in (?1) and s.timestamp between ?2 and ?3 " +
            "group by s.uri, s.app")
    Collection<ViewStats> findDistinctIpTimestampBetweenAndUris(Collection<String> uris, LocalDateTime start, LocalDateTime end);

    // получение информации о просмотрах с уникальным ip без уточнения uris

    @Query("select  new ru.yandex.practicum.statserver.statistic.dto.ViewStats(s.app, s.uri, count(distinct s.ip)) " +
            "from Statistic as s " +
            "where s.timestamp between ?1 and ?2 " +
            "group by s.uri, s.app")
    Collection<ViewStats> findDistinctIpTimestampBetween(LocalDateTime start, LocalDateTime end);

    // получение информации о просмотрах
    @Query("select new ru.yandex.practicum.statserver.statistic.dto.ViewStats(s.app, s.uri, count(s.ip)) " +
            "from Statistic as s " +
            "where s.uri in (?1) and s.timestamp between ?2 and ?3 " +
            "group by s.uri, s.app")
    Collection<ViewStats> findTimestampBetweenAndUris(Collection<String> uris, LocalDateTime start, LocalDateTime end);

    // получение информации о просмотрах без уточнения uris
    @Query("select new ru.yandex.practicum.statserver.statistic.dto.ViewStats(s.app, s.uri, count(s.ip)) " +
            "from Statistic as s " +
            "where s.timestamp between ?1 and ?2 " +
            "group by s.uri, s.app")
    Collection<ViewStats> findTimestampBetween(LocalDateTime start, LocalDateTime end);
}
