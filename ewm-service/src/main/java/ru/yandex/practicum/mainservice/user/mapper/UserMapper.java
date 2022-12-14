package ru.yandex.practicum.mainservice.user.mapper;

import ru.yandex.practicum.mainservice.user.dto.NewUserDto;
import ru.yandex.practicum.mainservice.user.dto.UserDto;
import ru.yandex.practicum.mainservice.user.dto.UserShortDto;
import ru.yandex.practicum.mainservice.user.model.User;

/**
 * класс преобразовывающий сущность пользователя в Dto и обратно
 */
public class UserMapper {

    private UserMapper() {
    }

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public static UserShortDto toUserShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public static User toUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }

    public static User toUserFromNewUserDto(NewUserDto userDto) {
        return User.builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }
}
