package ru.yandex.practicum.mainservice.client;

import lombok.*;

/**
 * класс DTO для работы получением статистики просмотров
 */
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
