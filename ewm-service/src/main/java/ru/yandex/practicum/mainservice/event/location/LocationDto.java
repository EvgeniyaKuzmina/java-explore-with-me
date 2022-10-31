package ru.yandex.practicum.mainservice.event.location;

import lombok.Builder;
import lombok.Data;

/**
 * класс DTO для местоположения
 */
@Data
@Builder
public class LocationDto {
    private Float lat;
    private Float lon;
}
