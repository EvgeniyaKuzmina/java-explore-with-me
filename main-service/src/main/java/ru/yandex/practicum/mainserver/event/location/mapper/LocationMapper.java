package ru.yandex.practicum.mainserver.event.location.mapper;

import ru.yandex.practicum.mainserver.event.location.Location;
import ru.yandex.practicum.mainserver.event.location.LocationDto;

public class LocationMapper {
    public static LocationDto toLocationDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }

    public static Location toLocation(LocationDto locationDto) {
        return Location.builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .build();
    }
}
