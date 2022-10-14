package ru.yandex.practicum.mainserver.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * класс DTO для создания нового пользователя
 */

@Data
@Builder
public class NewUserDto {
    @NotNull
    private String name;
    @Email
    @NotNull
    private String email;
}
