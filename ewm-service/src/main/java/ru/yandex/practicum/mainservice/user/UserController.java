package ru.yandex.practicum.mainservice.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.mainservice.user.dto.NewUserDto;
import ru.yandex.practicum.mainservice.user.dto.UserDto;
import ru.yandex.practicum.mainservice.user.mapper.UserMapper;
import ru.yandex.practicum.mainservice.user.model.User;

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
    private final UserService service;

    @Autowired
    public UserController(UserServiceImpl service) {
        this.service = service;
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody NewUserDto newUserDto) {
        log.info("UserController: createUser — получен запрос на создание пользователя");
        User user = UserMapper.toUserFromNewUserDto(newUserDto);
        return UserMapper.toUserDto(service.createUser(user));
    }

    @PatchMapping(value = {"/{id}"})
    public UserDto updateUser(@Valid @RequestBody UserDto userDto, @PathVariable Long id) {
        log.info("UserController: updateUser — получен запрос на обновление пользователя");
        User user = UserMapper.toUser(userDto);
        user = service.updateUser(user, id);
        return UserMapper.toUserDto(user);
    }

    @DeleteMapping(value = {"/{id}"})
    public void removeUser(@PathVariable Long id) {
        log.info("UserController: removeUser — получен запрос на удаление пользователя");
        service.removeUser(id);
    }

    @GetMapping(value = {"/{id}"})
    public UserDto getUserById(@PathVariable Long id) {
        log.info("UserController: getUserById — получен запрос на получение пользователя по id");
        User user = service.getUserById(null);
        return UserMapper.toUserDto(user);
    }

    @GetMapping
    public Collection<UserDto> getAllUsers(@RequestParam(required = false) List<Long> ids,
                                           @RequestParam(defaultValue = FROM) @PositiveOrZero Integer from,
                                           @RequestParam(defaultValue = SIZE) @Positive Integer size) {
        log.info("UserController: getAllUsers — получен запрос на списка всех пользователей");
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        Collection<UserDto> allUsersDto = new ArrayList<>();

        if (ids != null) {
            Collection<User> allUsersByIds = service.getAllUsersByIds(ids, pageable);
            allUsersByIds.forEach(u -> allUsersDto.add(UserMapper.toUserDto(u)));
            return allUsersDto;
        }

        service.getAllUsers(pageable).forEach(u -> allUsersDto.add(UserMapper.toUserDto(u)));
        return allUsersDto;
    }
}
