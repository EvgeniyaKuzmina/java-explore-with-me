package ru.yandex.practicum.mainserver.user;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.mainserver.user.dto.NewUserDto;
import ru.yandex.practicum.mainserver.user.dto.UserDto;
import ru.yandex.practicum.mainserver.user.model.User;

import java.util.Collection;

/**
 * класс описывающий методы для работы с пользователем
 */


public interface UserService {

    // создание пользователя
    User createUser(NewUserDto userDto);

    //обновление пользователя
    User updateUser(UserDto userDto, Long id);

    // удаление пользователя по id
    void removeUser(Long id);

    // получение списка всех пользователей
    Collection<User> getAllUsers(Pageable pageable);

    // получение пользователя по id
    User getUserById(Long id);
}
