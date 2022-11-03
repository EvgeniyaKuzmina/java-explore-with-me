package ru.yandex.practicum.mainservice.compilation.mapper;

import ru.yandex.practicum.mainservice.compilation.dto.CompilationDto;
import ru.yandex.practicum.mainservice.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.mainservice.compilation.model.Compilation;
import ru.yandex.practicum.mainservice.event.dto.EventShortDto;
import ru.yandex.practicum.mainservice.event.mapper.EventMapper;
import ru.yandex.practicum.mainservice.event.model.Event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CompilationMapper {
    private CompilationMapper() {
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        List<EventShortDto> eventsDto = new ArrayList<>();
        compilation.getEvents().forEach(e -> eventsDto.add(EventMapper.toEventShortDto(e)));
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events(eventsDto)
                .build();
    }

    public static Compilation toCompilationFromNewCompilationDto(NewCompilationDto compilationDto, Collection<Event> events) {
        return Compilation.builder()
                .title(compilationDto.getTitle())
                .pinned(compilationDto.getPinned())
                .events(events)
                .build();
    }
}
