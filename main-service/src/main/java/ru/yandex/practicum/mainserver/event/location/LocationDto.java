package ru.yandex.practicum.mainserver.event.location;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;

/**
 * класс DTO для местоположения
 */

@Data
@Builder
public class LocationDto {

    private Float lat;
    private Float lon;
}
