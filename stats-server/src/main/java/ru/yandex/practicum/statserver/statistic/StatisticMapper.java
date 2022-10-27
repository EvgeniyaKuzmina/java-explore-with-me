package ru.yandex.practicum.statserver.statistic;

import ru.yandex.practicum.statserver.statistic.dto.EndpointHit;
import ru.yandex.practicum.statserver.statistic.model.Statistic;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class StatisticMapper {
    public static Statistic toStatistic(EndpointHit endpointHit) {
        Statistic statistic =  Statistic.builder()
                .app(endpointHit.getApp())
                .ip(endpointHit.getIp())
                .uri(endpointHit.getUri())
                .build();
        String timestamp = new String(endpointHit.getTimestamp().getBytes(), StandardCharsets.UTF_8);
        statistic.setTimestamp(timestamp);
        return statistic;
    }
}
