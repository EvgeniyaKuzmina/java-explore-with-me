package ru.yandex.practicum.statserver.statistic.dto;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ViewStats {
    private String app;
    private String uri;
    private Long hits;
}
