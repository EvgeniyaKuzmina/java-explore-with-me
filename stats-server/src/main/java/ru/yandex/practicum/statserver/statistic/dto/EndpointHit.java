package ru.yandex.practicum.statserver.statistic.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class EndpointHit {

    private Long id;
    @Length(max = 20)
    private String app;
    @Length(max = 1000)
    private String uri;
    @Length(max = 1000)
    private String ip;
    private String timestamp;
}
