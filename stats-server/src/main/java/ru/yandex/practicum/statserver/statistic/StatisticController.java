package ru.yandex.practicum.statserver.statistic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.statserver.statistic.dto.EndpointHit;
import ru.yandex.practicum.statserver.statistic.dto.ViewStats;
import ru.yandex.practicum.statserver.statistic.model.Statistic;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

@RestController
@RequestMapping
@Slf4j
public class StatisticController {

    private final StatisticService service;

    @Autowired
    public StatisticController(StatisticService service) {
        this.service = service;
    }

    @PostMapping(value = "/hit")
    public void addStatistic(@Valid @RequestBody EndpointHit endpointHit) {
        log.info("StatisticController: addStatistic — получен запрос на сохранение статистики");
        Statistic statistic = StatisticMapper.toStatistic(endpointHit);
        service.addNewHit(statistic);
    }

    @GetMapping(value = "/stats")
    public Collection<ViewStats> getStatistic(@RequestParam String start, @RequestParam String end,
                                              @RequestParam(required = false) Collection<String> uris,
                                              @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("StatisticController: getStatistic — получен запрос на получение статистики");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDateTime = LocalDateTime.parse(start, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(end, formatter);
        return service.getStatistic(startDateTime, endDateTime, uris, unique);
    }
}
