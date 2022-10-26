package ru.yandex.practicum.mainservice.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * класс DTO для создания нового пользователя
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NewUserDto {
    @NotNull
    private  String name;
    @Email
    @NotNull
    private String email;
}
