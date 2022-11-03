package ru.yandex.practicum.mainservice.user.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

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
    @Length(max = 300)
    private  String name;
    @Email
    @NotNull
    @Length(max = 500)
    private String email;
}
