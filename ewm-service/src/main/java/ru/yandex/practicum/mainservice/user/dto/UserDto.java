package ru.yandex.practicum.mainservice.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * класс DTO для работы с пользователем
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDto {

    private Long id;
    @NotNull
    private String name;
    @Email
    @NotNull
    private String email;
}
