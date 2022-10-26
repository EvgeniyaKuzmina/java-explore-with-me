package ru.yandex.practicum.mainservice.compilation.dto;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CompilationUpdDto {

    private Long eventId;
    private Boolean pinned;
    private String title;
}
