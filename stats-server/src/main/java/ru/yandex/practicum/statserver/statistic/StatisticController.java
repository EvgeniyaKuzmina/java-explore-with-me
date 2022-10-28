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

    // сохранение истории просмотра
    @PostMapping(value = "/hit")
    public void addStatistic(@Valid @RequestBody EndpointHit endpointHit) {
        Statistic statistic = StatisticMapper.toStatistic(endpointHit);
        service.addNewHit(statistic);

    }

    // получение истории просмотров
    @GetMapping(value = "/stats")
    public Collection<ViewStats> getUserById(@RequestParam String start, @RequestParam String end,
                                             @RequestParam(required = false) Collection<String> uris,
                                             @RequestParam(defaultValue = "false") Boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDateTime = LocalDateTime.parse(start, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(end, formatter);
        return service.getStatistic(startDateTime, endDateTime, uris, unique);
    }
}
