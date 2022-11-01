package ru.yandex.practicum.mainservice.compilation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.mainservice.compilation.CompilationService;
import ru.yandex.practicum.mainservice.compilation.CompilationServiceImpl;
import ru.yandex.practicum.mainservice.compilation.dto.CompilationDto;
import ru.yandex.practicum.mainservice.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.mainservice.compilation.mapper.CompilationMapper;
import ru.yandex.practicum.mainservice.compilation.model.Compilation;
import ru.yandex.practicum.mainservice.event.EventService;
import ru.yandex.practicum.mainservice.event.model.Event;

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

    @PostMapping
    public CompilationDto createCompilation(@Valid @RequestBody NewCompilationDto compilationDto) {
        log.info("CompilationAdminController: createCompilation — получен запрос на создание новой подборки событий");
        Collection<Event> events = new ArrayList<>();
        compilationDto.getEvents().forEach(id -> events.add(eventService.getEventById(id)));
        Compilation compilation = CompilationMapper.toCompilationFromNewCompilationDto(compilationDto, events);
        return CompilationMapper.toCompilationDto(service.createCompilation(compilation));
    }

    @PatchMapping(value = {"{compId}/events/{eventId}"})
    public CompilationDto addEventToCompilation(@Valid @PathVariable Long compId, @PathVariable Long eventId) {
        log.info("CompilationAdminController: addEventToCompilation — получен запрос на добавление события в подборку событий");
        Compilation compilation = service.addEventToCompilation(eventId, compId);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @DeleteMapping(value = {"{compId}/events/{eventId}"})
    public CompilationDto deleteEventFromCompilation(@Valid @PathVariable Long compId, @PathVariable Long eventId) {
        log.info("CompilationAdminController: deleteEventFromCompilation — получен запрос на удаление события из подборки");
        Compilation compilation = service.deleteEventFromCompilation(eventId, compId);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @PatchMapping(value = {"{compId}/pin"})
    public CompilationDto pinCompilation(@Valid @PathVariable Long compId) {
        log.info("CompilationAdminController: pinCompilation — получен запрос на закрепление подборки на главной странице");
        Compilation compilation = service.pinCompilation(true, compId);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @DeleteMapping(value = {"{compId}/pin"})
    public CompilationDto unpinCompilation(@Valid @PathVariable Long compId) {
        log.info("CompilationAdminController: unpinCompilation — получен запрос на открепление подборки на главной странице");
        Compilation compilation = service.unpinCompilation(false, compId);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @DeleteMapping(value = {"/{id}"})
    public void removeCompilation(@PathVariable Long id) {
        log.info("CompilationAdminController: removeCompilation — получен запрос на удаление подборки");
        service.removeCompilation(id);
    }
}
