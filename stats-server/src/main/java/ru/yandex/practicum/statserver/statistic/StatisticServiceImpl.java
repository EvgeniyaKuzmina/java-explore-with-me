package ru.yandex.practicum.statserver.statistic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.statserver.statistic.dto.ViewStats;
import ru.yandex.practicum.statserver.statistic.model.Statistic;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * класс реализующий методы для работы со статистикой
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

    private final StatisticRepository repository;

    @Override
    public Statistic addNewHit(Statistic statistic) {
        log.info("StatisticServiceImpl: addNewHit — информация о просмотре добавлена");
        return repository.save(statistic);
    }

    @Override
    public Collection<ViewStats> getStatistic(LocalDateTime start, LocalDateTime end, Collection<String> uris, Boolean unique) {
        if (Boolean.TRUE.equals(unique) && uris != null) {
            return repository.findDistinctIpTimestampBetweenAndUris(uris, start, end);
        }  else if (Boolean.TRUE.equals(unique)) {
            return repository.findDistinctIpTimestampBetween(start, end);
        } else if (uris != null) {
            return repository.findTimestampBetweenAndUris(uris, start, end);
        }  else {
            return repository.findTimestampBetween(start, end);
        }
    }
}
