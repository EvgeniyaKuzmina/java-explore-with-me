package ru.yandex.practicum.statserver.statistic.dto;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class EndpointHit {

    private Long id;
    private String app;
    private String uri;
    private String ip;
    private String timestamp;

}
