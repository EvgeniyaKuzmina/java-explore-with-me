package ru.yandex.practicum.mainserver.user.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;

/**
 * класс DTO для краткой информации о пользователе
 */

@Data
@Builder
public class UserShortDto {
    @Id
    @NotNull
    private Long id;
    @NotNull
    private String name;
}
