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
        log.info("CompilationAdminController: createCompilation — received request to create new compilation");
        Collection<Long> eventsId = new ArrayList<>(compilationDto.getEvents());
        Collection<Event> events = eventService.getEventByIdIn(eventsId);
        Compilation compilation = CompilationMapper.toCompilationFromNewCompilationDto(compilationDto, events);
        return CompilationMapper.toCompilationDto(service.createCompilation(compilation));
    }

    @PatchMapping(value = {"{compId}/events/{eventId}"})
    public CompilationDto addEventToCompilation(@Valid @PathVariable Long compId, @PathVariable Long eventId) {
        log.info("CompilationAdminController: addEventToCompilation — received request to add event to compilation");
        Compilation compilation = service.addEventToCompilation(eventId, compId);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @DeleteMapping(value = {"{compId}/events/{eventId}"})
    public CompilationDto deleteEventFromCompilation(@Valid @PathVariable Long compId, @PathVariable Long eventId) {
        log.info("CompilationAdminController: deleteEventFromCompilation — received request to delete event from compilation");
        Compilation compilation = service.deleteEventFromCompilation(eventId, compId);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @PatchMapping(value = {"{compId}/pin"})
    public CompilationDto pinCompilation(@Valid @PathVariable Long compId) {
        log.info("CompilationAdminController: pinCompilation — received request to pin compilation on the main page");
        Compilation compilation = service.pinCompilation(true, compId);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @DeleteMapping(value = {"{compId}/pin"})
    public CompilationDto unpinCompilation(@Valid @PathVariable Long compId) {
        log.info("CompilationAdminController: unpinCompilation — received request to unpin compilation on the main page");
        Compilation compilation = service.unpinCompilation(false, compId);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @DeleteMapping(value = {"/{id}"})
    public void removeCompilation(@PathVariable Long id) {
        log.info("CompilationAdminController: removeCompilation — received request to delete compilation");
        service.removeCompilation(id);
    }
}
