package ru.yandex.practicum.mainserver.compilation.mapper;

import ru.yandex.practicum.mainserver.compilation.dto.CompilationDto;
import ru.yandex.practicum.mainserver.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.mainserver.compilation.model.Compilation;

import java.util.ArrayList;
import java.util.List;

public class CompilationMapper {


    public static CompilationDto toCompilationDto(Compilation compilation) {
        //List<Event> events = new ArrayList<>();
       // compilation.getEventsId().forEach(e -> events.add(e.getId()));
        return CompilationDto.builder()
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                //.event(compilation)
                .build();
    }


    public static Compilation toCompilation(CompilationDto compilationDto) {
        List<Long> events = new ArrayList<>();
        compilationDto.getEvents().forEach(e -> events.add(e.getId()));
        return Compilation.builder()
                .title(compilationDto.getTitle())
                .pinned(compilationDto.getPinned())
                .eventsId(events)
                .build();
    }

    public static Compilation toCompilationFromNewCompilationDto(NewCompilationDto compilationDto) {
        return Compilation.builder()
                .title(compilationDto.getTitle())
                .pinned(compilationDto.getPinned())
                .eventsId(compilationDto.getEvents())
                .build();
    }
}
