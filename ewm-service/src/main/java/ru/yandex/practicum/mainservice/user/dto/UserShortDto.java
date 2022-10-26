package ru.yandex.practicum.mainservice.user.dto;

import lombok.*;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;

/**
 * класс DTO для краткой информации о пользователе
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserShortDto {
    @Id
    @NotNull
    private Long id;
    @NotNull
    private String name;
}
