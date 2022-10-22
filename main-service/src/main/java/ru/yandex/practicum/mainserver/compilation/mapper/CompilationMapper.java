package ru.yandex.practicum.mainserver.compilation.mapper;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.mainserver.compilation.dto.CompilationDto;
import ru.yandex.practicum.mainserver.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.mainserver.compilation.model.Compilation;
import ru.yandex.practicum.mainserver.event.dto.EventShortDto;
import ru.yandex.practicum.mainserver.event.mapper.EventMapper;
import ru.yandex.practicum.mainserver.event.model.Event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
public class CompilationMapper {


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
