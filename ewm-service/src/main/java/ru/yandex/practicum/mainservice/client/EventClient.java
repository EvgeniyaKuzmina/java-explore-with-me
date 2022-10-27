package ru.yandex.practicum.mainservice.client;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class EventClient extends BaseClient {
    private static final String APP = "ewm-main-service";

    @Autowired
    public EventClient(@Value("${stats-service.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );

    }

    // сохранение информации о запросе в сервисе статистики
    public void addStatistic(EndpointHit endpointHit) {
        endpointHit.setApp(APP);
        post("/hit", endpointHit);
    }

    // получение статистики по просмотрам
    public Collection<ViewStats> getStatistic(String start, String end,
                                              Collection<String> uris,
                                              Boolean unique) {
        StringBuilder sb = new StringBuilder();
        uris.forEach(u -> sb.append("&uris=").append(u));
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "unique", unique,
                "uris", sb
        );

        ResponseEntity<Object> response = get("/stats?start=" + start + "&end=" + end + "&unique" + unique + sb, parameters);

        return parseResponseEntityToViewStats(response);


    }

    private Collection<ViewStats> parseResponseEntityToViewStats(ResponseEntity<Object> response) {
        Gson gson = new Gson();
        StringBuilder sb = new StringBuilder(Objects.requireNonNull(response.getBody()).toString());

        sb.delete(sb.length() - 2, sb.length());
        sb.deleteCharAt(0);

        String[] arr = sb.toString().split("}, ");

        Collection<ViewStats> viewStats = new ArrayList<>();
        for (String a : arr) {
            viewStats.add(gson.fromJson(a + "}", ViewStats.class));
        }
        return viewStats;
    }


}
