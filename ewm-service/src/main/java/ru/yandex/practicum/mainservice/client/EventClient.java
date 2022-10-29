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

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class EventClient extends BaseClient {
    private static final String APP = "ewm-main-service";

    @Autowired
    public EventClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );

    }

    // сохранение информации о запросе в сервисе статистики
    public void addStatistic(HttpServletRequest request) {
        EndpointHit endpointHit = EndpointHit.builder()
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(encodingTime())
                .app(APP)
                .build();
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

    private String encodingTime() {
        LocalDateTime timestamp = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return timestamp.format(formatter);
    }

    private Collection<ViewStats> parseResponseEntityToViewStats(ResponseEntity<Object> response) {
        Gson gson = new Gson();
        String responseString = Objects.requireNonNull(response.getBody()).toString().replace('/', '|');
        Collection<ViewStats> viewStats = new ArrayList<>();

        StringBuilder sb = new StringBuilder(responseString);
        if (sb.length() > 2) {
            sb.delete(sb.length() - 2, sb.length());
            sb.deleteCharAt(0);
            String[] arr = sb.toString().split("}, ");
            for (String a : arr) {
                viewStats.add(gson.fromJson(a + "}", ViewStats.class));
            }
        }
        viewStats.forEach(vs -> vs.setUri(vs.getUri().replace('|', '/')));
        return viewStats;
    }


}
