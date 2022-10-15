package ru.yandex.practicum.mainserver.compilation.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class CompilationUpdDto {

    private Long eventId;
    private Boolean pinned;
    private String title;
}
