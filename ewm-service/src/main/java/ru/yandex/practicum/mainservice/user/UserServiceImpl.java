package ru.yandex.practicum.mainservice.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.mainservice.exception.ConflictException;
import ru.yandex.practicum.mainservice.exception.ObjectNotFountException;
import ru.yandex.practicum.mainservice.user.model.User;

import java.util.Collection;
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
    public User createUser(User user) {
        try {
            user =  repository.save(user);
            log.info("UserServiceImpl: createUser —Добавлен пользователь {}.", user);
            return user;
        } catch (DataIntegrityViolationException e) {
            log.error("UserServiceImpl: createUser — Пользователь с таким email {} уже существует.", user.getEmail());
            throw new ConflictException(String.format("Пользователь с таким email %s уже существует.",
                    user.getEmail()));
        }
    }

    @Override
    public User updateUser(User user, Long userId) {
        User updUser = getUserById(userId);

        Optional.ofNullable(user.getName()).ifPresent(updUser::setName);
        Optional.ofNullable(user.getEmail()).ifPresent(updUser::setEmail);

        try {
            updUser = repository.save(updUser);
            log.info("UserServiceImpl: updateUser — пользователь обновлён {}.", updUser);
            return updUser;
        } catch (DataIntegrityViolationException e) {
            log.error("UserServiceImpl: removeUser — Пользователь с таким email {} уже существует.", updUser.getEmail());
            throw new ConflictException(String.format("Пользователь с таким email %s уже существует.",
                    updUser.getEmail()));
        }
    }

    @Override
    public void removeUser(Long id) {
        getUserById(id); // проверка, что пользователь с указанным eventId есть
        repository.deleteById(id);
        log.info("UserServiceImpl: removeUser — Пользователя с указанным eventId {} удалён", id);
    }

    @Override
    public Collection<User> getAllUsers(Pageable pageable) {
        Collection<User> users = repository.findAll(pageable).toList();
        log.info("UserServiceImpl: getAllUsers — список пользователей получен");
        return users;
    }

    @Override
    public Collection<User> getAllUsersByIds(Collection<Long> ids, Pageable pageable) {
        Collection<User> users = repository.findByIdIn(ids, pageable).toList();
        log.info("UserServiceImpl: getAllUsersByIds — список пользователей по указанным id получен");
        return users;
    }

    @Override
    public User getUserById(Long id) {
        Optional<User> userOpt = repository.findById(id);
        User user = userOpt.orElseThrow(() -> {
            log.warn("UserServiceImpl: getUserById —Пользователя с указанным id {} нет", id);
            throw new ObjectNotFountException("Пользователя с указанным id " + id + " нет");
        });

        log.info("UserServiceImpl: getUserById — Пользователь с указанным id {} получен", id);
        return user;
    }
}
