package ru.yandex.practicum.mainserver.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.mainserver.exception.ConflictException;
import ru.yandex.practicum.mainserver.exception.ObjectNotFountException;
import ru.yandex.practicum.mainserver.user.dto.NewUserDto;
import ru.yandex.practicum.mainserver.user.dto.UserDto;
import ru.yandex.practicum.mainserver.user.mapper.UserMapper;
import ru.yandex.practicum.mainserver.user.model.User;
import org.springframework.data.domain.Pageable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * класс реализующий методы для работы с пользователем
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public User createUser(NewUserDto userDto) {
        User user = UserMapper.toUserFromNewUserDto(userDto);
        try {
            log.info("Добавлен пользователь {}.", user);
            return repository.save(user);
        } catch (DataIntegrityViolationException e) {
            log.error("Пользователь с таким email {} уже существует.", user.getEmail());
            throw new ConflictException(String.format("Пользователь с таким email %s уже существует.",
                    user.getEmail()));
        }
    }

    @Override
    public User updateUser(UserDto userDto, Long userId) {
        User updUser = getUserById(userId); // проверка, что пользователь с указанным id есть
        // обновляем данные
        Optional.ofNullable(userDto.getName()).ifPresent(updUser::setName);
        Optional.ofNullable(userDto.getEmail()).ifPresent(updUser::setEmail);

        try {
            log.info("Добавлен пользователь {}.", updUser);
            return repository.save(updUser);
        } catch (DataIntegrityViolationException e) {
            log.error("Пользователь с таким email {} уже существует.", updUser.getEmail());
            throw new ConflictException(String.format("Пользователь с таким email %s уже существует.",
                    updUser.getEmail()));
        }
    }

    @Override
    public void removeUser(Long id) {
        getUserById(id); // проверка, что пользователь с указанным id есть
        log.warn("Пользователя с указанным id {} удалён", id);
        repository.deleteById(id);

    }

    @Override
    public Collection<User> getAllUsers(Pageable pageable) {
        return repository.findAll(pageable).toList();
    }

    @Override
    public Collection<User> getAllUsersByIds(List<Long> ids, Pageable pageable) {
        return repository.findByIdIn(ids, pageable).toList();
    }

    @Override
    public User getUserById(Long id) {
        Optional<User> user = repository.findById(id);
        user.orElseThrow(() -> {
            log.warn("Пользователя с указанным id {} нет", id);
            return new ObjectNotFountException("Пользователя с указанным id " + id + " нет");
        });

        log.warn("Пользователь с указанным id {} получен", id);
        return user.get();
    }
}
