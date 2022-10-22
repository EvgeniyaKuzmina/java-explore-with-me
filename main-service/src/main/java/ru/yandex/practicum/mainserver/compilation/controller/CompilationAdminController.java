package ru.yandex.practicum.mainserver.compilation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.mainserver.compilation.CompilationService;
import ru.yandex.practicum.mainserver.compilation.CompilationServiceImpl;
import ru.yandex.practicum.mainserver.compilation.dto.CompilationDto;
import ru.yandex.practicum.mainserver.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.mainserver.compilation.mapper.CompilationMapper;
import ru.yandex.practicum.mainserver.compilation.model.Compilation;
import ru.yandex.practicum.mainserver.event.EventService;
import ru.yandex.practicum.mainserver.event.model.Event;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;

/**
 * класс контроллер для работы с API подборками событий
 */

@RestController
@RequestMapping(path = "/admin/compilations")
@Slf4j
public class CompilationAdminController {

    private final CompilationService service;

    private final EventService eventService;

    @Autowired
    public CompilationAdminController(CompilationServiceImpl service, EventService eventService) {
        this.service = service;
        this.eventService = eventService;
    }

    // создание подборки
    @PostMapping
    public CompilationDto createCompilation(@Valid @RequestBody NewCompilationDto compilationDto) {
        Collection<Event> events = new ArrayList<>();
        compilationDto.getEvents().forEach(id -> events.add(eventService.getEventById(id)));
        Compilation compilation = CompilationMapper.toCompilationFromNewCompilationDto(compilationDto, events);
        return CompilationMapper.toCompilationDto(service.createCompilation(compilation));
    }

    // добавить событие в подборку
    @PatchMapping(value = {"{compId}/events/{eventId}"})
    public CompilationDto addEventToCompilation(@Valid @PathVariable Long compId, @PathVariable Long eventId) {
        Compilation compilation = service.addEventToCompilation(eventId, compId);
        return CompilationMapper.toCompilationDto(compilation);
    }

    // удалить событие из подборки
    @DeleteMapping(value = {"{compId}/events/{eventId}"})
    public CompilationDto deleteEventFromCompilation(@Valid @PathVariable Long compId, @PathVariable Long eventId) {
        Compilation compilation = service.deleteEventFromCompilation(eventId, compId);
        return CompilationMapper.toCompilationDto(compilation);
    }

    // закрепить подборку на главной странице
    @PatchMapping(value = {"{compId}/pin"})
    public CompilationDto pinCompilation(@Valid @PathVariable Long compId) {
        Compilation compilation = service.pinCompilation(true, compId);
        return CompilationMapper.toCompilationDto(compilation);
    }

    // удалить подборку с главной страницы
    @DeleteMapping(value = {"{compId}/pin"})
    public CompilationDto unpinCompilation(@Valid @PathVariable Long compId) {
        Compilation compilation = service.unpinCompilation(false, compId);
        return CompilationMapper.toCompilationDto(compilation);
    }

    // удаление подборки по eventId
    @DeleteMapping(value = {"/{id}"})
    public void removeCompilation(@PathVariable Long id) {
        service.removeCompilation(id);
    }



}
