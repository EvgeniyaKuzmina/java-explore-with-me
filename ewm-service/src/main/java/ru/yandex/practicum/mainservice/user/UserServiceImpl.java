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
            log.info("UserServiceImpl: createUser — user was added {}.", user);
            return user;
        } catch (DataIntegrityViolationException e) {
            log.error("UserServiceImpl: createUser — user with email {} already exist", user.getEmail());
            throw new ConflictException(String.format("User with email %s already exist",
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
            log.info("UserServiceImpl: updateUser — user was update {}.", updUser);
            return updUser;
        } catch (DataIntegrityViolationException e) {
            log.error("UserServiceImpl: removeUser — user with email {} already exist", updUser.getEmail());
            throw new ConflictException(String.format("User with email %s already exist",
                    updUser.getEmail()));
        }
    }

    @Override
    public void removeUser(Long id) {
        getUserById(id); // проверка, что пользователь с указанным eventId есть
        repository.deleteById(id);
        log.info("UserServiceImpl: removeUser — user with id {} was deleted", id);
    }

    @Override
    public Collection<User> getAllUsers(Pageable pageable) {
        Collection<User> users = repository.findAll(pageable).toList();
        log.info("UserServiceImpl: getAllUsers — list of users was received");
        return users;
    }

    @Override
    public Collection<User> getAllUsersByIds(Collection<Long> ids, Pageable pageable) {
        Collection<User> users = repository.findByIdIn(ids, pageable).toList();
        log.info("UserServiceImpl: getAllUsersByIds — list of users with ids {} was received", ids);
        return users;
    }

    @Override
    public User getUserById(Long id) {
        Optional<User> userOpt = repository.findById(id);
        User user = userOpt.orElseThrow(() -> {
            log.warn("UserServiceImpl: getUserById — user with id {} not exist", id);
            throw new ObjectNotFountException("User with id " + id + " not exist");
        });

        log.info("UserServiceImpl: getUserById — user with id {} was received", id);
        return user;
    }
}
