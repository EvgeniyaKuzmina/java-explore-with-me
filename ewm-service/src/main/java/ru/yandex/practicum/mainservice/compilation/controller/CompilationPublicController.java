package ru.yandex.practicum.mainservice.compilation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.mainservice.compilation.CompilationService;
import ru.yandex.practicum.mainservice.compilation.CompilationServiceImpl;
import ru.yandex.practicum.mainservice.compilation.dto.CompilationDto;
import ru.yandex.practicum.mainservice.compilation.mapper.CompilationMapper;
import ru.yandex.practicum.mainservice.compilation.model.Compilation;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.Collection;

/**
 * класс контроллер для работы с публичным API подборками событий
 */
@RestController
@RequestMapping(path = "/compilations")
@Slf4j
public class CompilationPublicController {

    private static final String FROM = "0";
    private static final String SIZE = "10";
    private final CompilationService service;

    @Autowired
    public CompilationPublicController(CompilationServiceImpl service) {
        this.service = service;
    }

    @GetMapping(value = {"/{id}"})
    public CompilationDto getCompilationById(@PathVariable Long id) {
        log.info("CompilationPublicController: getCompilationById — получен запрос на получение подборки событий по id");
        Compilation compilation = service.getCompilationById(id);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @GetMapping
    public Collection<CompilationDto> getAllCompilations(@RequestParam(required = false) Boolean pinned,
                                                         @RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
                                                         @RequestParam(defaultValue = SIZE) @Positive Integer size) {
        log.info("CompilationPublicController: getAllCompilations — получен запрос на получение списка всех подборок");
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        Collection<CompilationDto> allCompilationDto = new ArrayList<>();
        Collection<Compilation> allCompilation = pinned == null
                ? service.getAllCompilations(pageable)
                : service.getAllCompilationsByPinned(pinned, pageable);

        allCompilation.forEach(c -> allCompilationDto.add(CompilationMapper.toCompilationDto(c)));
        return allCompilationDto;
    }
}
