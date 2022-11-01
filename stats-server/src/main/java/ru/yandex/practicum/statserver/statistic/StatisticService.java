package ru.yandex.practicum.statserver.statistic;

import ru.yandex.practicum.statserver.statistic.dto.ViewStats;
import ru.yandex.practicum.statserver.statistic.model.Statistic;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * класс описывающий методы для работы со статистикой
 */
public interface StatisticService {

    // сохранение информации о том, что к эндпоинту был запрос
    Statistic addNewHit(Statistic statistic);

    // получение статистики по посещениям
    Collection<ViewStats> getStatistic(LocalDateTime start, LocalDateTime end, Collection<String> uris, Boolean unique);
}
