package ru.yandex.practicum.mainserver.compilation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.mainserver.compilation.CompilationService;
import ru.yandex.practicum.mainserver.compilation.CompilationServiceImpl;
import ru.yandex.practicum.mainserver.compilation.dto.CompilationDto;
import ru.yandex.practicum.mainserver.compilation.mapper.CompilationMapper;
import ru.yandex.practicum.mainserver.compilation.model.Compilation;

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

    // получение подборки по Id
    @GetMapping(value = {"/{id}"})
    public CompilationDto getCompilationById(@PathVariable Long id) {
        Compilation compilation = service.getCompilationById(id);
        return CompilationMapper.toCompilationDto(compilation);
    }

    // получение списка всех пользователей
    @GetMapping
    public Collection<CompilationDto> getAllCompilations(@RequestParam(required = false) Boolean pinned,
                                                         @RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
                                                         @RequestParam(defaultValue = SIZE) @Positive Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        Collection<CompilationDto> allCompilationDto = new ArrayList<>();
        Collection<Compilation> allCompilation;
        if (pinned == null) {
            allCompilation = service.getAllCompilations(pageable);
        } else {
            allCompilation = service.getAllCompilationsWithTitle(pinned, pageable);
        }

        allCompilation.forEach(c -> allCompilationDto.add(CompilationMapper.toCompilationDto(c)));
        return allCompilationDto;
    }
}
