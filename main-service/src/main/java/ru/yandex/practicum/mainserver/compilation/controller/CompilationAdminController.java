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

import javax.validation.Valid;

/**
 * класс контроллер для работы с API подборками событий
 */

@RestController
@RequestMapping(path = "/admin/compilations")
@Slf4j
public class CompilationAdminController {

    private final CompilationService service;

    @Autowired
    public CompilationAdminController(CompilationServiceImpl service) {
        this.service = service;
    }

    // создание подборки
    @PostMapping
    public CompilationDto createCompilation(@Valid @RequestBody NewCompilationDto compilationDto) {
        return CompilationMapper.toCompilationDto(service.createCompilation(compilationDto));
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

    // удаление пользователя по id
    @DeleteMapping(value = {"/{id}"})
    public void removeUser(@PathVariable Long id) {
        service.removeCompilation(id);
    }

    // получение пользователя по Id


}
