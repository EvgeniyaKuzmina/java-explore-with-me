package ru.yandex.practicum.mainservice.user;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.mainservice.user.model.User;

import java.util.Collection;

/**
 * класс описывающий методы для работы с пользователем
 */
public interface UserService {

    /**
     * создание пользователя
     */
    User createUser(User user);

    /**
     * обновление пользователя
     */
    User updateUser(User user, Long id);

    /**
     * удаление пользователя по eventId
     */
    void removeUser(Long id);

    /**
     * получение списка всех пользователей
     */
    Collection<User> getAllUsers(Pageable pageable);

    /**
     * получение списка всех пользователей c указанными eventId
     */
    Collection<User> getAllUsersByIds(Collection<Long> ids, Pageable pageable);

    /**
     * получение пользователя по eventId
     */
    User getUserById(Long id);
}
