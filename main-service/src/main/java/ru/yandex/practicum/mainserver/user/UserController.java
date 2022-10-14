package ru.yandex.practicum.mainserver.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.mainserver.exception.ArgumentNotValidException;
import ru.yandex.practicum.mainserver.user.dto.NewUserDto;
import ru.yandex.practicum.mainserver.user.dto.UserDto;
import ru.yandex.practicum.mainserver.user.mapper.UserMapper;
import ru.yandex.practicum.mainserver.user.model.User;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * класс контроллер для работы с пользователями
 */

@RestController
@RequestMapping(path = "/admin/users")
@Slf4j
public class UserController {

    private static final String FROM = "0";
    private static final String SIZE = "10";
    private final UserService userServiceImpl;

    @Autowired
    public UserController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    // создание пользователя
    @PostMapping
    public UserDto createUser(@Valid @RequestBody NewUserDto newUserDto) {
        if (newUserDto.getEmail() == null || newUserDto.getName() == null) {
            log.warn("UserController.createUser: Не указан email пользователя или имя пользователя");
            throw new ArgumentNotValidException("Не указан email пользователя или имя пользователя"); // сделать 403 ошибку
        }
        return UserMapper.toUserDto(userServiceImpl.createUser(newUserDto));
    }

    // обновление пользователя
    @PatchMapping(value = {"/{id}"})
    public UserDto updateUser(@Valid @RequestBody UserDto userDto, @PathVariable Long id) {
        User user = userServiceImpl.updateUser(userDto, id);
        return UserMapper.toUserDto(user);
    }

    // удаление пользователя по id
    @DeleteMapping(value = {"/{id}"})
    public void removeUser(@PathVariable Long id) {
        userServiceImpl.removeUser(id);
    }

    // получение пользователя по Id
    @GetMapping(value = {"/{id}"})
    public UserDto getUserById(@PathVariable Long id) {
        User user = userServiceImpl.getUserById(id);
        return UserMapper.toUserDto(user);
    }

    // получение списка всех пользователей
    @GetMapping
    public Collection<UserDto> getAllUsers(@RequestParam(required = false) List<Long> ids,
                                           @RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
                                           @RequestParam(defaultValue = SIZE) @Positive Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        Collection<UserDto> allUsersDto = new ArrayList<>();

        if (!ids.isEmpty()) {
            Collection<User> allUsersByIds = new ArrayList<>();
            ids.forEach(id -> allUsersByIds.add(userServiceImpl.getUserById(id)));
            allUsersByIds.forEach(u -> allUsersDto.add(UserMapper.toUserDto(u)));
            return allUsersDto;
        }

        userServiceImpl.getAllUsers(pageable).forEach(u -> allUsersDto.add(UserMapper.toUserDto(u)));
        return allUsersDto;
    }
}
