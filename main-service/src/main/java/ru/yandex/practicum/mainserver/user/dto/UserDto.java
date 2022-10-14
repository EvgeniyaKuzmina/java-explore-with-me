package ru.yandex.practicum.mainserver.user.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * класс DTO для работы с пользователем
 */

@Data
@Builder
public class UserDto {

    @Id
    private Long id;
    @NotNull
    private String name;
    @Email
    @NotNull
    private String email;
}
