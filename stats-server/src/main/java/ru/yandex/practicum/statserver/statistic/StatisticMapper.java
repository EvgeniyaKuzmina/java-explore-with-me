package ru.yandex.practicum.statserver.statistic;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.statserver.statistic.dto.EndpointHit;
import ru.yandex.practicum.statserver.statistic.model.Statistic;

@Slf4j
public class StatisticMapper {

    public static Statistic toStatistic(EndpointHit endpointHit) {
        Statistic statistic = Statistic.builder()
                .app(endpointHit.getApp())
                .ip(endpointHit.getIp())
                .uri(endpointHit.getUri())
                .build();
        statistic.setTimestamp(endpointHit.getTimestamp());
        return statistic;
    }
}
