package ru.yandex.practicum.mainservice.client;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class EndpointHit {

    private String app;
    private String uri;
    private String ip;
    private String timestamp;


}
