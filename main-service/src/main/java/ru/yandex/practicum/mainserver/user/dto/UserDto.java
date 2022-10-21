package ru.yandex.practicum.mainserver.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.annotation.Id;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    @Id
    private Long id;
    @NotNull
    private String name;
    @Email
    @NotNull
    private String email;
}
