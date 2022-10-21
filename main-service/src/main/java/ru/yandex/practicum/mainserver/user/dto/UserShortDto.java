package ru.yandex.practicum.mainserver.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserShortDto {
    @Id
    @NotNull
    private Long id;
    @NotNull
    private String name;
}
